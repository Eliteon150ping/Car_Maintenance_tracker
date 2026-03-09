package sia.sever.service;

import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryService {

    // These methods must be defined in the class that uses this interface(eg. ServiceHistoryImpl)
    ServiceHistory createServiceHistory(ServiceHistory serviceHistory);
    ServiceHistory getServiceHistoryById(Long id);
    ServiceHistory updateServiceHistory(Long id, ServiceHistory serviceHistory);
    List<ServiceHistory> getServiceHistoryForACar(Car car);
    List<ServiceHistory> getServiceHistoryByDate(Car car, LocalDate date);
    List<ServiceHistory> getServiceHistoryByServiceType(ServiceType serviceType);
    List<ServiceHistory> getServiceHistoryByCategory(ServiceCategory serviceCategory);
}
