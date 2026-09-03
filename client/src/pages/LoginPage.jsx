import PageHeader from "../components/PageHeader";
import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Link, useNavigate } from "react-router-dom";
import "../styles/AuthPage.css";

function LoginPage() {

    const [email, setEmail] = useState("");        // these hooks will store the email and password when needed for
    const [password, setPassword] = useState("");  // submitting the form
    const { login } = useAuth();                     // Use the login function that AuthContext is sharing
    const navigate = useNavigate();
    const [errors, setErrors] = useState({});
    const [shake, setShake] = useState(false);

    async function handleSubmit(event) {           // React calls this function when the form is submitted.
        event.preventDefault();

        const validationErrors = {};

        function validateEmail() {
            if (email.trim() == "") {
                validationErrors.email = "Email cannot be empty";
            }
        }
        validateEmail();

        function validatePassword() {
            if (password.trim() == "") {
                validationErrors.password = "Password cannot be empty";
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
            await login(email, password);
            navigate("/");
        } catch (error) {
            console.error("Error caught: " + error.message);
            setErrors(error.errors ? error.errors : { general: error.message });
            triggerShake();
        }

        function triggerShake(){
            setShake(true);
            setTimeout(() => {
                setShake(false)
            }, 400);
        }
    }

    return (
        <div className="auth-page">

            <PageHeader
                title="Login"
                description="Please enter your details to continue"
            />

            <form className="auth-form" onSubmit={handleSubmit}>

                <label className="form-field">Email
                    <input type="email"
                        className={errors.email ? (shake ? `input-error input-shake` :`input-error`) : ""}
                        placeholder="Enter your email address"
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
                        placeholder="Enter your password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />

                    {errors.password && (
                        <span className="field-error">
                            {errors.password}
                        </span>
                    )}
                </label>

                <button className="auth-button" type="submit">Login</button>

                <p className="auth-link">Don't have an account? <Link to="/register">Make one here</Link></p>

            </form>
        </div>
    );
}

export default LoginPage;

/* 
The flow of the login:

User types
      │
      ▼
Browser Input
      │
      ▼
onChange fires
      │
      ▼
setEmail(...)
      │
      ▼
React State
      │
      ▼
React re-renders
      │
      ▼
value={email}

*/