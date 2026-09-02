import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import UserDetailsCard from "../components/UserDetailsCard";
import { deleteAccount, getCurrentUser } from "../api/authApi";
import UpdateProfileForm from "../components/UpdateProfileForm";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import ConfirmationModal from "../components/ConfirmationModal";
import "../styles/ProfilePage.css";

function ProfilePage() {

    const [profile, setProfile] = useState(null);
    const [showEditingForm, setShowEditingForm] = useState(false);
    const { logout } = useAuth();
    const navigate = useNavigate();
    const [errors, setErrors] = useState([]);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    useEffect(() => {

        loadProfile();

    }, [])

    async function loadProfile() {
        const data = await getCurrentUser();
        setProfile(data);
    }

    async function handleDeleteAccount() {

        setErrors([]);
        try {
            await deleteAccount();
            logout();
            navigate("/login");

        } catch (error) {
            console.error(`Error caught: ${error.message}`);
            setErrors(["Unable to delete account, please try again later"]);
        }
    }

    return (

        <div className="profile-page">

            <PageHeader
                title="Profile"
                description="View your profile details here"
            />

            {!showEditingForm && profile && (
                <UserDetailsCard
                    id={profile.id}
                    userName={profile.userName}
                    email={profile.email}
                />
            )}

            {showEditingForm && (
                <UpdateProfileForm

                    profile={profile}
                    id={profile?.id}

                    onSave={() => {
                        loadProfile();
                        setShowEditingForm(false);
                    }}

                    onCancel={() => {
                        setShowEditingForm(false);
                    }}
                />
            )}

            {profile != null && !showEditingForm && (
                <button onClick={() => { setShowEditingForm(true) }}>Edit Profile</button>
            )}

            <button onClick={() => {
                logout();
                navigate("/login")
            }}>Logout</button>

            {errors.length > 0 && (
                <ul style={{ color: "red" }}>
                    {errors.map((error, index) => (
                        <li key={index}>{error}</li>
                    ))}
                </ul>
            )}

            <button onClick={() => setShowDeleteConfirmation(true)}>Delete Account</button>
            {showDeleteConfirmation && (
                <ConfirmationModal
                    title="Delete Account"
                    message="Are you sure you want to delete your account? This will erase all associated data with your account and it cannot be undone."
                    confirmText="Delete Account"
                    cancelText="Cancel"
                    onConfirm={handleDeleteAccount}
                    onCancel={() => setShowDeleteConfirmation(false)}
                />
            )}
        </div>
    );
}

export default ProfilePage;