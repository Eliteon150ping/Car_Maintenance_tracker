import "../styles/VehicleInfomationCard.css";

function VehicleInformationCard({ brand, model, year, colour, currentMileage, onEdit }) {

    return (
        <div className="vehicle-information">

            <div className="vehicle-info-content">
                <h3 className="vehicle-info">{year} {brand} {model}</h3>
                <p className="vehicle-colour">Colour: {colour}</p>
                <p className="vehicle-info-mileage">Mileage: {currentMileage?.toLocaleString()} km</p>
                <button className="vehicle-info-edit" type="button" onClick={onEdit}>Edit</button>
            </div>

            <img className="vehicle-info-image" src="/images/generic_car.png" alt="Generic car" />

        </div>
    );
}

export default VehicleInformationCard;