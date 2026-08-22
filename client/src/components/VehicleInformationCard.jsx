function VehicleInformationCard({ brand, model, year, colour, currentMileage, onEdit }) {

    return (
            <div className="vehicle-information">
                <h3>{year} {brand} {model}</h3>
                <p>Colour: {colour}</p>
                <p>Mileage: {currentMileage?.toLocaleString()} km</p>
                <button type="edit" onClick={onEdit}>Edit</button>
            </div>
    );
}

export default VehicleInformationCard;