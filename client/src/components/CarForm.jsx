import { useEffect, useState } from "react";
import { addCar, editCar } from "../api/vehicleApi";
import "../styles/CarForm.css";
import ConfirmationModal from "./ConfirmationModal";

function CarForm({ onCancel, onSave, editingCarForm, carId }) {

    const [brand, setBrand] = useState("");
    const [model, setModel] = useState("");
    const [year, setYear] = useState("");
    const [colour, setColour] = useState("");
    const [currentMileage, setCurrentMileage] = useState("");
    const [showSaveConfrimation, setShowSaveConfirmation] = useState(false);
    const [showUnsavedConfirmation, setShowUnsavedConfirmation] = useState(false);
    const [errors, setErrors] = useState([]);

    const formData = {
        brand,
        model,
        year,
        colour,
        currentMileage
    };

    useEffect(() => {

        if (editingCarForm != null) {
            setBrand(editingCarForm.brand);
            setModel(editingCarForm.model);
            setYear(editingCarForm.year);
            setColour(editingCarForm.colour);
            setCurrentMileage(editingCarForm.currentMileage);
        } else {
            setBrand("");
            setModel("");
            setYear("");
            setColour("");
            setCurrentMileage("");
        }

    }, [editingCarForm])

    async function handleClickSave() {
        const validationErrors = [];

        function validateBrand() {
            if (brand.trim() == "") {
                validationErrors.push("Brand cannot be empty");
            }
        }

        function validateModel() {
            if (model.trim() == "") {
                validationErrors.push("Model cannot be empty");
            }
        }

        function validateYear() {
            if (!year) {
                validationErrors.push("Year cannot be empty");
            }
        }

        function validateColour() {
            if (colour.trim() == "") {
                validationErrors.push("Colour cannot be empty");
            }
        }

        function validateCurrentMileage() {
            if (!currentMileage) {
                validationErrors.push("Mileage cannot be empty");
            }
        }

        if (editingCarForm != null) {

            validateColour();
            validateCurrentMileage();

        } else {

            validateBrand();
            validateModel();
            validateYear();
            validateColour();
            validateCurrentMileage();
        }

        if (validationErrors.length > 0) {
            setErrors(validationErrors);
            return;
        }
        setShowSaveConfirmation(true);
    }

    async function saveChanges() {
        setErrors([]);
        try {
            if (editingCarForm != null) {
                await editCar(carId, formData);
            } else {
                await addCar(formData);
            }
            onSave();
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(error.errors?.length ? error.errors : [error.message]);
        }
    }

    return (
        <form className="car-form">

            <div className="form-container">

                <h2 className="car-form-heading" style={{ color: "black" }}>{editingCarForm ? "Edit your car" : "Add a new car"}</h2>

                <label className="form-field">Brand
                    <input type="text"
                        placeholder="eg. Toyota"
                        name="brand"
                        disabled={editingCarForm != null}
                        value={brand}
                        onChange={(event) => setBrand(event.target.value)} />
                </label>

                <label className="form-field">Model
                    <input type="text"
                        placeholder="eg. Corolla"
                        name="model"
                        disabled={editingCarForm != null}
                        value={model}
                        onChange={(event) => setModel(event.target.value)} />
                </label>

                <label className="form-field">Year
                    <input type="number"
                        placeholder="eg. 2020"
                        name="year"
                        min="1886"
                        max="2099"
                        disabled={editingCarForm != null}
                        value={year}
                        onChange={(event) => setYear(event.target.value)} />
                </label>

                <label className="form-field">Colour
                    <input type="text"
                        placeholder="eg. White"
                        name="colour"
                        value={colour}
                        onChange={(event) => setColour(event.target.value)} />
                </label>

                <label className="form-field">Current Mileage
                    <input type="number"
                        placeholder="eg. 20,345"
                        name="currentMileage"
                        min="1"
                        value={currentMileage}
                        onChange={(event) => setCurrentMileage(event.target.value)} />
                </label>

                {errors.length > 0 && (
                    <ul style={{ color: "red" }}>
                        {errors.map((error, index) => (
                            <li key={index}>{error}</li>
                        ))}
                    </ul>
                )}

                <div className="form-buttons">
                    <button className="form-button" type="button" onClick={handleClickSave}>{editingCarForm ? "Save changes" : "Add car"}</button>
                    <button className="form-button cancel" type="button" onClick={() => setShowUnsavedConfirmation(true)}>Cancel</button>
                </div>
            </div>

            {showSaveConfrimation && (
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
                    message="Are you sure you want to cancel any unsaved changes?"
                    confirmText="Yes"
                    cancelText="No"
                    onConfirm={() => onCancel()}
                    onCancel={() => setShowUnsavedConfirmation(false)}
                />
            )}
        </form>
    );
}

export default CarForm;