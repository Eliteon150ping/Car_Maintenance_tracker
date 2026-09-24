import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate } from "react-router-dom";
import { getOverdueRecords } from "../api/vehicleDetailsApi";
import VehicleDetailsCard from "../components/VehicleDetailsCard";

function OverdueServicesPage({showExtraDetails}) {

    const [serviceRecords, setServiceRecords] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadRecords();
    }, []);

    async function loadRecords() {
        const data = await getOverdueRecords();
        setServiceRecords(data)
    }

    return (
        <div className="service-history-page">

            <PageHeader
                title="Overdue services"
                description="Urgent services that need to be done to prevent damage to your cars"
            />

            <div className={`service-record-header ${showExtraDetails ? "without-car" : "with-car"}`}>
                <div>Car</div>
                <div>Service Date</div>
                <div>Mileage at service</div>
                <div>Service Type</div>
                <div>Description</div>
                <div>Cost</div>
                <div>Next due mileage</div>
                <div>Next due date</div>
                <div>Remaining KM</div>
                <div>Remaining Days</div>
                <div>Edit</div>
            </div>

            {serviceRecords.map(serviceRecord => (
                <VehicleDetailsCard
                    key={serviceRecord.id}
                    id={serviceRecord.car.id}

                    brand={serviceRecord.car.brand}
                    model={serviceRecord.car.model}
                    year={serviceRecord.car.year}

                    serviceDate={serviceRecord.serviceDate}
                    mileageAtService={serviceRecord.mileageAtService}
                    nextDueMileage={serviceRecord.nextDueMileage}
                    nextDueDate={serviceRecord.nextDueDate}
                    serviceType={serviceRecord.serviceType}
                    isLatestRecord={true}
                    cost={serviceRecord.cost}
                    description={serviceRecord.description}
                    remainingKm={serviceRecord.remainingKm}
                    remainingDays={serviceRecord.remainingDays}
                    showExtraDetails={true}
                    highlightRemaining={true}

                    onEdit={() => {
                        navigate(`/vehicles/${serviceRecord.car.id}`, {
                            state: { editingServiceRecordId: serviceRecord.id }
                        });
                    }}
                />
            ))}

        </div>
    );
}

export default OverdueServicesPage;