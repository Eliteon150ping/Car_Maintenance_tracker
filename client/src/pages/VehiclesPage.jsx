import PageHeader from "../components/PageHeader";
import VehicleCard from "../components/VehicleCard";

function VehiclesPage() {

    return (
        <div>
            <PageHeader
                title="My Garage"
                description="View your entire vehicle catalogue here"
            />

            <VehicleCard 
            brand="Toyota"
            model="Corolla RXI"
            year={2001}
            mileage={240000}
            />

            <VehicleCard 
            brand="Toyota"
            model="Corolla"
            year={2004}
            mileage={283000}
            />

        </div>
    );
}

export default VehiclesPage;