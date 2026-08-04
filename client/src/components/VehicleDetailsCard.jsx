import { formatRemainingKM, formatRemainingDays, formatDate, formatServiceType } from "../utils/serviceFormatter"

function VehicleDetailsCard({ id, serviceDate, mileageAtService, nextDueMileage, nextDueDate, serviceType, cost,
    description, remainingKm, remainingDays, car, onEdit }) {

    return (

        <div className="vehicle-details-card">
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
            <p>{formatRemainingKM(remainingKm)}</p>
            <p>{formatRemainingDays(remainingDays)}</p>
            <button type="edit" onClick={onEdit}>Edit</button>
        </div>

    );
}

export default VehicleDetailsCard;