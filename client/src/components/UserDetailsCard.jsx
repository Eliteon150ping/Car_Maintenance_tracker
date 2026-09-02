import "../styles/UserDetailsCard.css";

function UserDetailsCard({ userName, email }) {

    return (

        <div className="user-details-card">
            <h3>User Details</h3>

            <label>Username
                <input
                    value={userName}
                    disabled={true} />
            </label>

            <label>Email
                <input
                    value={email}
                    disabled={true} />
            </label>

            <label>Password
                <input
                    value="*****"
                    disabled={true} />
            </label>
        </div>
    );
}

export default UserDetailsCard;