package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.entity.Car;
import sia.sever.repository.CarRepository;
import sia.sever.specification.CarSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository){
        this.carRepository = carRepository;
    }

    // Mapper for DTO and service
    public CarResponseDTO CarResponseDTO(Car car){
        return new CarResponseDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getColour()
                                  , car.getCurrentMileage());
    }

    // Create a car
    @Override
    public CarResponseDTO createCar(Car car){
        Car savedCar = carRepository.save(car);
        return CarResponseDTO(savedCar);
    }

    // Get all cars
    @Override
    public List<CarResponseDTO> getAllCars(){
        List<Car> findAllCars = carRepository.findAll();
        return findAllCars.stream()
                          .map(this::CarResponseDTO)
                          .collect(Collectors.toList());
    }

    // Update an existing car
    @Override
    public CarResponseDTO updateCar(Long id, Car updatedCar){

        // First Check if an entity exists before continuing with updating
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot update car not found with ID: " + id));

        // Check if the new mileage is NOT lower than the current mileage
        if(updatedCar.getCurrentMileage() < existingCar.getCurrentMileage()){
            throw new RuntimeException("Updated mileage cannot be less than current mileage");
        }

        // If entity exists then update all its selected fields using the getter and setter methods
        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setYear(updatedCar.getYear());
        existingCar.setColour(updatedCar.getColour());
        existingCar.setCurrentMileage(updatedCar.getCurrentMileage());

        Car updatedCarInfo = carRepository.save(existingCar);
        return CarResponseDTO(updatedCarInfo);
    }

    // Delete a car
    @Override
    public void deleteCar(Long id){
        // First check if an entity exists before trying to delete it
        if(!carRepository.existsById(id)){
            throw new RuntimeException("Cannot delete car not found with ID" + id);
        }
       carRepository.deleteById(id);
    }

    // Find a specific car by id
    @Override
    public CarResponseDTO getCarById(Long id){
        Car findCarById = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with ID" + id));
        return CarResponseDTO(findCarById);
    }

    // Dynamic filtering method for getting cars by its brand, model or year
    @Override
    public List<CarResponseDTO> getAllCarsByBrandAndModelAndYear(String brand, String model, Integer year){

        Specification<Car> spec = Specification.where(CarSpecification.hasBrand(brand))
                                                     .and(CarSpecification.hasModel(model))
                                                     .and(CarSpecification.hasYear(year));

        List<Car> filterCars = carRepository.findAll(spec);
        return filterCars.stream().map(this::CarResponseDTO).collect(Collectors.toList());
    }
}




