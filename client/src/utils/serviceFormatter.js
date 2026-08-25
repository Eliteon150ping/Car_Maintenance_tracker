// Format the Remaining KM
export function formatRemainingKM(km){

    if(km == null){
        return "";
    }

    if (km < 0){
        return `Remaining KM: Service overdue by ${Math.abs(km).toLocaleString()} km`;
    }

    return `Remaining KM: ${km.toLocaleString()} km remaining`;
}

// Format the Remaining Days
export function formatRemainingDays(days){

    if(days == null){
        return "";
    }

    if(days < 0){
        return `Remaining Days: Service overdue by ${Math.abs(days)} days`;
    }

    if(days == 0){
        return `Remaining Days: 1 day remaining till next service`;
    }

    return `Remaining Days: ${days} days remaining`;
}

// Format the actual Date
export function formatDate(inputDate){

    const date = new Date(inputDate);

    if(isNaN(date.getTime())){
        return "Invaild Date"
    }

    return new Intl.DateTimeFormat('en-GB',{
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).format(date);
}

// Format the Service Type
export function formatServiceType(serviceType){

    if (!serviceType){
        return "";
    }

    return serviceType
    .toLowerCase()
    .split("_")
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}