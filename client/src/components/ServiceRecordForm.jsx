import { useState } from "react";

function ServiceRecordForm(){

    const [serviceDate, setServiceDate] = useState("");
    const [mileageAtService, setMileageAtService] = useState("");
    const [serviceType, setSeviceType] = useState("");
    cosnt [description, setDescription] = useState("");

    async function handleSubmit() {
        
    }

    return(

        <form onSubmit={handleSubmit}>
            test..
        </form>
    );
}

export default ServiceRecordForm;