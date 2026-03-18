package sia.sever.service;

import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryService {

    // These methods must be defined in the class that uses this interface(eg. ServiceHistoryImpl)
    ServiceHistory createServiceHistory(ServiceHistory serviceHistory, Long carId);
    List<ServiceHistory> getAllServiceRecords();
    ServiceHistory getServiceHistoryById(Long id);
    ServiceHistory updateServiceHistory(Long id, ServiceHistory serviceHistory, Long carId);
    List<ServiceHistory> getServiceHistoryByCar(Long carId);
    List<ServiceHistory> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate);
    List<ServiceHistory> getServiceHistoryByServiceType(ServiceType serviceType);
    List<ServiceHistory> getServiceHistoryByCategory(ServiceCategory serviceCategory);
}
