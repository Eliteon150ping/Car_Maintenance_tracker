import { formatRemainingKM, formatRemainingDays, formatDate, formatServiceType } from "../utils/serviceFormatter";
import "../styles/VehicleServiceHistoryCard.css";
import { Link } from "react-router-dom";


function VehicleDetailsCard({ serviceDate, mileageAtService, nextDueMileage, nextDueDate, serviceType, cost,
    description, remainingKm, remainingDays, onEdit, showExtraDetails, brand, model, year, highlightRemaining,
    isLatestRecord, id }) {

    return (

        <div className={`vehicle-details-card ${showExtraDetails ? "with-car" : "without-car"}`}>


            {showExtraDetails && (
                <div className="service-column">
                    <Link className="vehicle-link" to={`/vehicles/${id}`}>
                        <p>{year} {brand} {model}</p>
                    </Link>
                </div>
            )}

            <div className="service-column">
                <p>{formatDate(serviceDate)}</p>
            </div>

            <div className="service-column">
                <p>{mileageAtService.toLocaleString()} km</p>
            </div>

            <div className="service-column">
                <p>{formatServiceType(serviceType)}</p>
            </div>

            <div className="service-column">
                <p>{description.trim() == "" ? "-" : description}</p>
            </div>

            <div className="service-column">
                <p>R{cost}</p>
            </div>

            <div className="service-column">
                <p>
                    {serviceType != "OTHER" ? `${nextDueMileage.toLocaleString()} km` : "-"}
                </p>
            </div>

            <div className="service-column">
                <p>
                    {serviceType != "OTHER" ? formatDate(nextDueDate) : "-"}
                </p>
            </div>

            <div className="service-column">
                {isLatestRecord ?
                    <p style={{
                        color: highlightRemaining && remainingKm > 0 && remainingKm <= 1500 ? "Orange" :
                            highlightRemaining && remainingKm < 0 ? "red" : "black"
                    }}>
                        {formatRemainingKM(remainingKm)}
                    </p> : <p style={{ color: "green" }}>Service done</p>
                }
                {serviceType != "OTHER" ? "" : "-"}
            </div>

            <div className="service-column">
                {isLatestRecord ?
                    <p style={{
                        color: highlightRemaining && remainingDays > 0 && remainingDays <= 30 ? "Orange" :
                            highlightRemaining && remainingDays < 0 ? "red" : "black"
                    }}>
                        {formatRemainingDays(remainingDays)}
                    </p> : <p style={{ color: "green" }}>Service done</p>
                }
                {serviceType != "OTHER" ? "" : "-"}
            </div>

            <div className="service-column">
                <button className="service-column-button" type="button" onClick={onEdit}>Edit</button>
            </div>
        </div>
    );
}

export default VehicleDetailsCard;