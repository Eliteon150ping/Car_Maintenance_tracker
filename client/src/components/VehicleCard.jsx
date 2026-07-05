function VehicleCard({brand, model, year, mileage}){

    return(
        <div>
            <h3>{year} {brand} {model}</h3>
            <p>Mileage: {mileage} km</p>
        </div>
    );
}

export default VehicleCard;