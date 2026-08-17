import PageHeader from "../components/PageHeader";
import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import {useNavigate} from "react-router-dom"

function LoginPage() {

    const [email, setEmail] = useState("");        // these hooks will store the email and password when needed for
    const [password, setPassword] = useState("");  // submitting the form
    const {login} = useAuth();                     // Use the login function that AuthContext is sharing
    const navigate = useNavigate();
    const [errors, setErrors] = useState([]);

    async function handleSubmit(event) {           // React calls this function when the form is submitted.
        event.preventDefault();

        const validationErrors = [];

        function validateEmail(){
            if(email.trim() == ""){
                validationErrors.push("Email cannot be empty");
            }
        }
        validateEmail();

        function validatePassword(){
            if(password.trim() == ""){
                validationErrors.push("Password cannot be empty");
            }
        }
        validatePassword();

        if(validationErrors.length > 0){
            setErrors(validationErrors);
            return;
        }

        setErrors([]);
        try{
            await login(email,password);
            navigate("/");
        }catch(error){
            console.error("Error caught: " + error.message);
            setErrors([error.errors?.length ? error.errors : [error.message]])
        }
        
    }

    return (
        <div>
            <PageHeader
                title="Login"
                description="Please enter your details to continue"
            />
            <form onSubmit={handleSubmit}>
                <label>Email
                    <input type="email"
                        placeholder="Enter your email address"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)} />
                </label>
                <label>Password
                    <input type="password"
                    placeholder="Enter your password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />
                </label>
                {errors.length > 0 && (
                    <ul style={{color: "red"}}>
                        {errors.map((error, index) => (
                            <li key={index}>{error}</li>
                        ))}
                    </ul>
                )}
                <button type="submit">Login</button>
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