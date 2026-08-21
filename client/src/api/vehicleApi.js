// Get all cars tied to a specific user
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

// Add a new car
export async function addCar(carData) {
    
    const token = localStorage.getItem("token");
    const jsonCarData = JSON.stringify(carData);

    const response = await fetch("http://localhost:8080/api/my-cars", {

        method: "POST",
        body: jsonCarData,
        headers:{
            Authorization: `Bearer ${token}`,
            "Content-Type" : "application/json"
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const createdCar = await response.json();
    return createdCar;
}

// Edit a car
export async function editCar(id, carData){

    const token = localStorage.getItem("token");
    const jsonCarData = JSON.stringify(carData);

    const response = await fetch(`http://localhost:8080/api/my-cars/${id}`, {

        method: "PUT",
        body: jsonCarData,
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type" : "application/json"
        }
    });

    const updatedCar = await response.json();

    if(!response.ok){
       const error = new Error(updatedCar.message || "Unable to save car details, please try again later");

       error.errors = updatedCar.errors || [];

       throw error;
    }

    return updatedCar;
}

// Delete a car
export async function deleteCar(id) {
    
    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/my-cars/${id}`, {

        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type" : "application/json"
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`);
    }
}

/*
This frontend API function does:

1) Ask for the data from the backend.
2) Convert the response into usable objects.
3) Return those objects to whoever called the function.

*/