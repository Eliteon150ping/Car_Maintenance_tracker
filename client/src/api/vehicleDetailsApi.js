export async function getCarById(id) {

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/my-cars/${id}`, {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const vehicleDetails = await response.json();

    return vehicleDetails;
    
}

export async function getServiceRecordsByCarId(id){

    const token = localStorage.getItem("token");
    
    const response = await fetch(`http://localhost:8080/api/service-records/car/${id}`, {

        method: "GET",
        headers:{
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const serviceRecords = await response.json();

    return serviceRecords;
}