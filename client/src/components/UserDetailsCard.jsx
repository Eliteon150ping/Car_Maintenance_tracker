import "../styles/UserDetailsCard.css";

function UserDetailsCard({ userName, email }) {

    return (

        <div className="user-details-card">
            <h3 className="user-details">User Details</h3>

            <label className="user-details-field">Username
                <input
                    value={userName}
                    disabled={true} />
            </label>

            <label className="user-details-field">Email
                <input
                    value={email}
                    disabled={true} />
            </label>

            <label className="user-details-field">Password
                <input
                    value="*****"
                    disabled={true} />
            </label>
        </div>
    );
}

export default UserDetailsCard;