export async function login(email, password) {
    
    const response = await fetch("http://localhost:8080/api/users/auth/login");

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const user = {email, password}
    user = await response.json();
    return user;
}