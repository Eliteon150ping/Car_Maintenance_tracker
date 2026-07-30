import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useParams } from "react-router-dom";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getServiceRecordsByCarId, getCarById } from "../api/vehicleDetailsApi";
import VehicleInformationCard from "../components/VehicleInformationCard";
import ServiceRecordForm from "../components/ServiceRecordForm";

function VehicleDetailsPage(){

    // {id} returns a object through destructuring
    const {id} = useParams(); // Reads the dynamic values from the current URL and returns them to your component.
    const [vehicle, setVehicle] = useState(null);
    const [serviceRecords, setServiceRecords] = useState([]);

    useEffect(() => {

        async function loadCar(){
            
            const data = await getCarById(id);
            setVehicle(data);

        }

        loadCar();

        async function loadServiceRecords(){
            
            const data = await getServiceRecordsByCarId(id);
            setServiceRecords(data)
        }

        loadServiceRecords();

    },[id])

    return(
        <div>
            <PageHeader 
            title="Vehicle Details"
            description="View your vehicle's service history and information"
            />

            {vehicle && (        // DO NOT USE .map() if you're expecting a singular object and not an array of
                                 // values
                <VehicleInformationCard 
                key={vehicle.id}
                id={vehicle.id}
                brand={vehicle.brand}
                model={vehicle.model}
                year={vehicle.year}
                colour={vehicle.colour}
                currentMileage={vehicle.currentMileage}
                />
            )}

            <ServiceRecordForm />

            {serviceRecords.map(serviceRecord =>(
                <VehicleDetailsCard 
                key={serviceRecord.id}
                id={serviceRecord.id}
                serviceDate={serviceRecord.serviceDate}
                mileageAtService={serviceRecord.mileageAtService}
                nextDueMileage={serviceRecord.nextDueMileage}
                nextDueDate={serviceRecord.nextDueDate}
                serviceType={serviceRecord.serviceType}
                cost={serviceRecord.cost}
                description={serviceRecord.description}
                remainingKM={serviceRecord.remainingKM}
                remainingDays={serviceRecord.remainingDays}
                />
            ))}
        </div>
    );
}

export default VehicleDetailsPage;