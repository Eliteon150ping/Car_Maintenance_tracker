import React, { createContext, useContext, useEffect, useState } from "react";
import { login as loginApi } from "../api/authApi";
import { getCurrentUser as currentUserApi } from "../api/authApi";
import {register as registerApi} from "../api/authApi"

// This acts as the central hub of who's logged in so the rest of the components or pages knows to check in
// here to see who's currently logged in and if its valid

// Create the context
const AuthContext = createContext(null);      // If someone tries to use this context without a provider, the 
                                              // default value is null cause the app needs to determine who has 
                                              // vaild credentials and jwt token before continuing 

// Create an AuthProvider
export function AuthProvider({ children }) {     // this will be exported to be used for the </app> which is the
                                                 // entire app to know that users must be logged in first

    const [loading, setLoading] = useState(true); // Indicates whether the application is still checking the user's 
                                                  // authentication state. Loading prevents the application from 
                                                  // rendering the wrong page(like the login page again after logging 
                                                  // in) while it's still checking whether the stored JWT is valid.

    const [user, setUser] = useState(null);   // set the user state to null cause they need to verify themselves
                                              // in order to proceed

    async function login(email, password) {
        const loginData = await loginApi(email, password);
        setUser(loginData.userResponseDTO);
        localStorage.setItem("token", loginData.token); // Store the JWT token once the user logs in
    }

    async function register(userName, email, password) {
        await registerApi(userName, email, password);
        await login(email, password);
    }

    function logout(){
        localStorage.removeItem("token");
        setUser(null);
    }

    const value = {
        user,
        loading,
        login,                                  // Here we STORE the function instead of just calling it and 
        register,                               // potentaily calling it at the wrong time
        logout
    };

    // Function to handle existing JWT's in the local Storage
    useEffect(() => {
        async function getCurrentUser() {

            try {
                const currentUser = await currentUserApi();
                setUser(currentUser);
            } catch (error) {
                localStorage.removeItem("token");
                setUser(null);
            } finally{
                setLoading(false);
            }
        }

        getCurrentUser();
    }, [])

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}