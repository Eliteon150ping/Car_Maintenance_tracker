package sia.sever.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import java.util.List;

@RestController
@RequestMapping("/api/lookups")
public class LookUpController {

    // Send service type enums to frontend
    @GetMapping("/service-types")
    public ResponseEntity<List<ServiceType>> getAllServiceTypes(){
        List<ServiceType> getAllServiceTypes = List.of(ServiceType.values());
        return ResponseEntity.ok(getAllServiceTypes);
    }

    // Send service category enums to frontend
    @GetMapping("/service-categories")
    public ResponseEntity<List<ServiceCategory>> getAllServiceCategories(){
        List<ServiceCategory> getAllServiceCategories = List.of(ServiceCategory.values());
        return ResponseEntity.ok(getAllServiceCategories);
    }
}
