import { useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

function PasswordInput({ value, onChange, className, placeholder }) {

    const [showPassword, setShowPassword] = useState(false);

    return (
        <div className="password-input-container">

            <input
                type={showPassword ? "text" : "password"}
                className={className}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
            />

            <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
            >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
            </button>

        </div>
    );
}

export default PasswordInput;