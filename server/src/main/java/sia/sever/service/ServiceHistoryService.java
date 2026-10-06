package sia.sever.service;

import org.springframework.data.domain.Page;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.LatestServiceInfoDTO;
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
    ServiceRecordResponseDTO updateServiceHistory(Long id, UpdateServiceRecordDTO serviceHistory, Long carId);
    List<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId);
    List<ServiceRecordResponseDTO> getUpcomingServiceRecords();
    List<ServiceRecordResponseDTO> getOverdueServiceRecords();
    void validateDuplicateRecord(Long carId, ServiceType serviceType, LocalDate serviceDate, int mileageAtService);
    LatestServiceInfoDTO getLatestServiceInfo(Long carId);

    // Pagination methods(optional but helps the frontend load data quicker)
    Page<ServiceRecordResponseDTO> getAllServiceRecords(List<ServiceType> serviceTypes, List<ServiceCategory> serviceCategories, int page, int size);
    Page<ServiceRecordResponseDTO> getUpcomingServiceRecords(List<ServiceType> serviceTypes, List<ServiceCategory> serviceCategories, int page, int size);
    Page<ServiceRecordResponseDTO> getOverdueServiceRecords(List<ServiceType> serviceTypes, List<ServiceCategory> serviceCategories, int page, int size);
    Page<ServiceRecordResponseDTO> getServiceHistoryByCar(List<ServiceType> serviceTypes, List<ServiceCategory> serviceCategories, Long carId, int page, int size);
}
