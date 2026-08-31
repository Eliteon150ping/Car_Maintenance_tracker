import { useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function RegisterPage() {

    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const { register } = useAuth();
    const navigate = useNavigate();
    const [errors, setErrors] = useState([]);

    async function handleSubmit(event) {
        event.preventDefault();

        const validationErrors = [];

        function validateUserName() {
            if (userName.trim() == "") {
                validationErrors.push("Username cannot be empty");

            } else if (userName.length < 3) {
                validationErrors.push("Username must be atleast 3 characters long");
            }
        }
        validateUserName();

        function validateEmail() {
            if (email.trim() == "") {
                validationErrors.push("Email cannot be empty");
            }
        }
        validateEmail();

        function validatePassword() {
            if (password.trim() == "") {
                validationErrors.push("Password cannot be empty");

            } else if (password.length < 8) {
                validationErrors.push("Password must be atleast 8 characters long");
            }
        }
        validatePassword();

        if (validationErrors.length > 0) {
            setErrors(validationErrors);
            return;
        }

        setErrors([]);
        try {
            await register(userName, email, password);
            navigate("/");
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(error.errors?.length ? error.errors : [error.message]);
        }
    }

    return (
        <div>
            <PageHeader
                title="Register Page"
                description="Don't have an account? Make one here!"
            />

            <form onSubmit={handleSubmit}>

                <label>Username
                    <input type="text"
                        placeholder="eg. John Doe"
                        value={userName}
                        onChange={(event) => setUserName(event.target.value)} />
                </label>

                <label>Email
                    <input type="email"
                        placeholder="eg. JohnDoe@gmail.com"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)} />
                </label>

                <label>Password
                    <input type="password"
                        placeholder="eg. JohnDoe123456"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />
                </label>

                {errors.length > 0 && (
                    <ul style={{ color: "red" }}>
                        {errors.map((error, index) => (
                            <li key={index}>{error}</li>
                        ))}
                    </ul>
                )}

                <button type="submit">Register</button>
            </form>
        </div>
    );
}

export default RegisterPage;