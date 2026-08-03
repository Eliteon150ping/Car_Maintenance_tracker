function VehicleDetailsCard({ id, serviceDate, mileageAtService, nextDueMileage, nextDueDate, serviceType, cost,
    description, remainingKM, remainingDays, car , onEdit}) {

    return (

        <div className="vehicle-details-card">
            <h3>Service Date: {serviceDate} </h3>
            <p>Mileage: {mileageAtService} km</p>
            <p>Service type: {serviceType}</p>
            <p>Description: {description}</p>
            <p>cost: R{cost}</p>
            <p>Next due mileage: {nextDueMileage} km</p>
            <p>Next due date: {nextDueDate}</p>
            <p>Remaining KM: {remainingKM} km</p>
            <p>Remaining days:{remainingDays}</p>
            <button type="edit" onClick={onEdit}>Edit</button>
        </div>

    );
}

export default VehicleDetailsCard;