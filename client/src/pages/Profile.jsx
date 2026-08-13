import { useEffect, useState } from "react";
import PageHeader from "../components/PageHeader";
import UserDetailsCard from "../components/UserDetailsCard";
import { getCurrentUser } from "../api/authApi";
import UpdateProfileForm from "../components/UpdateProfileForm";

function Profile() {

    const [profile, setProfile] = useState(null);
    const [showEditingForm, setShowEditingForm] = useState(false);

    useEffect(() => {

        loadProfile();

    }, [])

    async function loadProfile() {
        const data = await getCurrentUser();
        setProfile(data);
    }

    return (

        <>
            <PageHeader
                title="Profile"
                description="View your profile details here"
            />

            {profile && (
                <UserDetailsCard
                    key={profile.id}
                    id={profile.id}
                    userName={profile.userName}
                    email={profile.email}

                />
            )}

            {showEditingForm ? <UpdateProfileForm

                profile={profile}
                id={profile?.id}

                onSave={() => {
                    loadProfile();
                    setShowEditingForm(false);
                }}

                onCancel={() => {
                    loadProfile();
                    setShowEditingForm(false);
                }}
            />
                : <button onClick={() => { setShowEditingForm(true) }}>Edit Profile</button>}
        </>
    );
}

export default Profile;