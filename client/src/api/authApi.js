// Register new user
export async function register(userName, email, password) {

    const user = { userName, email, password };
    const jsonString = JSON.stringify(user);

    const response = await fetch("http://localhost:8080/api/users/auth/register", {

        method: "POST",
        body: jsonString,
        headers: { "Content-Type": "application/json" }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    const registerData = await response.json();
    return registerData;
}

// Login user
export async function login(email, password) {

    const user = { email, password };
    const jsonString = JSON.stringify(user);

    const response = await fetch("http://localhost:8080/api/users/auth/login", {  /* fetch() has two parameters:
                                                                                    1) URL
                                                                                    2) Options object
                                                                                    (to either recieve or send data)
                                                                                    The options object can contain:

                                                                                    - method
                                                                                    - headers
                                                                                    - body

                                                                                    to send to the url if you 
                                                                                    sending user info from the 
                                                                                    frontend 
                                                                                */

        method: "POST",   // Tells what type of method to use the data for: post, get, delete etc.
        body: jsonString, // The JSON representation of the object being sent to the backend.
        headers: { "Content-Type": "application/json" }     // This tells what format data type is to be deserialized 
        // to the backend: json, xml, hmtl etc.
    });

    const loginData = await response.json();

    if (!response.ok) {
        const error = new Error(loginData.message || "Unable to login in");

        error.errors = loginData.errors || [];

        throw error;
    }

    return loginData;
}

// Get the current user
export async function getCurrentUser() {

    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/users/profile", {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    const currentUser = await response.json();
    return currentUser;
}

// Update user details
export async function updateUserDetails(profile) {

    const token = localStorage.getItem("token");
    const jsonUpdateProfile = JSON.stringify(profile);

    const response = await fetch("http://localhost:8080/api/users/profile", {

        method: "PUT",
        body: jsonUpdateProfile,
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    });

    const updatedUserDetails = await response.json();

    if (!response.ok) {
        const error = new Error(updatedUserDetails.message || "Unable to update profile");

        error.errors = updatedUserDetails.errors || [];

        throw error;
    }

    return updatedUserDetails;
}

/* 

How the flow of data gets sent through the frontend and backend:

User types their email and password.

↓

onChange updates the React state.

↓

React re-renders with the updated values.

↓

The user clicks Login.

↓

handleSubmit() calls login(email, password).

↓

authApi.js creates a JavaScript object containing the email and password.

↓

JSON.stringify() converts that object into JSON text.

↓

fetch() sends the JSON in the HTTP request body to the login endpoint.

↓

Spring Boot receives the JSON and converts it into a LoginRequestDTO.

↓

The authentication service validates the credentials.

*/