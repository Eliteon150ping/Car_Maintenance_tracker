import { useState } from "react";
import PageHeader from "../components/PageHeader";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "../styles/authPage.css";

function RegisterPage() {

    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const { register } = useAuth();
    const navigate = useNavigate();
    const [errors, setErrors] = useState({});
    const [shake, setShake] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();

        const validationErrors = {};

        function validateUserName() {
            if (userName.trim() == "") {
                validationErrors.userName = "Username cannot be empty";

            } else if (userName.length < 3) {
                validationErrors.userName = "Username must be atleast 3 characters long";
            }
        }
        validateUserName();

        function validateEmail() {
            if (email.trim() == "") {
                validationErrors.email = "Email cannot be empty";
            }
        }
        validateEmail();

        function validatePassword() {
            if (password.trim() == "") {
                validationErrors.password = "Password cannot be empty";

            } else if (password.length < 8) {
                validationErrors.password = "Password must be atleast 8 characters long";
            }
        }
        validatePassword();

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            triggerShake();
            return;
        }

        setErrors({});
        try {
            await register(userName, email, password);
            navigate("/");
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(error.errors ? error.errors : { general: error.message });
            triggerShake();
        }

        function triggerShake(){
            setShake(true);
            setTimeout(() => {
                setShake(false);
            }, 400);
        }
    }

    return (
        <div className="auth-page">

            <PageHeader
                title="Register Page"
                description="Don't have an account? Make one here!"
            />

            <form className="auth-form" onSubmit={handleSubmit}>

                <label className="form-field">Username
                    <input type="text"
                        className={errors.userName ? (shake ? `input-error input-shake` : `input-error`) : ""}
                        placeholder="eg. John Doe"
                        value={userName}
                        onChange={(event) => setUserName(event.target.value)} />

                    {errors.userName && (
                        <span className="field-error">
                            {errors.userName}
                        </span>
                    )}
                </label>

                <label className="form-field">Email
                    <input type="email"
                        className={errors.email ? (shake ? `input-error input-shake` : `input-error`) : ""}
                        placeholder="eg. JohnDoe@gmail.com"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)} />

                    {errors.email && (
                        <span className="field-error">
                            {errors.email}
                        </span>
                    )}
                </label>

                <label className="form-field">Password
                    <input type="password"
                        className={errors.password ? (shake ? `input-error input-shake` : `input-error`) : ""}
                        placeholder="eg. JohnDoe123456"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />

                    {errors.password && (
                        <span className="field-error">
                            {errors.password}
                        </span>
                    )}
                </label>

                <button className="auth-button" type="submit">Register</button>

                <p className="auth-link">Already have an account? <Link to="/login">Login here</Link></p>
            </form>
        </div>
    );
}

export default RegisterPage;