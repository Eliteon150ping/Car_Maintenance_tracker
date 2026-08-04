// Get a car by its ID
export async function getCarById(id) {

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/my-cars/${id}`, {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    const vehicleDetails = await response.json();

    return vehicleDetails;

}

// Get all services records for a car
export async function getServiceRecordsByCarId(id) {

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/service-records/car/${id}`, {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    const serviceRecords = await response.json();

    return serviceRecords;
}

// Add service record
export async function addServiceRecord(carId, serviceRecord) {

    const token = localStorage.getItem("token");
    const jsonServiceRecord = JSON.stringify(serviceRecord);

    const response = await fetch(`http://localhost:8080/api/service-records/car/${carId}`, {

        method: "POST",
        body: jsonServiceRecord,
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }

    const createdServiceRecord = await response.json();

    return createdServiceRecord;
}

// Edit service record
export async function editServiceRecord(carId, id, serviceRecord){

    const token = localStorage.getItem("token");
    const jsonServiceRecord = JSON.stringify(serviceRecord);

    const response = await fetch(`http://localhost:8080/api/service-records/car/${carId}/service/${id}`, {

        method: "PUT",
        body: jsonServiceRecord,
        headers:{
            Authorization: `Bearer ${token}`,
            "Content-type": "application/json"
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const updatedServiceRecord = await response.json();

    return updatedServiceRecord;
}

// Get Enum service type list
export async function getServiceTypes(){

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/lookups/service-types`, {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const serviceTypes = await response.json();
    
    return serviceTypes;
}