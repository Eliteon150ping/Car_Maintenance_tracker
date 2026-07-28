import { useAuth } from "../context/AuthContext";
import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {

    const { user, loading } = useAuth();            // Bring the user auth state and loading from authContext

    if (loading) {                                  // Redirect unauthenticated users to the login page
        return <p>Loading...</p>
    }

    if (!user) {                    // If user is not logged in then redirect to login page 
        return (
            <Navigate to="/login" />
        );
    }

    return children;                     // If a user is authenticated, render the protected page
}

export default ProtectedRoute;