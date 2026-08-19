function VehicleInformationCard({ id, brand, model, year, colour, currentMileage }) {

    return (
            <div className="vehicle-information">
                <h3>{year} {brand} {model}</h3>
                <p>Colour: {colour}</p>
                <p>Mileage: {currentMileage.toLocaleString()} km</p>
                <button type="edit">Edit</button>
            </div>
    );
}

export default VehicleInformationCard;