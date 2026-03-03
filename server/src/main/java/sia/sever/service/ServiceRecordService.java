package sia.sever.service;

import sia.sever.entity.ServiceRecord;

import java.util.List;

public interface ServiceRecordService {

    // These methods must be defined in the class that uses this interface(eg. ServiceRecordImpl)
    ServiceRecord createServiceRecord(ServiceRecord serviceRecord);
    ServiceRecord getServiceRecordById(Long id);
    ServiceRecord updateServiceRecord(Long id, ServiceRecord serviceRecord);
    List<ServiceRecord> getFilteredServiceRecords(Integer engineOilAndFilter, Integer tyreRotation,
                                                  Integer airFilter, Integer brakePads,
                                                  Integer wheelAlignment, Integer coolantFlush,
                                                  Integer sparkPlugs, Integer serpentineBelt,
                                                  Integer timingBelt, Integer battery, Integer fuelFilter,
                                                  Integer differentialOil);
}
