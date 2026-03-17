package sia.sever.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @PostMapping
    public ResponseEntity<ServiceHistory> createServiceHistory(@RequestBody ServiceHistory serviceHistory, @RequestParam Long carId){
        ServiceHistory createdServiceRecord = serviceHistoryService.createServiceHistory(serviceHistory, carId);
        return new ResponseEntity<>(createdServiceRecord, HttpStatus.CREATED);
    }

    // Get all service records
    @GetMapping
    public ResponseEntity<List<ServiceHistory>> getAllServiceRecords(){
        List<ServiceHistory> allServiceRecords = serviceHistoryService.getAllServiceRecords();
        return ResponseEntity.ok(allServiceRecords);
    }

    // Get service Record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceHistory> getServiceHistoryById(@PathVariable Long id){
        ServiceHistory getServiceRecordById = serviceHistoryService.getServiceHistoryById(id);
        return ResponseEntity.ok(getServiceRecordById);
    }

//    // Update service record
//    @PutMapping("/car/{carId}/update/{id}")
//    public ResponseEntity<ServiceHistory> updateServiceHistory(@PathVariable Long id, @RequestBody ServiceHistory serviceHistory, @PathVariable Long carId){
//        ServiceHistory updatedServiceRecord = serviceHistoryService.updateServiceHistory(id, serviceHistory, carId);
//        return ResponseEntity.ok(updatedServiceRecord);
//    }

    // Get service history by car
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<ServiceHistory>> getServiceHistoryByCar(@PathVariable Long carId){
        List<ServiceHistory> getServiceHistoryByCar = serviceHistoryService.getServiceHistoryByCar(carId);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    // Get service history by car and date
    @GetMapping("/car/{carId}/date/{date}")
    public ResponseEntity<List<ServiceHistory>> getServiceHistoryByCarAndDate(@PathVariable Long carId, @PathVariable LocalDate date){
        List<ServiceHistory> getServiceHistoryByCarAndDate = serviceHistoryService.getServiceHistoryByCarAndDate(carId, date);
        return ResponseEntity.ok(getServiceHistoryByCarAndDate);
    }

    // Get service history by service type
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<ServiceHistory>> getServiceHistoryByServiceType(@PathVariable ServiceType serviceType){
        List<ServiceHistory> getServiceHistoryByServiceType = serviceHistoryService.getServiceHistoryByServiceType(serviceType);
        return ResponseEntity.ok(getServiceHistoryByServiceType);
    }

    // Get service history by category
    @GetMapping("/category/{serviceCategory}")
    public ResponseEntity<List<ServiceHistory>> getServiceHistoryByCategory(@PathVariable ServiceCategory serviceCategory){
        List<ServiceHistory> getServiceHistoryByCategory = serviceHistoryService.getServiceHistoryByCategory(serviceCategory);
        return ResponseEntity.ok(getServiceHistoryByCategory);
    }
}
