package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.UnauthorizedException;
import sia.sever.repository.CarRepository;
import sia.sever.repository.UserRepository;
import sia.sever.specification.CarSpecification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CarServiceImpl implements CarService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    // Take the value of file.upload-dir from application.properties and put it into this variable.
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    // Mapper for DTO and service to return a car object to the frontend
    private CarResponseDTO mapToCarResponseDTO(Car car) {
        return new CarResponseDTO(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColour(),
                car.getCurrentMileage(),
                car.getCarImageUrl()
        );
    }

    // Mapper for carRequestDTO to convert to a car object
    private Car mapToEntity(CreateCarDTO createCarDTO) {
        Car car = new Car();
        car.setBrand(createCarDTO.getBrand());
        car.setModel(createCarDTO.getModel());
        car.setYear(createCarDTO.getYear());
        car.setColour(createCarDTO.getColour());
        car.setCurrentMileage(createCarDTO.getCurrentMileage());
        return car;
    }

    // Mapper to apply update DTO changes to an existing managed entity
    private void updateEntityFromDTO(UpdateCarDTO updateCarDTO, Car existingCar, String imageUrl) {

        if (updateCarDTO.getColour() != null) {
            existingCar.setColour(updateCarDTO.getColour());
        }
        if (updateCarDTO.getCurrentMileage() != null) {
            existingCar.setCurrentMileage(updateCarDTO.getCurrentMileage());
        }

        if (Boolean.TRUE.equals(updateCarDTO.getRemoveImage())) {
            existingCar.setCarImageUrl(null);
        } else if (imageUrl != null) {
            existingCar.setCarImageUrl(imageUrl);
        }
    }

    // Create a car
    @Override
    public CarResponseDTO createCar(CreateCarDTO car, MultipartFile image) throws IOException {
        Car convertToEntity = mapToEntity(car);
        User user = getAuthenticatedUser();
        convertToEntity.setUser(user);

        String imageUrl = saveImage(image);
        if (imageUrl != null) {
            convertToEntity.setCarImageUrl(imageUrl);
        }

        Car savedCar = carRepository.save(convertToEntity);
        return mapToCarResponseDTO(savedCar);
    }

    // This helps when a user adds an image of the car by taking the original
    // file name and keeping only the ".png or any original file extension" and concat-ing
    // a randomly generated uuid characters to .png.
    private String generateUniqueFileName(MultipartFile image) {

        String originalFileName = image.getOriginalFilename();

        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return UUID.randomUUID() + extension;
    }

    // This generates a unique filename by keeping the original file extension
    // and adding it to a randomly generated UUID.
    private Path createFilePath(String fileName) {
        return Paths.get(uploadDir).resolve(fileName);
    }

    private String saveImage(MultipartFile image) throws IOException {

        // No image was provided.
        if (image == null || image.isEmpty()) {
            return null;
        }

        String fileName = generateUniqueFileName(image);

        Path filePath = createFilePath(fileName);

        image.transferTo(filePath);

        return "/uploads/cars/" + fileName;
    }

    private void deleteImage(String imageUrl) throws IOException {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        // Get the file name from the stored URL.
        String fileName = Paths.get(imageUrl).getFileName().toString();

        // Build the path to the image inside the upload directory.
        Path filePath = createFilePath(fileName);

        // Delete the file if it exists.
        java.nio.file.Files.deleteIfExists(filePath);
    }

    // Get all cars
    @Override
    public List<CarResponseDTO> getAllCars() {
        User user = getAuthenticatedUser();
        List<Car> findAllCars = carRepository.findAllByUserOrderByIdAsc(user);
        return findAllCars.stream()
                .map(this::mapToCarResponseDTO)
                .collect(Collectors.toList());
    }

    // Update an existing car
    @Override
    public CarResponseDTO updateCar(Long id, UpdateCarDTO updatedCar, MultipartFile image) throws IOException {

        User user = getAuthenticatedUser();
        Map<String, String> errors = new HashMap<>();

        // First Check if an entity exists before continuing with updating
        Car existingCar = getUserCar(id, user);

        String oldImageUrl = existingCar.getCarImageUrl();
        String imageUrl = saveImage(image);

        // Check if the new mileage is NOT lower than the current mileage
        if (updatedCar.getCurrentMileage() != null && updatedCar.getCurrentMileage()
                < existingCar.getCurrentMileage()) {
            errors.put("currentMileage", "Updated mileage cannot be less than current mileage");
        }
        if (!errors.isEmpty()) {
            throw new InvalidMileageException("Updated failed", errors);
        }

        updateEntityFromDTO(updatedCar, existingCar, imageUrl);

        if (oldImageUrl != null &&
                (Boolean.TRUE.equals(updatedCar.getRemoveImage()) || imageUrl != null)) {

            deleteImage(oldImageUrl);
        }

        Car updatedCarInfo = carRepository.save(existingCar);
        return mapToCarResponseDTO(updatedCarInfo);
    }

    // Validate the updated new mileage to display in the frontend immediately before the confirmation
    // box
    @Override
    public void validateUpdateMileage(Long id, int updateMileage) {

        User user = getAuthenticatedUser();
        Map<String, String> errors = new HashMap<>();

        // First Check if an entity exists before continuing with updating
        Car existingCar = getUserCar(id, user);

        // Check if the new mileage is NOT lower than the current mileage
        if (updateMileage < existingCar.getCurrentMileage()) {
            errors.put("currentMileage", "Updated mileage cannot be less than current mileage");
        }

        if (!errors.isEmpty()) {
            throw new InvalidMileageException("Updated failed", errors);
        }
    }

    // Delete a car
    @Override
    public void deleteCar(Long id) throws IOException{
        User user = getAuthenticatedUser();
        // First check if an entity exists before trying to delete it
        Car existingCar = getUserCar(id, user);
        deleteImage(existingCar.getCarImageUrl());
        carRepository.delete(existingCar);
    }

    // Find a specific car by id
    @Override
    public CarResponseDTO getCarById(Long id) {
        User user = getAuthenticatedUser();
        Car findCarById = getUserCar(id, user);
        return mapToCarResponseDTO(findCarById);
    }

    // Search cars for filtering
    @Override
    public List<CarResponseDTO> searchCars(String search) {
        User user = getAuthenticatedUser();
        Specification<Car> spec = Specification
                .where(CarSpecification.search(search))
                .and(CarSpecification.hasUser(user));

        List<Car> filteredCars = carRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));

        return filteredCars.stream()
                .map(this::mapToCarResponseDTO)
                .collect(Collectors.toList());
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