// Components are reusable pieces of UI that can be shared across multiple pages to avoid duplicating code
import { Link } from "react-router-dom";
import "../styles/Navbar.css"
import { useAuth } from "../context/AuthContext";

function Navbar() {

    const { user } = useAuth();

    return (
        <nav>
            <h2>Car Maintenance Tracker</h2>
            {user ? (
                <ul>
                    <Link className="link" to="/">Dashboard</Link>
                    <Link className="link" to="/vehicles">My Garage</Link>
                    <Link className="link" to="/profile">Profile</Link>

                </ul>
            ) : (
                <ul>
                    <Link className="link" to="/login">Login</Link>
                    <Link className="link" to="/register">Register</Link>
                </ul>
            )}

        </nav>
    );
}

export default Navbar;
/*

What is it?
- A reusable piece of UI.

Why does it exist?
- To avoid duplicating UI.

When do I use it?
- Whenever the same UI appears in multiple places.

Java comparison
- Similar to creating a reusable class instead of copying code.

*/
