package sia.sever.controller;

import org.hibernate.annotations.Filter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.entity.Car;
import sia.sever.service.CarService;

import java.util.List;

@RestController("") // Insert frontend url here later...
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
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car createdCar = carService.createCar(car);
        return new ResponseEntity<>(createdCar, HttpStatus.CREATED);
    }

    // Get all cars
    @GetMapping
    public ResponseEntity<List<Car>> getALlCars(){
        List<Car> allCars = carService.getAllCars();
        return new ResponseEntity<>(allCars, HttpStatus.OK);
    }

    // Get car by id
    @GetMapping()
    public ResponseEntity<Car> getCarById(@PathVariable Long id){
        Car getCarById = carService.getCarById(id);
        return ResponseEntity.ok(getCarById);
    }

    // Update car by id
    @PutMapping()
    public ResponseEntity<Car> updateCarById(@PathVariable Long id, @RequestBody Car car){
        Car updatedCar = carService.updateCar(id, car);
        return ResponseEntity.ok(updatedCar);
    }

    // Delete car by id
    @DeleteMapping()
    public ResponseEntity<Car> deleteCarById(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // Filter by brand/model/year
    @Filter(name = "")
    public ResponseEntity<List<Car>> filterByBrand(String brand){
        List<Car> carsByBrand = carService.getAllCarsByBrand(brand);
        return new ResponseEntity<>(carsByBrand, HttpStatus.OK);
    }

    @Filter(name = "")
    public ResponseEntity<List<Car>> filterByModel(String model){
        List<Car> carsByModel = carService.getAllCarsByBrand(model);
        return new ResponseEntity<>(carsByModel, HttpStatus.OK);
    }

    @Filter(name = "")
    public ResponseEntity<List<Car>> filterByYear(int year){
        List<Car> carsByYear = carService.getAllCarsByBrand(String.valueOf(year));
        return new ResponseEntity<>(carsByYear, HttpStatus.OK);
    }
}
