import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate, useParams } from "react-router-dom";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getServiceRecordsByCarId, getCarById, getServiceTypes } from "../api/vehicleDetailsApi";
import VehicleInformationCard from "../components/VehicleInformationCard";
import ServiceRecordForm from "../components/ServiceRecordForm";
import CarForm from "../components/CarForm";
import { useLocation } from "react-router-dom";
import "../styles/VehicleDetailsPage.css";
import { FaFilter } from "react-icons/fa";

function VehicleDetailsPage({ showExtraDetails }) {

    // {id} returns a object through destructuring
    const { id } = useParams(); // Reads the dynamic values from the current URL and returns them to your component.
    const location = useLocation();
    const navigate = useNavigate();
    const [vehicle, setVehicle] = useState(null);
    const [serviceRecords, setServiceRecords] = useState([]);
    const [serviceTypes, setServiceTypes] = useState([]);
    const [filteredServiceRecords, setFilteredServiceRecords] = useState([]);
    const [showServiceForm, setShowServiceForm] = useState(false);
    const [editingServiceRecord, setEditingServiceRecord] = useState(null);
    const [showCarForm, setShowCarForm] = useState(location.state?.editing === true);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedServiceTypes, setSelectedServiceTypes] = useState([]);
    const [selectedServiceCategories, setSelectedServiceCategories] = useState([]);
    const editingServiceRecordId = location.state?.editingServiceRecordId;

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

        loadCar();
        loadServiceRecords();
        loadServiceTypes();

    }, [id])

    async function loadCar() {

        const data = await getCarById(id);
        setVehicle(data);

        if (location.state?.editing === true) {
            setShowCarForm(true);

            navigate(location.pathname, {  // Opens the edit form when arriving from the Garage,
                replace: true, state: null // then clears the navigation state so the edit form
                // does not reopen after refreshing the page.            
            });
        }

    }

    async function loadServiceRecords() {

        const data = await getServiceRecordsByCarId(id);
        setServiceRecords(data);
        setFilteredServiceRecords(data);

        if (editingServiceRecordId) {

            const recordToEdit = data.find(   // .find() loops through the array and returns the first object
                // for which the condition evaluates to true. If it can't find
                // anything that satisfies the condition, it returns undefined
                // instead of false.
                serviceRecord => serviceRecord.id === editingServiceRecordId
            );

            if (recordToEdit) {
                setEditingServiceRecord(recordToEdit);
                setShowServiceForm(true);
            }

            navigate(location.pathname, {
                replace: true, state: null
            });
        }
    }

    async function loadServiceTypes() {

        const data = await getServiceTypes();
        setServiceTypes(data);
    }

    function handleFilterChange(e, selectedValues, setSelectedValues) {
        const value = e.target.value;

        // Take the existing selected checkbox values and spread them into a new array, then add the newly 
        // selected value.
        if (e.target.checked) {
            setSelectedValues([
                ...selectedValues,
                value
            ]);
        } else { // If the checkbox is unchecked, remove its value from the selected values array.
            setSelectedValues(
                selectedValues.filter(
                    selectedValue => selectedValue !== value
                )
            );
        }
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

                return serviceType && selectedServiceCategories.includes(serviceType.serviceCategory)
            });
        }

        setFilteredServiceRecords(filteredRecords);
        setShowFilters(false);
    }

    // Find the latest record of each service type by comparing the id of the record and storing it if it
    // doesn't exist else ignore it
    const latestRecordsByServiceType = {};
    serviceRecords.forEach(serviceRecord => {
        if (!latestRecordsByServiceType[serviceRecord.serviceType]) {
            latestRecordsByServiceType[serviceRecord.serviceType] = serviceRecord.id
        }
    });

    return (
        <div className="vehicle-details-page">
            <PageHeader
                title="Vehicle Details"
                description="View your vehicle's service history and information"
            />

            {/* DO NOT USE .map() if you're expecting a singular object and not an array of values */}
            {!showServiceForm && !showCarForm && vehicle && (
                <VehicleInformationCard
                    key={vehicle.id}
                    id={vehicle.id}
                    brand={vehicle.brand}
                    model={vehicle.model}
                    year={vehicle.year}
                    colour={vehicle.colour}
                    currentMileage={vehicle.currentMileage}

                    onEdit={() => {
                        setShowCarForm(true);
                    }}
                />
            )}

            {showCarForm && vehicle && (
                <CarForm

                    editingCarForm={vehicle}
                    carId={id}

                    onSave={() => {
                        loadCar();
                        loadServiceRecords();
                        setShowCarForm(false);
                    }}

                    onCancel={() => {
                        setShowCarForm(false);
                    }}

                />
            )}

            <div className="service-record-actions">

                {!showCarForm && (showServiceForm ? <ServiceRecordForm
                    // All the props here are passed into serviceRecord form

                    vehicleMileage={vehicle.currentMileage}
                    latestServiceMileage={serviceRecords[0]?.mileageAtService}
                    latestServiceDate={serviceRecords[0]?.serviceDate}
                    vehicleYear={vehicle.year}
                    // If editingServiceRecord is null, the form is being used to add a new record.
                    // If it contains a service record object, the form switches to edit mode.
                    serviceRecord={editingServiceRecord}
                    carId={id}

                    // use the service record's Id when editing
                    id={editingServiceRecord?.id}

                    // Cancel clears the current editing record and hides the form.
                    onCancel={() => {
                        setEditingServiceRecord(null),
                            setShowServiceForm(false)
                    }}

                    // After saving:
                    // 1. Reload the latest service records.
                    // 2. Clear the editing record.
                    // 3. Close the form.
                    onSave={() => {
                        loadServiceRecords();
                        setEditingServiceRecord(null);
                        setShowServiceForm(false);

                        // Show the Add button when the form is hidden.
                        // Clicking it clears any editing record and opens a blank form.
                    }} /> : <button className="add-service-record-button"
                        onClick={() => {
                            setEditingServiceRecord(null),
                                setShowServiceForm(true)
                        }}>Add service record</button>
                )}

                {!showServiceForm && !showCarForm && (
                    <button
                        type="button"
                        className="service-filter-button"
                        onClick={() => setShowFilters(!showFilters)}
                    >
                        <FaFilter/>
                    </button>
                )}
            </div>

            {!showServiceForm && !showCarForm && showFilters && (
                <div className="service-filter-panel">

                    <h3>Filter Service Records</h3>

                    <div className="service-filter-section">
                        <h4>Service Type</h4>

                        {serviceTypeOptions.map(option => (
                            <label key={option.value}>
                                <input type="checkbox"
                                    value={option.value}
                                    onChange={(e) => handleFilterChange(
                                        e,
                                        selectedServiceTypes,
                                        setSelectedServiceTypes
                                    )}
                                    checked={selectedServiceTypes.includes(option.value)}
                                />
                                {option.label}
                            </label>
                        ))}

                    </div>

                    <div className="service-filter-section">
                        <h4>Service Category</h4>

                        {serviceCategoryOptions.map(option => (
                            <label key={option.value}>
                                <input type="checkbox"
                                    value={option.value}
                                    onChange={(e) => handleFilterChange(
                                        e,
                                        selectedServiceCategories,
                                        setSelectedServiceCategories
                                    )}
                                    checked={selectedServiceCategories.includes(option.value)}
                                />
                                {option.label}
                            </label>
                        ))}

                    </div>

                    <div className="service-filter-actions">
                        <button type="button"
                            onClick={() => {
                                setSelectedServiceTypes([]);
                                setSelectedServiceCategories([]);
                                setFilteredServiceRecords(serviceRecords);
                            }}
                        >
                            Clear
                        </button>

                        <button type="button"
                            onClick={applyFilters}
                        >
                            Apply Filters
                        </button>
                    </div>

                </div>
            )}

            {!showServiceForm && !showCarForm && (
                <div className={`service-record-header ${showExtraDetails ? "with-car" : "without-car"}`}>
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
            )}

            {!showServiceForm && !showCarForm && filteredServiceRecords.map(serviceRecord => (
                <VehicleDetailsCard
                    key={serviceRecord.id}
                    id={serviceRecord.id}
                    serviceDate={serviceRecord.serviceDate}
                    mileageAtService={serviceRecord.mileageAtService}
                    nextDueMileage={serviceRecord.nextDueMileage}
                    nextDueDate={serviceRecord.nextDueDate}
                    serviceType={serviceRecord.serviceType}
                    isLatestRecord={latestRecordsByServiceType[serviceRecord.serviceType] === serviceRecord.id}
                    cost={serviceRecord.cost}
                    description={serviceRecord.description}
                    remainingKm={serviceRecord.remainingKm}
                    remainingDays={serviceRecord.remainingDays}
                    highlightRemaining={true}

                    // Store the selected service record in state and open the form.
                    // The form receives this object as a prop and fills the inputs using useEffect. 
                    onEdit={() => {
                        setEditingServiceRecord(serviceRecord);
                        setShowServiceForm(true);
                    }}
                />
            ))}
        </div>
    );
}

export default VehicleDetailsPage;