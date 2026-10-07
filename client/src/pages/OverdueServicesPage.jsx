import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate } from "react-router-dom";
import { getOverdueRecordsPaginated } from "../api/vehicleDetailsApi";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { FaFilter } from "react-icons/fa";
import ServiceRecordFilter from "../components/ServiceRecordFilter";
import "../styles/OverdueServicesPage.css";

function OverdueServicesPage({ showExtraDetails }) {

    const [serviceRecords, setServiceRecords] = useState([]);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedServiceTypes, setSelectedServiceTypes] = useState([]);
    const [selectedServiceCategories, setSelectedServiceCategories] = useState([]);
    const [appliedServiceTypes, setAppliedServiceTypes] = useState([]);
    const [appliedServiceCategories, setAppliedServiceCategories] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize] = useState(6);
    const navigate = useNavigate();

    const serviceTypeOptions = [
        { value: "ENGINE_OIL_AND_FILTER", label: "Engine Oil & Filter" },
        { value: "AIR_FILTER", label: "Air Filter" },
        { value: "SPARK_PLUGS", label: "Spark Plugs" },
        { value: "SERPENTINE_BELT", label: "Serpentine Belt" },
        { value: "TIMING_BELT", label: "Timing Belt" },
        { value: "COOLANT_FLUSH", label: "Coolant Flush" },
        { value: "BATTERY", label: "Battery" },
        { value: "DIFFERENTIAL_OIL", label: "Differential Oil" },
        { value: "TRANSMISSION_FLUID", label: "Transmission Fluid" },
        { value: "TYRE_ROTATION", label: "Tyre Rotation" },
        { value: "BRAKE_PADS", label: "Brake Pads" },
        { value: "FUEL_FILTER", label: "Fuel Filter" },
        { value: "FUEL_INJECTOR_CLEANING", label: "Fuel Injector Cleaning" },
        { value: "OTHER", label: "OTHER" }
    ];

    const serviceCategoryOptions = [
        { value: "ENGINE", label: "Engine" },
        { value: "COOLING", label: "Cooling" },
        { value: "ELECTRICAL", label: "Electrical" },
        { value: "DRIVETRAIN", label: "Drivetrain" },
        { value: "Wheels_And_Suspension", label: "Wheels And Suspension" },
        { value: "BRAKING", label: "Braking System" },
        { value: "FUEL_DELIVERY", label: "Fuel Delivery" },
        { value: "OTHER", label: "OTHER" }
    ];

    useEffect(() => {
        loadRecords();
    }, [currentPage, appliedServiceTypes, appliedServiceCategories]);

    async function loadRecords() {
        const data = await getOverdueRecordsPaginated(currentPage, pageSize, appliedServiceTypes, appliedServiceCategories);
        setServiceRecords(data.content);
        setTotalPages(data.totalPages);
    }

    function handleFilterChange(e, selectedValues, setSelectedValues) {
        const value = e.target.value;

        if (e.target.checked) {
            setSelectedValues([
                ...selectedValues,
                value
            ]);
        } else {
            setSelectedValues(
                selectedValues.filter(
                    selectedValue => selectedValue !== value
                )
            );
        }
    }

    function clearFilters() {
        setSelectedServiceTypes([]);
        setSelectedServiceCategories([]);
        setAppliedServiceTypes([]);
        setAppliedServiceCategories([]);
        setCurrentPage(0);
    }

    function applyFilters() {
        setAppliedServiceTypes(selectedServiceTypes);
        setAppliedServiceCategories(selectedServiceCategories);
        setCurrentPage(0);
        setShowFilters(false);
    }

    return (
        <div className="service-overdue-page">

            <PageHeader
                title="Overdue services"
                description="Urgent services that need to be done to prevent damage to your cars"
            />

            <div className="service-overdue-actions">
                <button
                    type="button"
                    className="service-filter-button"
                    onClick={() => setShowFilters(!showFilters)}
                >
                    <FaFilter />
                </button>
            </div>

            <ServiceRecordFilter
                serviceTypeOptions={serviceTypeOptions}
                serviceCategoryOptions={serviceCategoryOptions}
                selectedServiceTypes={selectedServiceTypes}
                setSelectedServiceTypes={setSelectedServiceTypes}
                selectedServiceCategories={selectedServiceCategories}
                setSelectedServiceCategories={setSelectedServiceCategories}
                handleFilterChange={handleFilterChange}
                clearFilters={clearFilters}
                applyFilters={applyFilters}
                showFilters={showFilters}
            />

            <div className={`service-record-header ${showExtraDetails ? "without-car" : "with-car"}`}>
                <div>Car</div>
                <div>Service Date</div>
                <div>Mileage at service</div>
                <div>Service Type</div>
                <div>Description</div>
                <div>Cost</div>
                <div>Next due mileage</div>
                <div>Next due date</div>
                <div>Remaining KM</div>
                <div>Remaining Days</div>
                <div>Edit</div>
            </div>

            {serviceRecords.length === 0 && (
                <p className="not-available">No overdue services yet...</p>
            )}

            {serviceRecords.map(serviceRecord => (
                <VehicleDetailsCard
                    key={serviceRecord.id}
                    id={serviceRecord.car.id}

                    brand={serviceRecord.car.brand}
                    model={serviceRecord.car.model}
                    year={serviceRecord.car.year}

                    serviceDate={serviceRecord.serviceDate}
                    mileageAtService={serviceRecord.mileageAtService}
                    nextDueMileage={serviceRecord.nextDueMileage}
                    nextDueDate={serviceRecord.nextDueDate}
                    serviceType={serviceRecord.serviceType}
                    isLatestRecord={serviceRecord.latestRecord}
                    cost={serviceRecord.cost}
                    description={serviceRecord.description}
                    remainingKm={serviceRecord.remainingKm}
                    remainingDays={serviceRecord.remainingDays}
                    showExtraDetails={true}
                    highlightRemaining={true}

                    onEdit={() => {
                        navigate(`/vehicles/${serviceRecord.car.id}`, {
                            state: { editingServiceRecordId: serviceRecord.id }
                        });
                    }}
                />
            ))}

            {serviceRecords.length > 0 && (
                <div className="pagination-controls">

                    <button
                        className="pagination-button"
                        type="button"
                        onClick={() => setCurrentPage(currentPage - 1)}
                        disabled={currentPage === 0}
                    >Previous</button>

                    <p className="pagination-info">Page {currentPage + 1} of {totalPages}</p>

                    <button
                        className="pagination-button"
                        type="button"
                        onClick={() => setCurrentPage(currentPage + 1)}
                        disabled={(currentPage === totalPages - 1)}
                    >Next</button>
                </div>
            )}

        </div>
    );
}

export default OverdueServicesPage;