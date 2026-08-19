import { useState } from "react";

function CarForm() {

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

    async function handleSubmit(even) {
        event.preventDefault();

        try {

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

            <button type="submit">Add Car</button>
            <button type="button">Cancel</button>
        </form>
    );
}

export default CarForm;