import { useEffect, useState } from "react";
import { updateUserDetails } from "../api/authApi";

function UpdateProfileForm({ profile, onSave, onCancel }) {

    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errors, setErrors] = useState([]);

    const formData = {
        userName,
        password: password.trim() == "" ? null : password
    };

    useEffect(() => {

        if (profile != null) {
            setUserName(profile.userName);
            setEmail(profile.email);
        }

    }, [profile])

    async function handleSubmit(event) {
        event.preventDefault();

        const validationErrors = [];

        function validateNewUserName() {
            if (userName.trim() == "") {
                validationErrors.push("Username cannot be empty");

            } else if (userName !== "" && userName.length < 3) {
                validationErrors.push("Username must be atleast 3 characters long");
            }
        }
        validateNewUserName();

        function validatePassword() {
            if (password !== "" && password.length < 8) {
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
            await updateUserDetails(formData);
            onSave();

        } catch (error) {
            console.error("Error caught " + error.message);
            setErrors(error.errors?.length ? error.errors : [error.message]);
        }
    }

    return (

        <form onSubmit={handleSubmit}>

            <h2 style={{color: "black"}}>Update profile</h2>
            <label>Username
                <input type="text"
                    placeholder="Enter a new username"
                    value={userName}
                    onChange={(event) => setUserName(event.target.value)} />
            </label>

            <label>Email
                <input type="email"
                    value={email}
                    disabled={true}
                    onChange={(event) => setEmail(event.target.value)} />
            </label>

            <label>Password
                <input type="password"
                    placeholder="(Optional) Enter a new password"
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

            <button type="submit">Save changes</button>
            <button type="button" onClick={onCancel}>Cancel</button>
        </form>
    );
}

export default UpdateProfileForm;