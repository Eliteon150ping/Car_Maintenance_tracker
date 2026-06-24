// Component Names always start with Capital letters to distinguish from HTML elements
// Component Names should match the file name for better readability
function DashboardPage(){

    return(
        <div>
            <h1>Dashboard</h1>
            <p>Welcome to the Car Maintenance Tracker App</p>
        </div>
    );
}

export default DashboardPage;

/*

'Pages' folder contains components that are called and rendered when the user navigates to a certain route
Eg. /dashboard -> DashboardPage

NB: Always create pages first then connect routes and test navigation so avoid import errors

*/