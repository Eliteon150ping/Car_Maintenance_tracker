import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useParams } from "react-router-dom";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getServiceRecordsByCarId, getCarById } from "../api/vehicleDetailsApi";
import VehicleInformationCard from "../components/VehicleInformationCard";
import ServiceRecordForm from "../components/ServiceRecordForm";

function VehicleDetailsPage() {

    // {id} returns a object through destructuring
    const { id } = useParams(); // Reads the dynamic values from the current URL and returns them to your component.
    const [vehicle, setVehicle] = useState(null);
    const [serviceRecords, setServiceRecords] = useState([]);
    const [showServiceForm, setShowServiceForm] = useState(false);
    const [editingServiceRecord, setEditingServiceRecord] = useState(null);

    useEffect(() => {

        async function loadCar() {

            const data = await getCarById(id);
            setVehicle(data);

        }

        loadCar();
        loadServiceRecords();

    }, [id])

    async function loadServiceRecords() {

        const data = await getServiceRecordsByCarId(id);
        setServiceRecords(data)
    }

    return (
        <div>
            <PageHeader
                title="Vehicle Details"
                description="View your vehicle's service history and information"
            />

            {vehicle && (        // DO NOT USE .map() if you're expecting a singular object and not an array of
                // values
                <VehicleInformationCard
                    key={vehicle.id}
                    id={vehicle.id}
                    brand={vehicle.brand}
                    model={vehicle.model}
                    year={vehicle.year}
                    colour={vehicle.colour}
                    currentMileage={vehicle.currentMileage}

                    // onEdit={() => {
                    //     setEditingCarForm(editingCarForm);
                    //     setShowCarForm(true);
                    // }}
                />
            )}


            {showServiceForm ? <ServiceRecordForm
                // All the props here are passed into serviceRecord form

                vehicleMileage={vehicle.currentMileage}
                latestServiceMileage={serviceRecords[0]?.mileageAtService}
                latestServiceDate={serviceRecords[0]?.serviceDate}
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
                    cost={serviceRecord.cost}
                    description={serviceRecord.description}
                    remainingKm={serviceRecord.remainingKm}
                    remainingDays={serviceRecord.remainingDays}

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