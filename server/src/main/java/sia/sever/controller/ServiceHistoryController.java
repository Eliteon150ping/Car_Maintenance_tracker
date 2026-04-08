package sia.sever.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.service.ServiceHistoryService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/service-history")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService){
        this.serviceHistoryService = serviceHistoryService;
    }

    // Create Service Record
    @PostMapping("/car/{carId}")
    public ResponseEntity<ServiceRecordResponseDTO> createServiceHistory(@RequestBody ServiceHistory serviceHistory, @PathVariable Long carId){
        ServiceRecordResponseDTO createdServiceRecord = serviceHistoryService.createServiceHistory(serviceHistory, carId);
        return new ResponseEntity<>(createdServiceRecord, HttpStatus.CREATED);
    }

    // Get all service records
    @GetMapping
    public ResponseEntity<List<ServiceRecordResponseDTO>> getAllServiceRecords(){
        List<ServiceRecordResponseDTO> allServiceRecords = serviceHistoryService.getAllServiceRecords();
        return ResponseEntity.ok(allServiceRecords);
    }

    // Get service Record by ID
    @GetMapping("/recordId/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> getServiceHistoryById(@PathVariable Long id){
        ServiceRecordResponseDTO getServiceRecordById = serviceHistoryService.getServiceHistoryById(id);
        return ResponseEntity.ok(getServiceRecordById);
    }

    // Update service record
    @PutMapping("/car/{carId}/update/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> updateServiceHistory(@PathVariable Long id, @RequestBody ServiceHistory serviceHistory, @PathVariable Long carId){
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
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCarAndDate(@PathVariable Long carId, @PathVariable LocalDate date){
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
}
