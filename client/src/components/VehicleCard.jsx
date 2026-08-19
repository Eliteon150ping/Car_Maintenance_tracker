import { Link } from "react-router-dom";

function VehicleCard({ id, brand, model, year, currentMileage }) {

    return (
        <Link to={`/vehicles/${id}`}>
            <div className="vehicle-card">
                <h3>{year} {brand} {model}</h3>
                <p>Mileage: {currentMileage.toLocaleString()} km</p>
                <button type="edit">Edit</button>
            </div>
        </Link>
    );
}

export default VehicleCard;