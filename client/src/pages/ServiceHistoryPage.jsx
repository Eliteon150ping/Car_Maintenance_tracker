import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import VehicleDetailsCard from "../components/VehicleDetailsCard";
import { getAllServiceRecords} from "../api/vehicleDetailsApi";
import { useNavigate } from "react-router-dom";

function ServiceHistoryPage() {

    const [serviceRecords, setServiceRecords] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadServiceRecords();
    }, []);

    async function loadServiceRecords() {

        const data = await getAllServiceRecords();
        setServiceRecords(data);
    }

    return (
        <div>
            <PageHeader
                title="Service History Page"
                description="View all service records for all your owned cars"
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
                    cost={serviceRecord.cost}
                    description={serviceRecord.description}
                    remainingKm={serviceRecord.remainingKm}
                    remainingDays={serviceRecord.remainingDays}
                    showExtraDetails={true}

                    onEdit={() => {
                        navigate(`/vehicles/${serviceRecord.car.id}`, {
                            state: {editingServiceRecordId : serviceRecord.id}
                        });
                    }}

                />
            ))}


        </div>
    );
}

export default ServiceHistoryPage;