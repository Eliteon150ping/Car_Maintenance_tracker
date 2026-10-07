package sia.sever.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.MileageValidationDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.service.CarService;
import sia.sever.dto.car.CarResponseDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/my-cars") // Insert API Endpoint here...
@CrossOrigin(origins = "http://localhost:5173") // A quick fix used to connect to the frontend on different port numbers
public class CarController {

 /*

    Controller should:
    1) NOT talk to repository directly
    2) NOT contain business logic
    3) ONLY call service methods
    4) Return responses
    Think of controller as: The receptionist that forwards requests to the brain (service)

     Important Rule ->
     When building controller:
     1) Service handles logic
     2) Controller handles HTTP
     3) Do not mix responsibilities.                                                                      */

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    // Create car
    @Operation(
            summary = "Create car",
            description = "Create a new car and save it too an authenticated user",
            tags = {"Cars"}
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarResponseDTO> createCar(
            @Valid @ModelAttribute CreateCarDTO car,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        CarResponseDTO createdCar = carService.createCar(car, image);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    // Get all cars
    @Operation(
            summary = "Get all cars",
            description = "Get all cars for an authenticated user",
            tags = {"Cars"}
    )
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<CarResponseDTO> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    @Operation(
            summary = "Search for a car",
            description = "Search for an authenticated user",
            tags = {"Cars"}
    )
    @GetMapping("/search")
    public ResponseEntity<List<CarResponseDTO>> searchCars(
            @RequestParam(value = "search", required = false) String search) {

        List<CarResponseDTO> searchedCars = carService.searchCars(search);
        return ResponseEntity.ok(searchedCars);
    }

    // Get car by id
    @Operation(
            summary = "Get car by its ID",
            description = "Get car by its ID for an authenticated user",
            tags = {"Cars"}
    )
    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable Long id) {
        CarResponseDTO getCarById = carService.getCarById(id);
        return ResponseEntity.ok(getCarById);
    }

    // Update car by id
    @Operation(
            summary = "Update car by its ID",
            description = "Update car by its ID for an authenticated user",
            tags = {"Cars"}
    )
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarResponseDTO> updateCarById(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateCarDTO car,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException{

        CarResponseDTO updatedCar = carService.updateCar(id, car, image);
        return ResponseEntity.ok(updatedCar);
    }

    // Check the mileage before showing the confirmation box(insane how all this needs to be done for that)
    @Operation(
            summary = "Check car's mileage before showing the confirmation box",
            description = "Check car's mileage before showing the confirmation box for an authenticated user",
            tags = {"Cars"}
    )
    @PutMapping("/{id}/mileage")
    public ResponseEntity<Void> validateMileage(@PathVariable Long id, @RequestBody MileageValidationDTO mileage) {
        carService.validateUpdateMileage(id, mileage.getMileage());
        return ResponseEntity.noContent().build();
    }

    // Delete car by id
    @Operation(
            summary = "Delete car by its id",
            description = "Delete car by its id for an authenticated user",
            tags = {"Cars"}
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id) throws IOException{
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
