export async function getAllVehicles() { // Async functions performs work that takes time to prevent JS from blocking
    
    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/my-cars", { // Await means Pause this function until the 
                                                                        // backend replies with the JSON response body.

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    }); 

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }
    
    const vehicles = await response.json(); // Take the JSON body from the response and convert it into a normal 
                                            // JavaScript object/array that React can use.
    return vehicles;
}

/*
This frontend API function does:

1) Ask for the data from the backend.
2) Convert the response into usable objects.
3) Return those objects to whoever called the function.

*/