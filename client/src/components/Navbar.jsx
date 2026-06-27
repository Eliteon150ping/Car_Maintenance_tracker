// Components are reusable pieces of UI that can be shared across multiple pages to avoid duplicating code
import {Link} from "react-router-dom";

function Navbar(){

    return(
        <nav>
            <h2>Car Maintenance Tracker</h2>
            <ul>
                <li>
                    <Link to="/">Dashboard</Link>
                </li>
                <li>
                    <Link to="/login">Login</Link>
                </li>
                <li>
                    <Link to="/service-records">Service Records</Link>
                </li>
                <li>
                    <Link to="/vehicles">My Garage</Link>
                </li>
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
