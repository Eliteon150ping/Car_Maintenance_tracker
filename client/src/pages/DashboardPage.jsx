import PageHeader from "../components/PageHeader";
import SummaryCard from "../components/SummaryCard";

// Component Names always start with Capital letters to distinguish from HTML elements
// Component Names should match the file name for better readability
function DashboardPage(){

    return(
        <div>
            <PageHeader 
            title="Dashboard"
            description="Welcome to the Car Maintenance Tracker App"
            />

            <SummaryCard 
            title="Vehicles"
            value={2}
            />

            <SummaryCard 
            title="Services done"
            value={10}
            />

            <SummaryCard 
            title="Upcoming"
            value={2}
            />

            <SummaryCard 
            title="Overdue"
            value={3}
            />

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