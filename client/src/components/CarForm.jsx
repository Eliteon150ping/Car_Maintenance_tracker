import { useEffect, useState } from "react";
import { addCar, editCar } from "../api/vehicleApi";

function CarForm({ onCancel, onSave, editingCarForm, carId}) {

    const [brand, setBrand] = useState("");
    const [model, setModel] = useState("");
    const [year, setYear] = useState("");
    const [colour, setColour] = useState("");
    const [currentMileage, setCurrentMileage] = useState("");
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

    async function handleSubmit(event) {
        event.preventDefault();

        const validationErrors = [];

        function validateBrand(){
            if(brand.trim() == ""){
                validationErrors.push("Brand cannot be empty");
            }
        }
        
        function validateModel(){
            if(model.trim() == ""){
                validationErrors.push("Model cannot be empty");
            }
        }

        function validateYear(){
            if(!year){
                validationErrors.push("Year cannot be empty");
            }
        }

        function validateColour(){
            if(colour.trim() == ""){
                validationErrors.push("Colour cannot be empty");
            }
        }

        function validateCurrentMileage(){
            if(!currentMileage){
                validationErrors.push("Mileage cannot be empty");
            }
        }

        if(editingCarForm != null){

            validateColour();
            validateCurrentMileage();

        }else{

            validateBrand();
            validateModel();
            validateYear();
            validateColour();
            validateCurrentMileage();
        }

        if(validationErrors.length > 0){
            setErrors(validationErrors);
            return; 
        }

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

        <form onSubmit={handleSubmit}>

            <h2 style={{ color: "black" }}>{editingCarForm ? "Edit your car" : "Add a new car"}</h2>

            <label>Brand
                <input type="text"
                    placeholder="eg. Toyota"
                    name="brand"
                    disabled={editingCarForm != null}
                    value={brand}
                    onChange={(event) => setBrand(event.target.value)} />
            </label>

            <label>Model
                <input type="text"
                    placeholder="eg. Corolla"
                    name="model"
                    disabled={editingCarForm != null}
                    value={model}
                    onChange={(event) => setModel(event.target.value)} />
            </label>

            <label>Year
                <input type="number"
                    placeholder="eg. 2020"
                    name="year"
                    min="1886"
                    max="2099"
                    disabled={editingCarForm != null}
                    value={year}
                    onChange={(event) => setYear(event.target.value)} />
            </label>

            <label>Colour
                <input type="text"
                    placeholder="eg. White"
                    name="colour"
                    value={colour}
                    onChange={(event) => setColour(event.target.value)} />
            </label>

            <label>Current Mileage
                <input type="number"
                    placeholder="eg. 20,345"
                    name="currentMileage"
                    min="1"
                    value={currentMileage}
                    onChange={(event) => setCurrentMileage(event.target.value)} />
            </label>

            {errors.length > 0  && (
                <ul style={{color: "red"}}>
                    {errors.map((error, index) => (
                        <li key={index}>{error}</li>
                    ))}
                </ul>
            )}

            <button type="submit" >{editingCarForm ? "Save changes" : "Add car"}</button>
            <button type="button" onClick={onCancel}>Cancel</button>
        </form>
    );
}

export default CarForm;