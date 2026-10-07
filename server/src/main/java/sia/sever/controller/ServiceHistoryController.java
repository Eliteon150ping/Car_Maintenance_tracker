package sia.sever.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.LatestServiceInfoDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.service.ServiceHistoryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/service-records")
public class ServiceHistoryController {

    private final ServiceHistoryService serviceHistoryService;

    public ServiceHistoryController(ServiceHistoryService serviceHistoryService) {
        this.serviceHistoryService = serviceHistoryService;
    }

    // Create Service Record
    @Operation(
            summary = "Create a service record",
            description = "Creates a new service record for the authenticated user's car.",
            tags = {"Service Records"}
    )
    @PostMapping("/car/{carId}")
    public ResponseEntity<ServiceRecordResponseDTO> createServiceRecord(@Valid @RequestBody CreateServiceRecordDTO serviceHistory, @PathVariable Long carId) {
        ServiceRecordResponseDTO createdServiceRecord = serviceHistoryService.createServiceHistory(serviceHistory, carId);
        return new ResponseEntity<>(createdServiceRecord, HttpStatus.CREATED);
    }

    // Validate duplicate service record before confirmation
    @Operation(
            summary = "Validate duplicate service record",
            description = "Checks whether a service record with the same type, date, and mileage already exists for the authenticated user's car.",
            tags = {"Service Records"}
    )
    @PostMapping("/car/{carId}/validate-duplicate")
    public ResponseEntity<Void> validateDuplicateRecord(
            @PathVariable Long carId,
            @RequestParam ServiceType serviceType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate,
            @RequestParam int mileageAtService) {

        serviceHistoryService.validateDuplicateRecord(
                carId,
                serviceType,
                serviceDate,
                mileageAtService
        );

        return ResponseEntity.ok().build();
    }

    // Get all service records
    @Operation(
            summary = "Get all service records",
            description = "Gets all service records for an authenticated user for all their cars.",
            tags = {"Service Records"}
    )
    @GetMapping
    public ResponseEntity<List<ServiceRecordResponseDTO>> getAllServiceRecords() {
        List<ServiceRecordResponseDTO> allServiceRecords = serviceHistoryService.getAllServiceRecords();
        return ResponseEntity.ok(allServiceRecords);
    }

    // Update service record
    @Operation(
            summary = "Update a service record",
            description = "Update a service record an authenticated user's car.",
            tags = {"Service Records"}
    )
    @PutMapping("/car/{carId}/service/{id}")
    public ResponseEntity<ServiceRecordResponseDTO> updateServiceRecord(@PathVariable Long id, @Valid @RequestBody UpdateServiceRecordDTO serviceHistory, @PathVariable Long carId) {
        ServiceRecordResponseDTO updatedServiceRecord = serviceHistoryService.updateServiceHistory(id, serviceHistory, carId);
        return ResponseEntity.ok(updatedServiceRecord);
    }

    // Get service history by car
    @Operation(
            summary = "Get service history by car",
            description = "Get all service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/car/{carId}")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getServiceHistoryByCar(@PathVariable Long carId) {
        List<ServiceRecordResponseDTO> getServiceHistoryByCar = serviceHistoryService.getServiceHistoryByCar(carId);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    @Operation(
            summary = "Get latest service information",
            description = "Gets the latest service mileage and service date for the authenticated user's car.",
            tags = {"Service Records"}
    )
    @GetMapping("/car/{carId}/latest-info")
    public ResponseEntity<LatestServiceInfoDTO> getLatestServiceInfo(
            @PathVariable Long carId) {

        LatestServiceInfoDTO latestServiceInfo =
                serviceHistoryService.getLatestServiceInfo(carId);

        return ResponseEntity.ok(latestServiceInfo);
    }

    // Filter and show only Upcoming services
    @Operation(
            summary = "Filter and show only Upcoming services",
            description = "Get all upcoming service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/upcoming")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getUpcomingServiceRecords() {
        List<ServiceRecordResponseDTO> getAllUpcomingRecords = serviceHistoryService.getUpcomingServiceRecords();
        return ResponseEntity.ok(getAllUpcomingRecords);
    }

    // Filter and show only Overdue services
    @Operation(
            summary = "Filter and show only Overdue services",
            description = "Get all Overdue service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/overdue")
    public ResponseEntity<List<ServiceRecordResponseDTO>> getOverdueServiceRecords() {
        List<ServiceRecordResponseDTO> getAllOverdueRecords = serviceHistoryService.getOverdueServiceRecords();
        return ResponseEntity.ok(getAllOverdueRecords);
    }

    // Pagination
    // Get service history by car
    @Operation(
            summary = "Get service history by car(pageable)",
            description = "Get all service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/car/{carId}/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getServiceHistoryByCarPaginated(
            @RequestParam(required = false) List<ServiceType> serviceTypes,
            @RequestParam(required = false) List<ServiceCategory> serviceCategories,
            @PathVariable Long carId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (serviceTypes == null) {
            serviceTypes = new ArrayList<>();
        }

        if (serviceCategories == null) {
            serviceCategories = new ArrayList<>();
        }

        Page<ServiceRecordResponseDTO> getServiceHistoryByCar = serviceHistoryService.getServiceHistoryByCar(serviceTypes, serviceCategories, carId, page, size);
        return ResponseEntity.ok(getServiceHistoryByCar);
    }

    // Get all service records for all user owned cars
    @Operation(
            summary = "Get all service records(pageable)",
            description = "Gets all service records for an authenticated user for all their cars.",
            tags = {"Service Records"}
    )
    @GetMapping("/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getAllServiceRecords(
            @RequestParam(required = false) List<ServiceType> serviceTypes,
            @RequestParam(required = false) List<ServiceCategory> serviceCategories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (serviceTypes == null) {
            serviceTypes = new ArrayList<>();
        }

        if (serviceCategories == null) {
            serviceCategories = new ArrayList<>();
        }

        return ResponseEntity.ok(serviceHistoryService.getAllServiceRecords(serviceTypes, serviceCategories, page, size));
    }

    // Filter and show only Upcoming services
    @Operation(
            summary = "Filter and show only Upcoming services",
            description = "Get all upcoming service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/upcoming/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getUpcomingServiceRecords(
            @RequestParam(required = false) List<ServiceType> serviceTypes,
            @RequestParam(required = false) List<ServiceCategory> serviceCategories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (serviceTypes == null) {
            serviceTypes = new ArrayList<>();
        }

        if (serviceCategories == null) {
            serviceCategories = new ArrayList<>();
        }

        return ResponseEntity.ok(
                serviceHistoryService.getUpcomingServiceRecords(serviceTypes, serviceCategories, page, size)
        );
    }

    // Filter and show only Overdue services
    @Operation(
            summary = "Filter and show only Overdue services(pageable)",
            description = "Get all Overdue service records for a particular car owned by a user",
            tags = {"Service Records"}
    )
    @GetMapping("/overdue/page")
    public ResponseEntity<Page<ServiceRecordResponseDTO>> getOverdueServiceRecords(
            @RequestParam(required = false) List<ServiceType> serviceTypes,
            @RequestParam(required = false) List<ServiceCategory> serviceCategories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (serviceTypes == null) {
            serviceTypes = new ArrayList<>();
        }

        if (serviceCategories == null) {
            serviceCategories = new ArrayList<>();
        }

        return ResponseEntity.ok(
                serviceHistoryService.getOverdueServiceRecords(serviceTypes, serviceCategories, page, size)
        );
    }
}
