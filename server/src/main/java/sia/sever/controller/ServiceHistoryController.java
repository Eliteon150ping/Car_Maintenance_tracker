package sia.sever.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.service.ServiceHistoryService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/serviceHistory")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService){
        this.serviceHistoryService = serviceHistoryService;
    }

    // Create Service Record
    @PostMapping
    public ResponseEntity<ServiceHistory> createServiceHistory(@RequestBody ServiceHistory serviceHistory){
        ServiceHistory createdServiceRecord = serviceHistoryService.createServiceHistory(serviceHistory);
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

    // Update service record
    @PutMapping("/{id}")
    public ResponseEntity<ServiceHistory> updateServiceHistory(@PathVariable Long id, @RequestBody ServiceHistory serviceHistory){
        ServiceHistory updatedServiceRecord = serviceHistoryService.updateServiceHistory(id, serviceHistory);
        return ResponseEntity.ok(updatedServiceRecord);
    }

    // Get service history by car
    @GetMapping
    public ResponseEntity<ServiceHistory> getServiceHistoryByCar(Car car){
        ServiceHistory getServiceHistoryByCar = (ServiceHistory) serviceHistoryService.getServiceHistoryByCar(car);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    // Get service history by car and date
    @GetMapping
    public ResponseEntity<ServiceHistory> getServiceHistoryByCarAndDate(Car car, LocalDate date){
        ServiceHistory getServiceHistoryByCarAndDate = (ServiceHistory) serviceHistoryService.getServiceHistoryByCarAndDate(car, date);
        return ResponseEntity.ok(getServiceHistoryByCarAndDate);
    }

    // Get service history by service type
    @GetMapping
    public ResponseEntity<ServiceHistory> getServiceHistoryByServiceType(ServiceType serviceType){
        ServiceHistory getServiceHistoryByServiceType = serviceType.getServiceCategory(serviceType);
        return ResponseEntity.ok(getServiceHistoryByServiceType);
    }

    // Get service history by category
    @GetMapping
    public ResponseEntity<ServiceCategory> getServiceHistoryByCategory(ServiceType serviceType){
        ServiceCategory getServiceHistoryByCategory = serviceType.getServiceCategory();
        return ResponseEntity.ok(getServiceHistoryByCategory);
    }
}
