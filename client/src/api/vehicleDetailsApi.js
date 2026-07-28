export async function getAllServiceRecordsForACar(id){

    const token = localStorage.getItem("token");
    
    const response = await fetch(`http://localhost:8080/api/service-records/car/${id}`, {

        method: "GET",
        headers:{
            authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const vehicleDetails = await response.json();

    return vehicleDetails;
}