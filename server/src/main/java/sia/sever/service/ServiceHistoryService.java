package sia.sever.service;

import sia.sever.entity.ServiceHistory;

import java.util.List;

public interface ServiceHistoryService {

    // These methods must be defined in the class that uses this interface(eg. ServiceHistoryImpl)
    ServiceHistory createServiceHistory(ServiceHistory serviceHistory);
    ServiceHistory getServiceHistoryById(Long id);
    ServiceHistory updateServiceHistory(Long id, ServiceHistory serviceHistory);
    List<ServiceHistory> getFilteredServiceHistory(Integer engineOilAndFilter, Integer tyreRotation,
                                                  Integer airFilter, Integer brakePads,
                                                  Integer wheelAlignment, Integer coolantFlush,
                                                  Integer sparkPlugs, Integer serpentineBelt,
                                                  Integer timingBelt, Integer battery, Integer fuelFilter,
                                                  Integer differentialOil);
}
