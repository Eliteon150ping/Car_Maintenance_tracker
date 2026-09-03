import { useEffect, useState } from "react";
import { updateUserDetails } from "../api/authApi";
import "../styles/UpdateProfileForm.css";

function UpdateProfileForm({ profile, onSave, onCancel }) {

    const [userName, setUserName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errors, setErrors] = useState({});
    const [shake, setShake] = useState(false);

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

        const validationErrors = {};

        function validateNewUserName() {
            if (userName.trim() == "") {
                validationErrors.userName = "Username cannot be empty";

            } else if (userName !== "" && userName.length < 3) {
                validationErrors.userName = "Username must be atleast 3 characters long";
            }
        }
        validateNewUserName();

        function validatePassword() {
            if (password !== "" && password.length < 8) {
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
            await updateUserDetails(formData);
            onSave();

        } catch (error) {
            console.error("Error caught " + error.message);
            setErrors(error.errors ? error.errors : { general: error.message });
            triggerShake();
        }

        function triggerShake() {
            setShake(true);
            setTimeout(() => {
                setShake(false);
            }, 400);
        }
    }

    return (

        <form className="update-profile-page" onSubmit={handleSubmit}>

            <h3 className="user-details" style={{ color: "black" }}>Update profile</h3>
            <label className="update-field">Username
                <input type="text"
                    className={errors.userName ? (shake ? `input-error input-shake` : `input-error`) : ""}
                    placeholder="Enter a new username"
                    value={userName}
                    onChange={(event) => setUserName(event.target.value)} />

                {errors.userName && (
                    <span className="field-error">
                        {errors.userName}
                    </span>
                )}
            </label>

            <label className="update-field">Email
                <input type="email"
                    className={errors.email ? (shake ? `input-error input-shake` : `input-error`) : ""}
                    value={email}
                    disabled={true}
                    onChange={(event) => setEmail(event.target.value)} />

                {errors.email && (
                    <span className="field-error">
                        {errors.email}
                    </span>
                )}
            </label>

            <label className="update-field">Password
                <input type="password"
                    className={errors.password ? (shake ? `input-error input-shake` : `input-error`) : ""}
                    placeholder="(Optional) Enter a new password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)} />

                {errors.password && (
                    <span className="field-error">
                        {errors.password}
                    </span>
                )}
            </label>
            
            <div className="update-profile-buttons">
                <button className="update-profile-button" type="submit">Save changes</button>
                <button className="update-profile-button cancel" type="button" onClick={onCancel}>Cancel</button>
            </div>
        </form>
    );
}

export default UpdateProfileForm;