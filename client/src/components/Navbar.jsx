// Components are reusable pieces of UI that can be shared across multiple pages to avoid duplicating code

import { useState } from "react";
import { Link } from "react-router-dom";
import "../styles/Navbar.css";
import { useAuth } from "../context/AuthContext";

function Navbar() {

    const { user } = useAuth();

    // Controls whether the mobile menu is open
    const [menuOpen, setMenuOpen] = useState(false);

    // Closes the menu after selecting a link
    const closeMenu = () => {
        setMenuOpen(false);
    };

    return (
        <nav className="navbar">

            <h2 className="navbar-brand">
                Car Maintenance Tracker
            </h2>

            <button
                className="navbar-toggle"
                onClick={() => setMenuOpen(!menuOpen)}
                aria-label="Toggle navigation menu"
                aria-expanded={menuOpen}
                type="button"
            >
                {menuOpen ? "✕" : "☰"}
            </button>

            <ul className={`navbar-links ${menuOpen ? "is-open" : ""}`}>

                {user ? (
                    <>
                        <Link className="link" to="/" onClick={closeMenu}>
                            Dashboard
                        </Link>

                        <Link className="link" to="/vehicles" onClick={closeMenu}>
                            My Garage
                        </Link>

                        <Link className="link" to="/profile" onClick={closeMenu}>
                            Profile
                        </Link>
                    </>
                ) : (
                    <>
                        <Link className="link" to="/login" onClick={closeMenu}>
                            Login
                        </Link>

                        <Link className="link" to="/register" onClick={closeMenu}>
                            Register
                        </Link>
                    </>
                )}

            </ul>

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
