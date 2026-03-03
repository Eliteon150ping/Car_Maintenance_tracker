package sia.sever.service;

import sia.sever.entity.ServiceRecord;

public interface ServiceRecordService {

    // These methods must be defined in the class that uses this interface(eg. ServiceRecordImpl)
    ServiceRecord createServiceRecord(ServiceRecord serviceRecord);
    ServiceRecord getServiceRecordById(Long id);
    ServiceRecord upadteServiceRecord(Long id, ServiceRecord serviceRecord);
    
}
