import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { getUpcomingRecords } from "../api/vehicleDetailsApi";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { useNavigate } from "react-router-dom";

function UpcomingServicesPage({showExtraDetails}) {

    const [serviceRecords, setServiceRecords] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadServiceRecords();
    }, []);

    async function loadServiceRecords() {
        const data = await getUpcomingRecords();
        setServiceRecords(data);
    }

    return (
        <div className="service-history-page">
            <PageHeader
                title="Upcoming services"
                description="Be sure to service your car soon"
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

export default UpcomingServicesPage;