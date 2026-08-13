import { useState } from "react";
import PageHeader from "../components/PageHeader";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function RegisterPage() {

    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const {register} = useAuth();
    const navigate = useNavigate();

    async function handleSubmit(event) {
        event.preventDefault();
        try{
            await register(userName, email, password);
            navigate("/");
        }catch(error){
            console.error("Error caught: " + error.message)
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
                    <input type="username"
                        value={userName}
                        onChange={(event) => setUserName(event.target.value)} />
                </label>
                <label>Email<input type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)} />
                </label>
                <label>Password
                    <input type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)} />
                </label>
                <button type="submit">Submit</button>
            </form>
        </div>
    );
}

export default RegisterPage;