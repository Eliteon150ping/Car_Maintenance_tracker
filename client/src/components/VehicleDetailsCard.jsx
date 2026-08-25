import { formatRemainingKM, formatRemainingDays, formatDate, formatServiceType } from "../utils/serviceFormatter"

function VehicleDetailsCard({ serviceDate, mileageAtService, nextDueMileage, nextDueDate, serviceType, cost,
    description, remainingKm, remainingDays, onEdit, showExtraDetails, brand, model, year, highlightRemaining }) {


    return (

        <div className="vehicle-details-card">

            {showExtraDetails && (
                <div>
                    <h2 style={{ color: "black" }}>{year} {brand} {model}</h2>
                </div>
            )}
            <h3>Service Date: {formatDate(serviceDate)} </h3>
            <p>Mileage At Service: {mileageAtService.toLocaleString()} km</p>
            <p>Service type: {formatServiceType(serviceType)}</p>
            <p>Description: {description}</p>
            <p>cost: R{cost}</p>
            {serviceType != "OTHER" && (
                <>
                    <p>Next due mileage: {nextDueMileage.toLocaleString()} km</p>
                    <p>Next due date: {formatDate(nextDueDate)}</p>
                </>
            )}

            <p style={{ color: highlightRemaining && remainingKm > 0 && remainingKm <= 1500 ? "Orange" : "black" }}>
                {formatRemainingKM(remainingKm)}
            </p>
            <p style={{ color: highlightRemaining && remainingDays > 0 && remainingDays <= 30? "Orange" : "black" }}>
                {formatRemainingDays(remainingDays)}
            </p>
            <button type="edit" onClick={onEdit}>Edit</button>
        </div>
    );
}

export default VehicleDetailsCard;