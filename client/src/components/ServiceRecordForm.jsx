import { useEffect, useState } from "react";
import { addServiceRecord, editServiceRecord, getServiceTypes } from "../api/vehicleDetailsApi";

// Props come from VehicleDetailsPage
function ServiceRecordForm({ id, carId, onCancel, onSave, serviceRecord, vehicleMileage, latestServiceMileage , latestServiceDate}) {

    const [serviceDate, setServiceDate] = useState("");
    const [mileageAtService, setMileageAtService] = useState("");
    const [serviceTypes, setServiceTypes] = useState([]);
    const [serviceType, setServiceType] = useState("");
    const [description, setDescription] = useState("");
    const [cost, setCost] = useState("");
    const [errors, setErrors] = useState([]);

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

        async function loadTypes() {
            const data = await getServiceTypes();
            setServiceTypes(data);
        }

        loadTypes();

        if (serviceRecord != null) {
            setServiceDate(serviceRecord.serviceDate);
            setMileageAtService(serviceRecord.mileageAtService);
            setServiceType(serviceRecord.serviceType);
            setDescription(serviceRecord.description);
            setCost(serviceRecord.cost);
        } else {
            setServiceDate("");
            setMileageAtService("");
            setServiceType("");
            setDescription("");
            setCost("");
        }

    }, [serviceRecord]) // Whenever the serviceRecord prop changes, update the form fields.

    async function handleSubmit(event) {
        event.preventDefault();

        const vaildationErrors = [];
    
        if (!serviceType) {
            vaildationErrors.push("Please Select a Service type");
        }
        if (serviceType == "OTHER" && description.trim() == "") {
            vaildationErrors.push("Service description is required for service type: OTHER");
        }
        if (Number(cost) <= 0) {
            vaildationErrors.push("Cost cannot be negative or 0");
        }
        if (!serviceDate) {
            vaildationErrors.push("Please select a date for the service");
        }
        if (Number(mileageAtService) <= 0) {
            vaildationErrors.push("Mileage at service cannot be less than 0");
        }
        if (!mileageAtService) {
            vaildationErrors.push("Mileage at service cannot be empty");
        }
        if(Number(mileageAtService) < Number(latestServiceMileage)){
            vaildationErrors.push("Mileage cannot be lower than the last latest service mileage");
        }
        if(new Date(serviceDate) < new Date(latestServiceDate)){
            vaildationErrors.push("Service date cannot be before the latest service date");
        }
        if(new Date(serviceDate) > new Date()){
            vaildationErrors.push("Service date cannot be after the present day");
        }
        if (Number(mileageAtService) > vehicleMileage) {
            vaildationErrors.push("New service mileage cannot be higher than the vehicle's current mileage");
        }

        if(vaildationErrors.length > 0){
            setErrors(vaildationErrors);
            return;
        }

        setErrors([]);
        try {
            if (serviceRecord == null) {
                await addServiceRecord(carId, formData);
            } else {
                await editServiceRecord(carId, id, formData);
            }
            onSave();
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(["Unable to save service record"]);
        }
    }

    return (

        <form onSubmit={handleSubmit}>
            <h2 style={{ color: 'black' }}>{serviceRecord ? "Edit Service Record" : "Add Service Record"}</h2>
            <select value={serviceType}
                disabled={serviceRecord != null}
                onChange={(event) => setServiceType(event.target.value)} >
                <option value="">Select Service</option>
                {serviceTypes.map(type => (
                    <option key={type.value} value={type.value}>{type.displayName}</option>
                ))}
            </select>
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
                onChange={(event) => setCost(event.target.value)} />
            </label>
            {errors.length > 0 && (
                <ul style={{ color: "red" }}>
                    {errors.map((error, index) => (
                        <li key={index}>{error}</li>
                    ))}
                </ul>
            )}
            <button type="submit" >{serviceRecord ? "Save changes" : "Add"}</button>
            <button type="button" onClick={onCancel}>Cancel</button>
        </form>
    );
}

export default ServiceRecordForm;