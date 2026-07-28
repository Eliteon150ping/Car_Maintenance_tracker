function VehicleDetailsCard({id, serviceDate, mileageAtService, nextDueMileage, nextDueDate, serviceType, cost, 
                             description, car}){
    return(
        
            <div className="vehicle-details-card">
                <h3>{ serviceDate} {mileageAtService}</h3>
                <p>nextDueMileage: {nextDueMileage}</p>
            </div>
     
    );
}

export default VehicleDetailsCard;