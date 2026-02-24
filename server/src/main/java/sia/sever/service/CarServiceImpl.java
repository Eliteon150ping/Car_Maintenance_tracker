package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.Car;
import sia.sever.repository.CarRepository;

import java.util.List;


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

    // Create a car
    @Override
    public Car createCar(Car car){
        return carRepository.save(car);
    }

    // Get all cars
    @Override
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    // Update an existing car
    @Override
    public Car updateCar(Long id, Car updatedCar){
        // First Check if an entity exists before continuing with updating
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot update car not found with ID: " + id));
        // If entity exists then update all its selected fields using the getter and setter methods
        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setYear(updatedCar.getYear());
        existingCar.setColour(updatedCar.getColour());
        existingCar.setMileage(updatedCar.getMileage());
        return carRepository.save(existingCar);
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
    public Car getCarById(Long id){
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with ID" + id));
    }

    // Dynamic filtering method for getting cars by its brand, model or year
    @Override
    public List<Car> getAllCarsByBrandAndModelAndYear(String brand, String model, Integer year){
        if(brand != null && model != null && year != null){
            return carRepository.findByBrandContainingIgnoreCaseAndModelContainingIgnoreCaseAndYear(brand, model, year);
        }else if(brand != null && model != null){
            return carRepository.findByBrandContainingIgnoreCaseAndModelContainingIgnoreCase(brand, model);
        }else if(brand != null && year != null){
            return carRepository.findByBrandContainingIgnoreCaseAndYear(brand, year);
        } else if(model != null && year != null){
            return carRepository.findByModelContainingIgnoreCaseAndYear(model, year);
        }else if(brand != null){
            return carRepository.findByBrandContainingIgnoreCase(brand);
        } else if(model != null){
            return carRepository.findByModelContainingIgnoreCase(model);
        }else if(year != null){
            return carRepository.findByYear(year);
        }else {
            return carRepository.findAll();
        }
    }
}




