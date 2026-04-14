package sia.sever.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.dto.serviceRecord.ServiceRecordRequestDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.service.ServiceHistoryService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/service-records")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService){
        this.serviceHistoryService = serviceHistoryService;
    }

    // Create Service Record
    @PostMapping("/cars/{carId}/services")
    public ResponseEntity<ServiceRecordResponseDTO> createServiceHistory(@Valid @RequestBody ServiceRecordRequestDTO serviceHistory, @PathVariable Long carId){
        ServiceRecordResponseDTO createdServiceRecord = serviceHistoryService.createServiceHistory(serviceHistory, carId);
        return new ResponseEntity<>(createdServiceRecord, HttpStatus.CREATED);
    }

    // Get all service records
    @GetMapping("/cars/services")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getAllServiceRecords(){
        List<ServiceRecordResponseDTO> allServiceRecords = serviceHistoryService.getAllServiceRecords();
        return ResponseEntity.ok(allServiceRecords);
    }

    // Get service Record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> getServiceRecord(@PathVariable Long id){
        ServiceRecordResponseDTO getServiceRecordById = serviceHistoryService.getServiceHistoryById(id);
        return ResponseEntity.ok(getServiceRecordById);
    }

    // Update service record
    @PutMapping("/cars/{carId}/services/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> updateServiceHistory(@PathVariable Long id, @Valid @RequestBody ServiceRecordRequestDTO serviceHistory, @PathVariable Long carId){
        ServiceRecordResponseDTO updatedServiceRecord = serviceHistoryService.updateServiceHistory(id, serviceHistory, carId);
        return ResponseEntity.ok(updatedServiceRecord);
    }

    // Get service history by car
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCar(@PathVariable Long carId){
        List<ServiceRecordResponseDTO> getServiceHistoryByCar = serviceHistoryService.getServiceHistoryByCar(carId);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    // Get service history by car and date
    @GetMapping("/car/{carId}/date/{date}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCarAndDate(@PathVariable Long carId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate = serviceHistoryService.getServiceHistoryByCarAndDate(carId, date);
        return ResponseEntity.ok(getServiceHistoryByCarAndDate);
    }

    // Get service history by service type
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByServiceType(@PathVariable ServiceType serviceType){
        List<ServiceRecordResponseDTO> getServiceHistoryByServiceType = serviceHistoryService.getServiceHistoryByServiceType(serviceType);
        return ResponseEntity.ok(getServiceHistoryByServiceType);
    }

    // Get service history by category
    @GetMapping("/category/{serviceCategory}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCategory(@PathVariable ServiceCategory serviceCategory){
        List<ServiceRecordResponseDTO> getServiceHistoryByCategory = serviceHistoryService.getServiceHistoryByCategory(serviceCategory);
        return ResponseEntity.ok(getServiceHistoryByCategory);
    }

    // Get service history by service type for a car
    @GetMapping("/car/{carId}/type/{serviceType}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCarAndServiceType(@PathVariable Long carId ,@PathVariable ServiceType serviceType){
        List<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType = serviceHistoryService.getServiceHistoryByCarAndServiceType(carId ,serviceType);
        return ResponseEntity.ok(getServiceHistoryByCarAndServiceType);
    }

    // Get service history by category for a car
    @GetMapping("/car/{carId}/category/{serviceCategory}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCarAndCategory(@PathVariable Long carId , @PathVariable ServiceCategory serviceCategory){
        List<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory = serviceHistoryService.getServiceHistoryByCarAndCategory(carId ,serviceCategory);
        return ResponseEntity.ok(getServiceHistoryByCarAndCategory);
    }



    // Pagination
    // Get service history by car and date
    @GetMapping("/car/{carId}/date/{date}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCarAndDatePaginated(@PathVariable Long carId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestParam(defaultValue = "0") int page,
                                                                                                 @RequestParam(defaultValue = "10") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate = serviceHistoryService.getServiceHistoryByCarAndDate(carId, date, page, size);
        return ResponseEntity.ok(getServiceHistoryByCarAndDate);
    }

    // Get service history by service type
    @GetMapping("/type/{serviceType}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByServiceTypePaginated(@PathVariable ServiceType serviceType, @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "10") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByServiceType = serviceHistoryService.getServiceHistoryByServiceType(serviceType, page, size);
        return ResponseEntity.ok(getServiceHistoryByServiceType);
    }

    // Get service history by car
    @GetMapping("/car/{carId}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCarPaginated(@PathVariable Long carId, @RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "5") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByCar = serviceHistoryService.getServiceHistoryByCar(carId, page, size);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    // Get service history by category
    @GetMapping("/category/{serviceCategory}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCategoryPaginated(@PathVariable ServiceCategory serviceCategory, @RequestParam(defaultValue = "0") int page,
                                                                                               @RequestParam(defaultValue = "5") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByCategory = serviceHistoryService.getServiceHistoryByCategory(serviceCategory, page, size);
        return ResponseEntity.ok(getServiceHistoryByCategory);
    }

    // Get service history by service type for a car
    @GetMapping("/car/{carId}/type/{serviceType}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCarAndServiceTypePaginated(@PathVariable Long carId ,@PathVariable ServiceType serviceType, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType = serviceHistoryService.getServiceHistoryByCarAndServiceType(carId ,serviceType, page, size);
        return ResponseEntity.ok(getServiceHistoryByCarAndServiceType);
    }

    // Get service history by category for a car
    @GetMapping("/car/{carId}/category/{serviceCategory}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCarAndCategoryPaginated(@PathVariable Long carId , @PathVariable ServiceCategory serviceCategory,  @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory = serviceHistoryService.getServiceHistoryByCarAndCategory(carId ,serviceCategory,  page, size);
        return ResponseEntity.ok(getServiceHistoryByCarAndCategory);
    }
}
