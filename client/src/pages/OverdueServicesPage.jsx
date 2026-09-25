import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate } from "react-router-dom";
import { getOverdueRecords, getServiceTypes } from "../api/vehicleDetailsApi";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { FaFilter } from "react-icons/fa";
import ServiceRecordFilter from "../components/ServiceRecordFilter";
import "../styles/OverdueServicesPage.css";

function OverdueServicesPage({showExtraDetails}) {

    const [serviceRecords, setServiceRecords] = useState([]);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedServiceTypes, setSelectedServiceTypes] = useState([]);
    const [selectedServiceCategories, setSelectedServiceCategories] = useState([]);
    const [filteredServiceRecords, setFilteredServiceRecords] = useState([]);
    const [serviceTypes, setServiceTypes] = useState([]);
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
        loadServiceTypes();
    }, []);

    async function loadRecords() {
        const data = await getOverdueRecords();
        setServiceRecords(data);
        setFilteredServiceRecords(data);
    }

    async function loadServiceTypes() {
        const data = await getServiceTypes();
        setServiceTypes(data);
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
    }

    function applyFilters() {
        let filteredRecords = serviceRecords;

        // Filter by service type
        if (selectedServiceTypes.length > 0) {
            filteredRecords = filteredRecords.filter(serviceRecord =>
                selectedServiceTypes.includes(serviceRecord.serviceType)
            );
        }

        // Filter by service category
        if (selectedServiceCategories.length > 0) {
            filteredRecords = filteredRecords.filter(serviceRecord => {
                const serviceType = serviceTypes.find(
                    type => type.value === serviceRecord.serviceType
                );

                return serviceType &&
                    selectedServiceCategories.includes(serviceType.serviceCategory);
            });
        }

        setFilteredServiceRecords(filteredRecords);
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

            {filteredServiceRecords.map(serviceRecord => (
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
                    isLatestRecord={true}
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

        </div>
    );
}

export default OverdueServicesPage;