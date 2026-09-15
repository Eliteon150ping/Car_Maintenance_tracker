import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import ConfirmationModal from "./ConfirmationModal";
import "../styles/VehicleCard.css"

function VehicleCard({ id, brand, model, year, currentMileage, onEdit, onDelete }) {

    const [showConfirmationCard, setShowConfirmationCard] = useState(false);
    const [showMenu, setShowMenu] = useState(false);

    useEffect(() => {

        function handleClickOutside(event) {
            if (!event.target.closest(".vehicle-menu")) {
                setShowMenu(false);
            }
        }
        document.addEventListener("click", handleClickOutside);

        return () => {
            document.removeEventListener("click", handleClickOutside);
        };
    }, []);

    return (
        <div>
            <div className="main-vehicle-card">
                <Link className="vehicle-link" to={`/vehicles/${id}`}>
                    <div className="vehicle-card">
                        <img className="vehicle-image" src="/images/generic_car.png" alt="Generic car" />
                        <h3 className="vehicle-name">{year} {brand} {model}</h3>
                        <p className="vehicle-mileage">Mileage: {currentMileage.toLocaleString()} km</p>
                    </div>
                </Link>

                <div className="vehicle-menu">
                    <button className="vehicle-menu-button"
                        onClick={() => setShowMenu(!showMenu)}
                    >⋮</button>

                    {showMenu && (
                        <div className="vehicle-menu-options">
                            <button onClick={onEdit}>Edit</button>
                            <button className="delete-button" onClick={() => {
                                setShowMenu(false);
                                setShowConfirmationCard(true);
                            }}>Delete</button>
                        </div>
                    )}

                </div>
            </div>
            
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