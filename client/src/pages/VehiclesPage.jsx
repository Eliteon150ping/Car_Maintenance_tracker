import PageHeader from "../components/PageHeader";
import VehicleCard from "../components/VehicleCard";
import { useState, useEffect } from "react";
import { getAllVehicles } from "../api/vehicleApi.js";
import CarForm from "../components/CarForm.jsx";
import { useNavigate } from "react-router-dom";
import { deleteCar } from "../api/vehicleApi.js";
import ConfirmationModal from "../components/ConfirmationModal.jsx";

function VehiclesPage() {

    const [vehicles, setVehicles] = useState([]);  // We use useState and set it to an array cause the values
    // coming through the VehicleApi.js can change while the app is
    // runing, in this case we're expecting an array of vehicle
    // objects to come in

    const [showCarForm, setShowCarForm] = useState(false);
    const [editingCarForm, setEditingCarForm] = useState(null);
    const [errors, setErrors] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {

        // useEffect runs this function once when the component
        // initially mounts because the dependency array is empty.
        
        // loadVehicles() is called here so the garage is populated
        // with the user's vehicles when they first open the page.

        loadVehicles();                            // Call the async function 

    }, [])                                         // [] is to run this effect after the component's initial render, 
                                                   //    and don't rerun it when state/props change. 

    async function loadVehicles() {

        // This function is responsible for fetching the latest vehicle
        // data from the backend and updating the vehicles state.

        // It is kept outside useEffect because it needs to be reusable.
        // It is called when the page initially loads and again after a
        // vehicle is added, edited, or deleted so the vehicle list stays
        // up to date.

        const data = await getAllVehicles();
        setVehicles(data);

    }

    async function handleDeleteCar(id){
        try{
            await deleteCar(id);
            await loadVehicles();
        }catch(error){
            console.error("Error caught: " + error.message);
            setErrors(["Unable to delete car, please try again later"]);
        }
    }

    return (
        <div>
            <PageHeader
                title="My Garage"
                description="View your entire vehicle catalogue here"
            />

            {showCarForm ? <CarForm

               editingCarForm={editingCarForm}
               carId={editingCarForm?.id}

                onSave={() => {
                    loadVehicles();
                    setShowCarForm(false);
                }}

                onCancel={() => {
                    setEditingCarForm(null);
                    setShowCarForm(false);
                }}

            /> : <button onClick={() => {
                setEditingCarForm(null);
                setShowCarForm(true);
            }}>Add Car</button>}

            {vehicles.map(vehicle => (
                <VehicleCard
                    key={vehicle.id}
                    id={vehicle.id}
                    brand={vehicle.brand}
                    model={vehicle.model}
                    year={vehicle.year}
                    colour={vehicle.colour}
                    currentMileage={vehicle.currentMileage}
                    onEdit={() => {
                        navigate(`/vehicles/${vehicle.id}`, {
                            state: {editing: true}
                        }); 
                    }}

                    onDelete={() => {
                        handleDeleteCar(vehicle.id)
                    }}
                />
            ))}

            {errors.length > 0 && (
                <ul style={{ color: "red" }}>
                    {errors.map((error, index) => (
                        <li key={index}>{error}</li>
                    ))}
                </ul>
            )}

        </div>
    );
}

export default VehiclesPage;