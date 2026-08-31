package sia.sever.service;

import org.springframework.data.domain.Page;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryService {

    // These methods must be defined in the class that uses this interface(eg. ServiceHistoryImpl)
    ServiceRecordResponseDTO createServiceHistory(CreateServiceRecordDTO serviceHistory, Long carId);
    List<ServiceRecordResponseDTO> getAllServiceRecords();
    ServiceRecordResponseDTO getServiceHistoryById(Long id);
    ServiceRecordResponseDTO updateServiceHistory(Long id, UpdateServiceRecordDTO serviceHistory, Long carId);
    List<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId);
    List<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate);
    List<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType);
    List<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory);
    List<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType(Long carId, ServiceType serviceType);
    List<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory(Long carId, ServiceCategory serviceCategory);
    List<ServiceRecordResponseDTO> getUpcomingServiceRecords();
    List<ServiceRecordResponseDTO> getOverDueServiceRecords();

    // Pagination methods(optional but helps the frontend load data quicker)
    Page<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType(Long carId, ServiceType serviceType, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory(Long carId, ServiceCategory serviceCategory, int page, int size);
}
