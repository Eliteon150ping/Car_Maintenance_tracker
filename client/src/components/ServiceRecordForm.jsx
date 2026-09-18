import { useEffect, useState } from "react";
import { addServiceRecord, editServiceRecord, getServiceTypes, validateDuplicateRecord } from "../api/vehicleDetailsApi";
import { formatDate, formatServiceType } from "../utils/serviceFormatter";
import "../styles/ServiceRecordForm.css";
import ServiceTypeDropdown from "./ServiceTypeDropdown";
import ConfirmationModal from "./ConfirmationModal";

// Props come from VehicleDetailsPage
function ServiceRecordForm({ id, carId, onCancel, onSave, serviceRecord, vehicleMileage, latestServiceMileage,
    latestServiceDate, vehicleYear }) {

    const [serviceDate, setServiceDate] = useState("");
    const [mileageAtService, setMileageAtService] = useState("");
    const [serviceTypes, setServiceTypes] = useState([]);
    const [serviceType, setServiceType] = useState("");
    const [description, setDescription] = useState("");
    const [cost, setCost] = useState("");
    const [errors, setErrors] = useState({});
    const [shake, setShake] = useState(false);
    const [showSaveConfirmation, setShowSaveConfirmation] = useState(false);
    const [showUnsavedConfirmation, setShowUnsavedConfirmation] = useState(false);

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

    async function handleClickSave() {

        const validationErrors = {};

        function validateCost() {
            if (!cost) {
                validationErrors.cost = "Cost cannot be empty";

            } else if (Number(cost) <= 0) {
                validationErrors.cost = "Cost cannot be negative or 0";

            }
        }

        function validateDescription() {
            if (serviceType == "OTHER" && description.trim() == "") {
                validationErrors.description = "Service description is required for service type: OTHER";

            } else if (description.length > 500) {
                validationErrors.description = "Description cannot be more than 500 characters";
            }
        }

        function validateServiceType() {
            if (!serviceType) {
                validationErrors.serviceType = "Please Select a Service type";
            }
        }

        function validateServiceDate() {

            if (!serviceDate) {
                validationErrors.serviceDate = "Please select a date for the service";

            } else if (new Date(serviceDate).getFullYear() < vehicleYear) {
                validationErrors.serviceDate = "Service date cannot be before the car's year model: // " + vehicleYear;

            } else if (new Date(serviceDate) < new Date(latestServiceDate)) {
                validationErrors.serviceDate = "Service date cannot be before the latest service date: // " + formatDate(latestServiceDate);

            } else if (new Date(serviceDate) > new Date()) {
                validationErrors.serviceDate = "Service date cannot be after the present day: " + formatDate(new Date());

            }
        }

        function validateServiceMileage() {

            if (!mileageAtService) {
                validationErrors.mileageAtService = "Mileage at service cannot be empty";

            } else if (Number(mileageAtService) <= 0) {
                validationErrors.mileageAtService = "Mileage at service cannot be less than 0";

            } else if (Number(mileageAtService) < Number(latestServiceMileage)) {
                validationErrors.mileageAtService = `Mileage cannot be lower than the last latest service mileage: // ${latestServiceMileage.toLocaleString()} km`;

            } else if (Number(mileageAtService) > vehicleMileage) {
                validationErrors.mileageAtService = `New service mileage cannot be higher than the vehicle's current mileage // : ${vehicleMileage.toLocaleString()} km. Please update the vehicle's mileage first`;
            }
        }

        if (serviceRecord != null) {

            validateCost();
            validateDescription();

        } else {

            validateServiceType();
            validateDescription();
            validateCost();
            validateServiceDate();
            validateServiceMileage();
        }

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            triggerShake();
            return;
        }

        try {
            if (serviceRecord == null) {
                await validateDuplicateRecord(carId, formData.serviceType, formData.serviceDate, formData.mileageAtService);
            }
            setShowSaveConfirmation(true);
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(
                Object.keys(error.errors || {}).length > 0
                    ? error.errors
                    : { general: error.message }
            );
            triggerShake();
        }
    }

    function triggerShake() {
        setShake(true);
        setTimeout(() => {
            setShake(false);
        }, 400);
    }

    async function saveChanges() {
        setErrors({});
        try {
            if (serviceRecord != null) {
                await editServiceRecord(carId, id, formData);
            } else {
                await addServiceRecord(carId, formData);
            }
            onSave();
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(error.errors ? error.errors : { general: error.message });
            triggerShake();
        }
    }

    return (

        <form className="service-record-form">

            <div className="service-record-form-container">

                <h2 className="service-form-heading" style={{ color: 'black' }}>{serviceRecord ? "Edit Service Record" : "Add Service Record"}</h2>

                <label className="form-field">Service type
                    <ServiceTypeDropdown
                        serviceRecord={serviceRecord}
                        hasError={errors.serviceType}
                        shake={shake}
                        serviceTypes={serviceTypes}
                        serviceType={formatServiceType(serviceType)}
                        setServiceType={setServiceType}
                    />
                    {errors.serviceType && (
                        <span className="field-error">
                            {errors.serviceType}
                        </span>
                    )}
                </label>

                <label className="form-field">Description
                    <input type="text"
                        className={errors.description ? (shake ? `input-error input-shake` : "input-error") : ""}
                        name="description"
                        placeholder={serviceType != "OTHER" ? "(Optional) eg. Replaced brake pads" : "(Required) eg. Replaced CV Joints"}
                        value={description}
                        onChange={(event) => setDescription(event.target.value)} />

                    {errors.description && (
                        <span className="field-error">
                            {errors.description}
                        </span>
                    )}
                </label>

                <label className="form-field">Service Date
                    <input type="date"
                        className={errors.serviceDate ? (shake ? `input-error input-shake` : "input-error") : ""}
                        name="service-date"
                        value={serviceDate}
                        disabled={serviceRecord != null}
                        onChange={(event) => setServiceDate(event.target.value)} />

                    {errors.serviceDate && (
                        <span className="field-error">
                            {errors.serviceDate}
                        </span>
                    )}
                </label>

                <label className="form-field">Mileage At Service
                    <input type="number"
                        className={errors.mileageAtService ? (shake ? `input-error input-shake` : "input-error") : ""}
                        name="mileage-at-service"
                        placeholder="eg. 20,345 km"
                        value={mileageAtService}
                        disabled={serviceRecord != null}
                        onChange={(event) => setMileageAtService(event.target.value)} />

                    {errors.mileageAtService && (
                        <span className="field-error">
                            {errors.mileageAtService}
                        </span>
                    )}
                </label>

                <label className="form-field">Cost
                    <input type="number"
                        className={errors.cost ? (shake ? `input-error input-shake` : "input-error") : ""}
                        name="cost"
                        placeholder="eg. R200"
                        value={cost}
                        onChange={(event) => setCost(event.target.value)} />

                    {errors.cost && (
                        <span className="field-error">
                            {errors.cost}
                        </span>
                    )}
                </label>

                {errors.general && (
                    <span className="field-error">
                        {errors.general}
                    </span>
                )}

                <div className="form-buttons">
                    <button className="form-button" type="button" onClick={handleClickSave}>{serviceRecord ? "Save changes" : "Add"}</button>
                    <button className="form-button cancel" type="button" onClick={() => setShowUnsavedConfirmation(true)}>Cancel</button>
                </div>
            </div>

            {showSaveConfirmation && (
                <ConfirmationModal
                    title="Save changes?"
                    message="Are you sure you want to save your new changes?"
                    confirmText="Yes"
                    cancelText="No"
                    onConfirm={saveChanges}
                    onCancel={() => setShowSaveConfirmation(false)}
                />
            )}

            {showUnsavedConfirmation && (
                <ConfirmationModal
                    title="Cancel unsaved changes?"
                    message="Are you sure you want to cancel your unsaved changes?"
                    confirmText="Yes"
                    cancelText="No"
                    onConfirm={() => onCancel()}
                    onCancel={() => setShowUnsavedConfirmation(false)}
                />
            )}
        </form>
    );
}

export default ServiceRecordForm;