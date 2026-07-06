import { Link } from "react-router-dom";

function VehicleCard({ id, brand, model, year, mileage }) {

    return (
        <Link to={`/vehicles/${id}`}>
            <div className="vehicle-card">
                <h3>{year} {brand} {model}</h3>
                <p>Mileage: {mileage} km</p>
            </div>
        </Link>
    );
}

export default VehicleCard;