import PageHeader from "../components/PageHeader";
import SummaryCard from "../components/SummaryCard";
import "../styles/DashboardPage.css";
import { getAllVehicles } from "../api/vehicleApi";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllServiceRecords, getOverdueRecords, getUpcomingRecords } from "../api/vehicleDetailsApi";

// Component Names always start with Capital letters to distinguish from HTML elements
// Component Names should match the file name for better readability
function DashboardPage() {

    const [vehicleCount, setVehicleCount] = useState(0);
    const [serviceRecordCount, setServiceRecordCount] = useState(0);
    const [upcomingCount, setUpcomingCount] = useState(0);
    const [overdueCount, setOverDueCount] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {

        loadVehicleCount();
        loadServiceRecordsCount();

    }, []);

    async function loadVehicleCount() {
        const vehicles = await getAllVehicles();
        setVehicleCount(vehicles.length);
    }

    async function loadServiceRecordsCount() {
        const serviceRecords = await getAllServiceRecords();
        const upcomingServices = await getUpcomingRecords();
        const overdueServices = await getOverdueRecords();
        setServiceRecordCount(serviceRecords.length);
        setUpcomingCount(upcomingServices.length);
        setOverDueCount(overdueServices.length);
    }

    return (
        <div className="dashboard-page">

            <PageHeader
                title="Dashboard"
                description="Welcome to the Car Maintenance Tracker App"
            />

            <div className="summary-grid">

                <SummaryCard
                    className="summary-vehicles"
                    title="Vehicles"
                    value={vehicleCount}
                    onClick={() => navigate("/vehicles")}
                />

                <SummaryCard
                    className="summary-done"
                    title="Services done"
                    value={serviceRecordCount}
                    onClick={() => navigate("/service-history")}
                />


                <SummaryCard
                    className="summary-upcoming"
                    title="Upcoming"
                    value={upcomingCount}
                    onClick={() => navigate("/upcoming-services")}
                />

                <SummaryCard
                    className="summary-overdue"
                    title="Overdue"
                    value={overdueCount}
                    onClick={() => navigate("/overdue-services")}
                />

            </div>

            <div className="dashboard-info">

    <div className="dashboard-info-section">
        <h2>Your vehicle maintenance at a glance</h2>
        <p>
            Keep track of your vehicles, service history, upcoming maintenance,
            and overdue services from one convenient place.
        </p>
    </div>

    <div className="dashboard-info-section">
        <h2>Stay on top of maintenance</h2>
        <p>
            Regular maintenance can help keep your vehicles reliable and make
            it easier to keep track of important services over time.
        </p>
    </div>

    <div className="dashboard-info-section">
        <h2>Keep your service history organised</h2>
        <p>
            Record completed services with the service date, mileage, cost,
            and other relevant details to keep your vehicle's maintenance
            history organised.
        </p>
    </div>

</div>
        </div>
    );
}

export default DashboardPage;

/*

'Pages' folder contains components that are called and rendered when the user navigates to a certain route
(They are basically what you call a Full page)
Eg. /dashboard -> DashboardPage

NB: Always create pages first then connect routes and test navigation so avoid import errors

*/