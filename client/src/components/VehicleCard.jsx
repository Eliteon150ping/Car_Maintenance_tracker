import { useState } from "react";
import { Link } from "react-router-dom";
import ConfirmationModal from "./ConfirmationModal";

function VehicleCard({ id, brand, model, year, currentMileage, onEdit, onDelete }) {

    const [showConfirmationCard, setShowConfirmationCard] = useState(false);

    return (
        <div>
            <Link to={`/vehicles/${id}`}>
                <div className="vehicle-card">
                    <h3>{year} {brand} {model}</h3>
                    <p>Mileage: {currentMileage.toLocaleString()} km</p>
                </div>
            </Link>
            <button type="edit" onClick={onEdit}>Edit</button>
            <button onClick={() => setShowConfirmationCard(true)}>Delete</button>

            {showConfirmationCard && (
                <ConfirmationModal
                    title="Delete Car"
                    message="Sold your car? You can delete it from your garage"
                    confirmText="Delete Car"
                    cancelText="Cancel"
                    onConfirm={onDelete}
                    onCancel={() => setShowConfirmationCard(false)}
                />
            )}
        </div>
    );
}

export default VehicleCard;