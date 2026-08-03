import { useEffect, useState } from "react";
import { addServiceRecord, editServiceRecord } from "../api/vehicleDetailsApi";

                            // Props come from VehicleDetailsPage
function ServiceRecordForm({ id, carId, onCancel, onSave, serviceRecord }) {

    const [serviceDate, setServiceDate] = useState("");
    const [mileageAtService, setMileageAtService] = useState("");
    const [serviceType, setServiceType] = useState("");
    const [description, setDescription] = useState("");
    const [cost, setCost] = useState("");

    const formData = {
        serviceDate,
        mileageAtService,
        serviceType,
        description,
        cost
    };

    // useState only uses its initial value the first time the component renders.
    // When the parent passes a different serviceRecord (for example when Edit is clicked),
    // useEffect updates the form state with the new values.
    useEffect(() => {

        if (serviceRecord != null) {
            setServiceDate(serviceRecord.serviceDate);
            setMileageAtService(serviceRecord.mileageAtService);
            setServiceType(serviceRecord.serviceType);
            setDescription(serviceRecord.description);
            setCost(serviceRecord.cost);
        } else{
            setServiceDate("");
            setMileageAtService("");
            setServiceType("");
            setDescription("");
            setCost("");
        }

    }, [serviceRecord]) // Whenever the serviceRecord prop changes, update the form fields.

    async function handleSubmit(event) {
        event.preventDefault();
        try {
            if (serviceRecord == null) {
                await addServiceRecord(carId, formData);
            } else {
                await editServiceRecord(carId, id, formData);
            }
            onSave();

        } catch (error) {
            console.error("Error caught: " + error.message);
        }
    }

    return (

        <form onSubmit={handleSubmit}>
            <h2 style={{ color: 'black' }}>{serviceRecord ? "Edit Service Record" : "Add Service Record"}</h2>
            <label>Service Type
                <input type="text" name="service-type"
                    value={serviceType}
                    disabled={serviceRecord != null}
                    onChange={(event) => setServiceType(event.target.value)} />
            </label>
            <label>Description
                <input type="text" name="description"
                    value={description}
                    onChange={(event) => setDescription(event.target.value)} />
            </label>
            <label>Service Date<input type="date" name="service-date"
                value={serviceDate}
                disabled={serviceRecord != null}
                onChange={(event) => setServiceDate(event.target.value)} />
            </label>
            <label>Mileage At Service<input type="number" name="mileage-at-service"
                value={mileageAtService}
                disabled={serviceRecord != null}
                onChange={(event) => setMileageAtService(event.target.value)} />
            </label>
            <label>Cost<input type="number" name="cost"
                value={cost}
                onChange={(event) => setCost(event.target.value)} /></label>
            <button type="submit" >{serviceRecord ? "Save changes": "Add"}</button>
            <button type="button" onClick={onCancel}>Cancel</button>
        </form>
    );
}

export default ServiceRecordForm;