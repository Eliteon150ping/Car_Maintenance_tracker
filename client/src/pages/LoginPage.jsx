import PageHeader from "../components/PageHeader";
import { useState } from "react";

function LoginPage() {

    const [email, setEmail] = useState("");        // these hooks will store the email and password when needed for
    const [password, setPassword] = useState("");  // submitting the form

    function handleSubmit(event) {                 // React calls this function when the form is submitted.
        event.preventDefault();
        console.log(email);
        console.log(password);
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
                        value={email}
                        onChange={(event) => setEmail(event.target.value)} />
                </label>
                <label>Password
                    <input type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />
                </label>
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