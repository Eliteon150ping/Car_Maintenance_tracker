import { Link } from "react-router-dom";

function VehicleCard({ id, brand, model, year, currentMileage, onEdit, onDelete }) {

    return (
        <div>
            <Link to={`/vehicles/${id}`}>
                <div className="vehicle-card">
                    <h3>{year} {brand} {model}</h3>
                    <p>Mileage: {currentMileage.toLocaleString()} km</p>
                </div>
            </Link>
            <button type="edit" onClick={onEdit}>Edit</button>
            <button onClick={onDelete}>Delete</button>
        </div>
    );
}

export default VehicleCard;