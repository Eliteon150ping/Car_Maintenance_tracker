package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sia.sever.dto.car.CarRequestDTO;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.UnauthorizedException;
import sia.sever.repository.CarRepository;
import sia.sever.repository.UserRepository;
import sia.sever.specification.CarSpecification;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    // Mapper for DTO and service to return a car object to the frontend
    private CarResponseDTO mapToCarResponseDTO(Car car) {
        return new CarResponseDTO(car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getColour()
                , car.getCurrentMileage());
    }

    // Mapper for carRequestDTO to convert to a car object
    private Car mapToEntity(CarRequestDTO carRequestDTO) {
        Car car = new Car();
        car.setBrand(carRequestDTO.getBrand());
        car.setModel(carRequestDTO.getModel());
        car.setYear(carRequestDTO.getYear());
        car.setColour(carRequestDTO.getColour());
        car.setCurrentMileage(carRequestDTO.getCurrentMileage());
        return car;
    }

    // Create a car
    @Override
    public CarResponseDTO createCar(CarRequestDTO car) {
        Car convertToEntity = mapToEntity(car);
        User user = getAuthenticatedUser();
        convertToEntity.setUser(user);
        Car savedCar = carRepository.save(convertToEntity);
        return mapToCarResponseDTO(savedCar);
    }

    // Get all cars
    @Override
    public List<CarResponseDTO> getAllCars() {
        User user = getAuthenticatedUser();
        List<Car> findAllCars = carRepository.findAllByUser(user);
        return findAllCars.stream()
                .map(this::mapToCarResponseDTO)
                .collect(Collectors.toList());
    }

    // Update an existing car
    @Override
    public CarResponseDTO updateCar(Long id, CarRequestDTO updatedCar) {

        Car convertToEntity = mapToEntity(updatedCar);
        User user = getAuthenticatedUser();

        // First Check if an entity exists before continuing with updating
        Car existingCar = getUserCar(id, user);

        // Check if the new mileage is NOT lower than the current mileage
        if (convertToEntity.getCurrentMileage() < existingCar.getCurrentMileage()) {
            throw new InvalidMileageException("Updated mileage cannot be less than current mileage");
        }

        // If entity exists then update all its selected fields using the getter and setter methods
        existingCar.setBrand(convertToEntity.getBrand());
        existingCar.setModel(convertToEntity.getModel());
        existingCar.setYear(convertToEntity.getYear());
        existingCar.setColour(convertToEntity.getColour());
        existingCar.setCurrentMileage(convertToEntity.getCurrentMileage());

        Car updatedCarInfo = carRepository.save(existingCar);
        return mapToCarResponseDTO(updatedCarInfo);
    }

    // Delete a car
    @Override
    public void deleteCar(Long id) {
        User user = getAuthenticatedUser();
        // First check if an entity exists before trying to delete it
        Car existingCar = getUserCar(id, user);
        carRepository.delete(existingCar);
    }

    // Find a specific car by id
    @Override
    public CarResponseDTO getCarById(Long id) {
        User user = getAuthenticatedUser();
        Car findCarById = getUserCar(id, user);
        return mapToCarResponseDTO(findCarById);
    }

    // Dynamic filtering method for getting cars by its brand, model or year
    @Override
    public List<CarResponseDTO> getAllCarsByBrandAndModelAndYear(String brand, String model, Integer year) {
        User user = getAuthenticatedUser();
        Specification<Car> spec = Specification.where(CarSpecification.hasBrand(brand))
                .and(CarSpecification.hasModel(model))
                .and(CarSpecification.hasYear(year))
                .and(CarSpecification.hasUser(user));

        List<Car> filterCars = carRepository.findAll(spec);
        return filterCars.stream().map(this::mapToCarResponseDTO).collect(Collectors.toList());
    }

    // Get authenticated user helper method
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User not authorized");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    // Get user's car helper method
    private Car getUserCar(Long id, User user) {
        return carRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
    }
}