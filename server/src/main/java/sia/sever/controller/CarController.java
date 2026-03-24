package sia.sever.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.entity.Car;
import sia.sever.service.CarService;
import sia.sever.dto.car.CarResponseDTO;
import java.util.List;

@RestController
@RequestMapping("/api/my-cars") // Insert API Endpoint here...
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
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@RequestBody Car car) {
        CarResponseDTO createdCar = carService.createCar(car);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    // Get all cars
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getALlCars(){
        List<CarResponseDTO> allCars = carService.getAllCars();
        return ResponseEntity.ok(allCars);
    }

    // Get car by id
    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable Long id){
        CarResponseDTO getCarById = carService.getCarById(id);
        return ResponseEntity.ok(getCarById);
    }

    // Update car by id
    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDTO> updateCarById(@PathVariable Long id, @RequestBody Car car){
        CarResponseDTO updatedCar = carService.updateCar(id, car);
        return ResponseEntity.ok(updatedCar);
    }

    // Delete car by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // Filter by brand/model/year
    @GetMapping("/filter")
    public ResponseEntity<List<CarResponseDTO>> filterByBrandModelYear(@RequestParam(value = "brand", required = false) String brand,
                                                                       @RequestParam(value = "model", required = false) String model,
                                                                       @RequestParam(value = "year", required = false)  Integer year)
    {
        List<CarResponseDTO> carsByBrandModelYear = carService.getAllCarsByBrandAndModelAndYear(brand, model, year);
        return new ResponseEntity<>(carsByBrandModelYear, HttpStatus.OK);
    }
}
