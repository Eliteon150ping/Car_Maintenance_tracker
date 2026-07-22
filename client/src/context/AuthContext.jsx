import React, { createContext, useContext, useState } from "react";
import { login as loginApi } from "../api/authApi";

// This acts as the central hub of who's logged in so the rest of the components or pages knows to check in
// here to see who's currently logged in and if its valid

// Create the context
const AuthContext = createContext(null);      // If someone tries to use this context without a provider, the 
                                              // default value is null cause the app needs to determine who has 
                                              // vaild credentials and jwt token before continuing 

// Create an AuthProvider
export function AuthProvider({ children }) {     // this will be exported to be used for the </app> which is the
                                                 // entire app to know that users must be logged in first

    const [user, setUser] = useState(null);   // set the user state to null cause they need to verify themselves
                                              // in order to proceed

    async function login(email, password){
        const loginData = await loginApi(email, password);
        console.log(loginData);
        setUser(loginData);
    }
    
    const value = {
        user,
        login                                  // Here we STORE the function instead of just calling it and 
                                               // potentaily calling it at the wrong time
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(){
    return useContext(AuthContext);
}