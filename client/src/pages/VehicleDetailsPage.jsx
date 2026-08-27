import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate, useParams } from "react-router-dom";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getServiceRecordsByCarId, getCarById } from "../api/vehicleDetailsApi";
import VehicleInformationCard from "../components/VehicleInformationCard";
import ServiceRecordForm from "../components/ServiceRecordForm";
import CarForm from "../components/CarForm";
import { useLocation } from "react-router-dom";

function VehicleDetailsPage() {

    // {id} returns a object through destructuring
    const { id } = useParams(); // Reads the dynamic values from the current URL and returns them to your component.
    const location = useLocation();
    const navigate = useNavigate();
    const [vehicle, setVehicle] = useState(null);
    const [serviceRecords, setServiceRecords] = useState([]);
    const [showServiceForm, setShowServiceForm] = useState(false);
    const [editingServiceRecord, setEditingServiceRecord] = useState(null);
    const [showCarForm, setShowCarForm] = useState(location.state?.editing === true);
    const editingServiceRecordId = location.state?.editingServiceRecordId;

    useEffect(() => {

        loadCar();
        loadServiceRecords();

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

        if(editingServiceRecordId){

            const recordToEdit = data.find(   // .find() loops through the array and returns the first object
                                              // for which the condition evaluates to true. If it can't find
                                              // anything that satisfies the condition, it returns undefined
                                              // instead of false.
                serviceRecord => serviceRecord.id === editingServiceRecordId
            );

            if(recordToEdit){
                setEditingServiceRecord(recordToEdit);
                setShowServiceForm(true);
            }

            navigate(location.pathname, {
                replace: true, state: null
            });
        }
    }

    // Find the latest record of each service type by comparing the id of the record and storing it if it
    // doesn't exist else ignore it
    const latestRecordsByServiceType = {};
    serviceRecords.forEach(serviceRecord => {
        if(!latestRecordsByServiceType[serviceRecord.serviceType]){
            latestRecordsByServiceType[serviceRecord.serviceType] = serviceRecord.id
        }
    });

    return (
        <div>
            <PageHeader
                title="Vehicle Details"
                description="View your vehicle's service history and information"
            />

            {/* DO NOT USE .map() if you're expecting a singular object and not an array of values */}
            {vehicle && (
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

            {showServiceForm ? <ServiceRecordForm
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
                }} /> : <button onClick={() => {
                    setEditingServiceRecord(null),
                        setShowServiceForm(true)
                }}>Add service record</button>}

            {serviceRecords.map(serviceRecord => (
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