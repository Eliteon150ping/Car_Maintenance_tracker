import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import { getUpcomingRecords } from "../api/vehicleDetailsApi";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { useNavigate } from "react-router-dom";

function UpcomingServicesPage() {

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
        <div>
            <PageHeader
                title="Upcoming services"
                description="Be sure to service your car soon"
            />

            {serviceRecords.map(serviceRecord => (
                <VehicleDetailsCard
                    key={serviceRecord.id}
                    id={serviceRecord.id}

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