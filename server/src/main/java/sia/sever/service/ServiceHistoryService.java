package sia.sever.service;

import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryService {

    // These methods must be defined in the class that uses this interface(eg. ServiceHistoryImpl)
    ServiceRecordResponseDTO createServiceHistory(ServiceHistory serviceHistory, Long carId);
    List<ServiceRecordResponseDTO> getAllServiceRecords();
    ServiceRecordResponseDTO getServiceHistoryById(Long id);
    ServiceRecordResponseDTO updateServiceHistory(Long id, ServiceHistory serviceHistory, Long carId);
    List<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId);
    List<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate);
    List<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType);
    List<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory);
}
