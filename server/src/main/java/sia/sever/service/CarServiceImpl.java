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
import sia.sever.exception.ValidationException;
import sia.sever.repository.CarRepository;
import sia.sever.repository.UserRepository;
import sia.sever.specification.CarSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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

    private String detectImageFormat(byte[] imageBytes) {

        // JPEG signature
        if (imageBytes.length >= 3 &&
                (imageBytes[0] & 0xFF) == 0xFF &&
                (imageBytes[1] & 0xFF) == 0xD8 &&
                (imageBytes[2] & 0xFF) == 0xFF) {
            return "jpg";
        }

        // PNG signature
        if (imageBytes.length >= 8 &&
                (imageBytes[0] & 0xFF) == 0x89 &&
                imageBytes[1] == 0x50 &&
                imageBytes[2] == 0x4E &&
                imageBytes[3] == 0x47 &&
                imageBytes[4] == 0x0D &&
                imageBytes[5] == 0x0A &&
                imageBytes[6] == 0x1A &&
                imageBytes[7] == 0x0A) {
            return "png";
        }

        // WebP signature
        if (imageBytes.length >= 12 &&
                imageBytes[0] == 'R' &&
                imageBytes[1] == 'I' &&
                imageBytes[2] == 'F' &&
                imageBytes[3] == 'F' &&
                imageBytes[8] == 'W' &&
                imageBytes[9] == 'E' &&
                imageBytes[10] == 'B' &&
                imageBytes[11] == 'P') {
            return "webp";
        }

        return null;
    }

    // Generate a unique filename using the detected image format.
    private String generateUniqueFileName(String imageFormat) {
        return UUID.randomUUID() + "." + imageFormat;
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

        // Reject images larger than 10 MB.
        if (image.getSize() > 10 * 1024 * 1024) {
            throw new ValidationException(
                    "Image upload failed",
                    Map.of("image", "Image size cannot exceed 10 MB")
            );
        }

        // Check the image's actual contents.
        byte[] imageBytes = image.getBytes();
        String imageFormat = detectImageFormat(imageBytes);

        if (imageFormat == null) {
            throw new ValidationException(
                    "Image upload failed",
                    Map.of("image", "Only JPEG, PNG and WebP images are supported")
            );
        }

        // Check whether the uploaded image can actually be decoded.
        int maxDimension = 6000;
        BufferedImage decodedImage;

        try (ImageInputStream inputStream = ImageIO.createImageInputStream(
                new ByteArrayInputStream(imageBytes))) {

            if (inputStream == null) {
                throw new ValidationException(
                        "Image upload failed",
                        Map.of("image", "The uploaded image is invalid or corrupted")
                );
            }

            // Find an ImageIO reader that supports the uploaded format.
            Iterator<ImageReader> readers = ImageIO.getImageReaders(inputStream);

            if (!readers.hasNext()) {
                throw new ValidationException(
                        "Image upload failed",
                        Map.of("image", "The uploaded image is invalid or corrupted")
                );
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(inputStream, true, true);

                // Check dimensions before fully decoding the image.
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                long totalPixels = (long) width * height;

                if (width > maxDimension || height > maxDimension ||
                        totalPixels > 20_000_000L) {
                    throw new ValidationException(
                            "Image upload failed",
                            Map.of("image", "Image dimensions cannot exceed 6000 x 6000 pixels")
                    );
                }

                // Now fully decode the image.
                decodedImage = reader.read(0);

                if (decodedImage == null) {
                    throw new ValidationException(
                            "Image upload failed",
                            Map.of("image", "The uploaded image is invalid or corrupted")
                    );
                }

            } finally {
                reader.dispose();
            }

        } catch (IOException e) {
            throw new ValidationException(
                    "Image upload failed",
                    Map.of("image", "The uploaded image is invalid or corrupted")
            );
        }

        // Generate a filename using the detected image format.
        String fileName = generateUniqueFileName(imageFormat);

        // Create the upload directory if it doesn't exist.
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        // Save the image.
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, imageBytes);

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

        // Check if the new mileage is NOT lower than the current mileage
        if (updatedCar.getCurrentMileage() != null && updatedCar.getCurrentMileage()
                < existingCar.getCurrentMileage()) {
            errors.put("currentMileage", "Updated mileage cannot be less than current mileage");
        }
        if (!errors.isEmpty()) {
            throw new InvalidMileageException("Updated failed", errors);
        }

        String oldImageUrl = existingCar.getCarImageUrl();
        String imageUrl = saveImage(image);

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
    public void deleteCar(Long id) throws IOException {
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