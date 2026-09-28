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

// Get all services records for a car(paginated)
export async function getServiceRecordsByCarIdPaginated(id, page = 0, size = 6){

    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/service-records/car/${id}/page?page=${page}&size=${size}`, {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`);
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

    const createdServiceRecord = await response.json();

    if (!response.ok) {
        const error = new Error(createdServiceRecord.message || "Unable to save service record, please try again later");

        error.errors = createdServiceRecord.errors || [];

        throw error;
    }

    return createdServiceRecord;
}

// Validate duplicate records before the confirmation box shows
export async function validateDuplicateRecord(carId, serviceType, serviceDate, mileageAtService) {

    const token = localStorage.getItem("token");

    const response = await fetch(
        `http://localhost:8080/api/service-records/car/${carId}/validate-duplicate?serviceType=${serviceType}&serviceDate=${serviceDate}&mileageAtService=${mileageAtService}`,
        {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        const validationError = await response.json();

        const error = new Error(
            validationError.message || "Unable to validate service record"
        );

        error.errors = validationError.errors || {};

        throw error;
    }
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

// Get All service records for every car
export async function getAllServiceRecords() {
    
    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/service-records", {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`);
    }

    const gotAllServiceRecords = await response.json();
    return gotAllServiceRecords;
}

// Get All service records for every car(pagination)
export async function getAllServiceRecordsPaginated(page = 0, size = 6) {
    
    const token = localStorage.getItem("token");

    const response = await fetch(`http://localhost:8080/api/service-records/page?page=${page}&size=${size}`,
        {
            method: "GET",
            headers:{
                Authorization: `Bearer ${token}`
            }
        }
    );

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
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

// Get all upcoming services
export async function getUpcomingRecords(){

    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/service-records/upcoming", {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const upcomingServices = await response.json();

    return upcomingServices;
}

// Get all upcoming services(paginated)
export async function getUpcomingRecordsPaginated(page = 0, size = 6) {

    const token = localStorage.getItem("token");

    const response = await fetch(
        `http://localhost:8080/api/service-records/upcoming/page?page=${page}&size=${size}`,
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
}

// Get all overdue services
export async function getOverdueRecords(){

    const token = localStorage.getItem("token");

    const response = await fetch("http://localhost:8080/api/service-records/overdue", {

        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if(!response.ok){
        throw new Error(`HTTP ${response.status}`)
    }

    const overdueServices = await response.json();

    return overdueServices;
}

// Get all overdue services(paginated)
export async function getOverdueRecordsPaginated(page = 0, size = 6) {
    
    const token = localStorage.getItem("token");

    const response = await fetch(
        `http://localhost:8080/api/service-records/overdue/page?page=${page}&size=${size}`,
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`
            }
        }
    );

    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();
}
