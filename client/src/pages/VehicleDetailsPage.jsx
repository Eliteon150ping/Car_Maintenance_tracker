import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useParams } from "react-router-dom";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getAllServiceRecordsForACar } from "../api/vehicleDetailsApi";

function VehicleDetailsPage(){

    // {id} returns a object through destructuring
    const {id} = useParams(); // Reads the dynamic values from the current URL and returns them to your component.
    const [vehicle, setVehicle] = useState([]);

    useEffect(() => {

        async function loadServiceRecords(){
            
            const data = await getAllServiceRecordsForACar(id);
            setVehicle(data)
        }

        loadServiceRecords();

    },[])

    return(
        <div>
            <PageHeader 
            title="Vehicle Details"
            description="View your vehicle's service history and information"
            />
            <p>Vehicle ID: {id}</p>

            {vehicle.map(vehicleDetails =>(
                <VehicleDetailsCard 
                key={vehicleDetails.id}
                id={vehicleDetails.id}
                serviceDate={vehicleDetails.serviceDate}
                mileageAtService={vehicleDetails.mileageAtService}
                nextDueMileage={vehicleDetails.nextDueMileage}
                />
            ))}
        </div>
    );
}

export default VehicleDetailsPage;