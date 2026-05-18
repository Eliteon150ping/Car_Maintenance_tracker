package sia.sever.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sia.sever.dto.lookup.ServiceCategoryLookupDTO;
import sia.sever.dto.lookup.ServiceTypeLookupDTO;
import sia.sever.enums.ServiceType;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/lookups")
public class LookUpController {

    // Send service type enums to frontend
    @GetMapping("/service-types")
    public ResponseEntity<List<ServiceTypeLookupDTO>> getAllServiceTypes() {
        List<ServiceTypeLookupDTO> serviceTypes = Arrays.stream(ServiceType.values())
                .map(serviceType -> new ServiceTypeLookupDTO(
                        serviceType.name(),
                        serviceType.getDisplayName(),
                        serviceType.getServiceCategory(),
                        serviceType.getIntervalKm(),
                        serviceType.getIntervalMonths()
                ))
                .toList();
        return ResponseEntity.ok(serviceTypes);
    }

    // Send service category enums to frontend
    @GetMapping("/service-categories")
    public ResponseEntity<List<ServiceCategoryLookupDTO>> getAllServiceCategories() {
        List<ServiceCategoryLookupDTO> serviceCategories = Arrays.stream(ServiceType.values())
                .map(serviceCategory -> new ServiceCategoryLookupDTO(
                        serviceCategory.name(),
                        serviceCategory.getDisplayName()
                ))
                .toList();
        return ResponseEntity.ok(serviceCategories);
    }
}
