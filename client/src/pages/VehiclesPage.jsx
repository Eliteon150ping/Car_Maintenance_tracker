import PageHeader from "../components/PageHeader";
import VehicleCard from "../components/VehicleCard";
import { useState, useEffect } from "react";
import { getAllVehicles } from "../api/vehicleApi.js";

function VehiclesPage() {

    const [vehicles, setVehicles] = useState([]);  // We use useState and set it to an array cause the values
                                                   // coming through the VehicleApi.js can change while the app is
                                                   // runing, in this case we're expecting an array of vehicle
                                                   // objects to come in
    
    useEffect(() => {                              

        async function loadVehicles() {            // we put it inside the useEffect because the goal is to get the 
                                                   // data to render only after the component has fully rendered and 
                                                   // with useState the component is automatically re-rendered once 
                                                   // react detects a change while the component is loaded as it
                                                   // returns a promise
            const data = await getAllVehicles();
            setVehicles(data)
        }

        loadVehicles();                            // Call the async function 

    },[])                                          // [] is used cause it can only work with the .map(...) 

    return (
        <div>
            <PageHeader
                title="My Garage"
                description="View your entire vehicle catalogue here"
            />

            {vehicles.map(vehicle =>(
                <VehicleCard
                key={vehicle.id}
                id={vehicle.id}
                brand={vehicle.brand}
                model={vehicle.model}
                year={vehicle.year}
                currentMileage={vehicle.currentMileage}
                />
            ))}

        </div>
    );
}

export default VehiclesPage;