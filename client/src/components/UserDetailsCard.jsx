function UserDetailsCard({id, userName, email}){

    return(

        <div className="user-details-card">
            <h3>User Details</h3>
            <p>Username: {userName}</p>
            <p>Email: {email}</p>
            <p>Password: *****</p>
        </div>
    );
}

export default UserDetailsCard;