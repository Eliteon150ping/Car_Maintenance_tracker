import { useState } from "react";
import { addCar } from "../api/vehicleApi";

function CarForm({onCancel, onSave}) {

    const [brand, setBrand] = useState("");
    const [model, setModel] = useState("");
    const [year, setYear] = useState("");
    const [colour, setColour] = useState("");
    const [currentMileage, setCurrentMileage] = useState("");

    const formData = {
        brand,
        model,
        year,
        colour,
        currentMileage
    };

    async function handleSubmit(event) {
        event.preventDefault();

        try {
            await addCar(formData);
            onSave();
        } catch (error) {
            console.error("Error caught: " + error.message);
        }

    }

    return (

        <form onSubmit={handleSubmit}>

            <h2 style={{ color: "black" }}>Add a new Car</h2>

            <label>Brand
                <input type="text"
                    placeholder="eg. Toyota"
                    name="brand"
                    value={brand}
                    onChange={(event) => setBrand(event.target.value)} />
            </label>

            <label>Model
                <input type="text"
                    placeholder="eg. Corolla"
                    name="model"
                    value={model}
                    onChange={(event) => setModel(event.target.value)} />
            </label>

            <label>Year
                <input type="number"
                    placeholder="eg. 2020"
                    name="year"
                    min="1886"
                    max="2099"
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

            <button type="submit" >Add Car</button>
            <button type="button" onClick={onCancel}>Cancel</button>
        </form>
    );
}

export default CarForm;