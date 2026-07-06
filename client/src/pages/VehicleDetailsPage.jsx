import PageHeader from "../components/PageHeader";
import { useParams } from "react-router-dom";

function VehicleDetailsPage(){

    // {id} returns a object through destructuring
    const {id} = useParams(); // Reads the dynamic values from the current URL and returns them to your component.

    return(
        <div>
            <PageHeader 
            title="Vehicle Details"
            description="View your vehicle's service history and information"
            />
        </div>
    );
}

export default VehicleDetailsPage;