package sia.sever.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.UnauthorizedException;
import sia.sever.repository.CarRepository;
import sia.sever.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.exception.InvalidMileageException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;

@ExtendWith(MockitoExtension.class) // Enables Mockito
class CarServiceImplTest {

    @Mock // Creates a fake repository, so we don't need a real PostgreSQL connection for this unit test.
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks // Creates CarServiceImpl and injects the mocked repositories into it.
    private CarServiceImpl carService;

    @BeforeEach
        // Runs setup before each test.
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@example.com",
                        "password",
                        java.util.List.of()
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validateUpdateMileage_shouldThrowExceptionWhenMileageDecreases() {

        User user = new User();

        Car car = new Car();
        car.setCurrentMileage(100000);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        assertThrows(
                InvalidMileageException.class,
                () -> carService.validateUpdateMileage(1L, 90000)
        );
    }

    @Test
    void validateUpdateMileage_shouldNotThrowExceptionWhenMileageIncreases() {

        User user = new User();

        Car car = new Car();
        car.setCurrentMileage(100000);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        assertDoesNotThrow(
                () -> carService.validateUpdateMileage(1L, 105000)
        );
    }


    @Test
    void validateUpdateMileage_shouldThrowExceptionWhenCarIsNotFound() {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> carService.validateUpdateMileage(1L, 105000)
        );
    }

    @Test
    void validateUpdateMileage_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> carService.validateUpdateMileage(1L, 105000)
        );
    }

    @Test
    void validateUpdateMileage_shouldThrowExceptionWhenUserIsNotFound() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> carService.validateUpdateMileage(1L, 105000)
        );
    }


    @Test
    void validateUpdateMileage_shouldNotThrowExceptionWhenMileageStaysTheSame() {

        User user = new User();

        Car car = new Car();
        car.setCurrentMileage(100000);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        assertDoesNotThrow(
                () -> carService.validateUpdateMileage(1L, 100000)
        );
    }


    @Test
    void getAllCars_shouldReturnAllCarsForAuthenticatedUser() {

        User user = new User();

        Car firstCar = new Car();
        firstCar.setBrand("Toyota");
        firstCar.setModel("Corolla");
        firstCar.setYear(2001);
        firstCar.setColour("Black");
        firstCar.setCurrentMileage(100000);

        Car secondCar = new Car();
        secondCar.setBrand("Honda");
        secondCar.setModel("Civic");
        secondCar.setYear(2010);
        secondCar.setColour("Silver");
        secondCar.setCurrentMileage(150000);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findAllByUserOrderByIdAsc(user))
                .thenReturn(List.of(firstCar, secondCar));

        List<CarResponseDTO> result = carService.getAllCars();

        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        assertEquals("Corolla", result.get(0).getModel());
        assertEquals("Honda", result.get(1).getBrand());
        assertEquals("Civic", result.get(1).getModel());

        verify(carRepository).findAllByUserOrderByIdAsc(user);
    }

    @Test
    void getAllCars_shouldReturnEmptyListWhenUserHasNoCars() {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findAllByUserOrderByIdAsc(user))
                .thenReturn(List.of());

        List<CarResponseDTO> result = carService.getAllCars();

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCars_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> carService.getAllCars()
        );
    }

    @Test
    void getCarById_shouldReturnCarWhenCarExists() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CarResponseDTO result = carService.getCarById(1L);

        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());

        verify(carRepository).findByIdAndUser(1L, user);
    }

    @Test
    void getCarById_shouldThrowExceptionWhenCarIsNotFound() {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> carService.getCarById(1L)
        );

        verify(carRepository).findByIdAndUser(1L, user);
    }

    @Test
    void getCarById_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> carService.getCarById(1L)
        );
    }

    @Test
    void createCar_shouldSaveCarForAuthenticatedUser() throws IOException {

        // Arrange: create the user and car details
        User user = new User();

        CreateCarDTO carDTO = new CreateCarDTO(
                "Toyota",
                "Corolla",
                2001,
                "Silver",
                150000
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        // Simulate the repository saving and returning the car
        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: create the car without an image
        CarResponseDTO result = carService.createCar(carDTO, null);

        // Assert: check the returned car details
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals(2001, result.getYear());
        assertEquals("Silver", result.getColour());
        assertEquals(150000, result.getCurrentMileage());

        // Verify: check that the car was saved for this user
        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

        verify(carRepository).save(carCaptor.capture());

        Car savedCar = carCaptor.getValue();

        assertEquals(user, savedCar.getUser());
    }

    @Test
    void createCar_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        CreateCarDTO carDTO = new CreateCarDTO(
                "Toyota",
                "Corolla",
                2001,
                "Silver",
                150000
        );

        assertThrows(
                UnauthorizedException.class,
                () -> carService.createCar(carDTO, null)
        );

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void createCar_shouldThrowExceptionWhenUserIsNotFound() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(null);

        CreateCarDTO carDTO = new CreateCarDTO(
                "Toyota",
                "Corolla",
                2001,
                "Silver",
                150000
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> carService.createCar(carDTO, null)
        );

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void createCar_shouldSaveCarWithoutImageWhenNoImageIsProvided() throws IOException {

        User user = new User();

        CreateCarDTO carDTO = new CreateCarDTO(
                "Honda",
                "Civic",
                2005,
                "Black",
                180000
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarResponseDTO result = carService.createCar(carDTO, null);

        assertEquals(null, result.getCarImageUrl());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(carCaptor.capture());

        assertEquals(null, carCaptor.getValue().getCarImageUrl());
    }

    @Test
    void createCar_shouldNotSaveImageWhenImageIsEmpty() throws IOException {

        User user = new User();

        CreateCarDTO carDTO = new CreateCarDTO(
                "Toyota",
                "Corolla",
                2001,
                "Silver",
                150000
        );

        MultipartFile image = org.mockito.Mockito.mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(true);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarResponseDTO result = carService.createCar(carDTO, image);

        assertNull(result.getCarImageUrl());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(carCaptor.capture());

        assertNull(carCaptor.getValue().getCarImageUrl());

        verify(image).isEmpty();
        verify(image, never()).transferTo(any(java.nio.file.Path.class));
    }

    @Test
    void createCar_shouldMapAllDTOFieldsToCarEntity() throws IOException {

        User user = new User();

        CreateCarDTO carDTO = new CreateCarDTO(
                "Volkswagen",
                "Golf",
                2010,
                "White",
                200000
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        carService.createCar(carDTO, null);

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(carCaptor.capture());

        Car savedCar = carCaptor.getValue();

        assertEquals("Volkswagen", savedCar.getBrand());
        assertEquals("Golf", savedCar.getModel());
        assertEquals(2010, savedCar.getYear());
        assertEquals("White", savedCar.getColour());
        assertEquals(200000, savedCar.getCurrentMileage());
        assertEquals(user, savedCar.getUser());
    }

    @Test
    void updateCar_shouldUpdateCarSuccessfully() throws IOException {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "blue",
                160000
        );

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarResponseDTO result = carService.updateCar(1L, carDTO, null);

        assertEquals("blue", result.getColour());
        assertEquals(160000, result.getCurrentMileage());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

        verify(carRepository).save(carCaptor.capture());

        Car updatedCar = carCaptor.getValue();

        assertEquals("blue", updatedCar.getColour());
        assertEquals(160000, updatedCar.getCurrentMileage());

    }

    @Test
    void updateCar_shouldUpdateOnlyProvidedFields() throws IOException {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "blue",
                160000
        );

        CarResponseDTO result = carService.updateCar(1L, carDTO, null);

        assertEquals("Corolla", result.getModel());
        assertEquals(2001, result.getYear());
        assertEquals("blue", result.getColour());
        assertEquals(160000, result.getCurrentMileage());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

        verify(carRepository).save(carCaptor.capture());

        Car updatedCar = carCaptor.getValue();

        assertEquals("Corolla", updatedCar.getModel());
        assertEquals(2001, updatedCar.getYear());
        assertEquals("blue", updatedCar.getColour());
        assertEquals(160000, updatedCar.getCurrentMileage());
    }

    @Test
    void updateCar_shouldThrowExceptionWhenCarIsNotFound() throws IOException {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "blue",
                160000
        );

        assertThrows(ResourceNotFoundException.class, () ->
                carService.updateCar(1L, carDTO, null)
        );

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "blue",
                160000
        );

        assertThrows(
                UnauthorizedException.class,
                () -> carService.updateCar(1L, carDTO, null)
        );

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_shouldThrowExceptionWhenMileageDecreases() throws IOException {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "Silver",
                140000
        );

        assertThrows(
                InvalidMileageException.class,
                () -> carService.updateCar(1L, carDTO, null)
        );

        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_shouldAllowMileageToStayTheSame() throws IOException {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(carRepository.save(any(Car.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateCarDTO carDTO = new UpdateCarDTO(
                "Silver",
                150000
        );

        CarResponseDTO result = carService.updateCar(1L, carDTO, null);

        assertEquals("Silver", result.getColour());
        assertEquals(150000, result.getCurrentMileage());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);

        verify(carRepository).save(carCaptor.capture());

        Car updatedCar = carCaptor.getValue();

        assertEquals("Silver", updatedCar.getColour());
        assertEquals(150000, updatedCar.getCurrentMileage());
    }

    @Test
    void deleteCar_shouldDeleteCarSuccessfully() throws IOException {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("Silver");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        carService.deleteCar(1L);

        verify(carRepository).delete(car);
    }

    @Test
    void deleteCar_shouldThrowExceptionWhenCarIsNotFound() throws IOException {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> carService.deleteCar(1L)
        );

        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    void deleteCar_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> carService.deleteCar(1L)
        );

        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    void searchCars_shouldReturnMatchingCars() {

        User user = new User();

        Car car1 = new Car();
        car1.setBrand("Toyota");
        car1.setModel("Corolla");
        car1.setYear(2001);
        car1.setColour("Silver");
        car1.setCurrentMileage(150000);
        car1.setUser(user);

        Car car2 = new Car();
        car2.setBrand("Toyota");
        car2.setModel("Supra");
        car2.setYear(2002);
        car2.setColour("Blue");
        car2.setCurrentMileage(100000);
        car2.setUser(user);

        Car car3 = new Car();
        car3.setBrand("Toyota");
        car3.setModel("Corolla");
        car3.setYear(2003);
        car3.setColour("Silver");
        car3.setCurrentMileage(350000);
        car3.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(car1, car3));

        List<CarResponseDTO> result = carService.searchCars("Corolla");

        assertEquals(2, result.size());

        verify(carRepository).findAll(
                any(Specification.class),
                any(Sort.class));
    }

    @Test
    void searchCars_shouldReturnEmptyListWhenNoCarsMatch() {

        User user = new User();

        Car car1 = new Car();
        car1.setBrand("Toyota");
        car1.setModel("Corolla");
        car1.setYear(2001);
        car1.setColour("Silver");
        car1.setCurrentMileage(150000);
        car1.setUser(user);

        Car car2 = new Car();
        car2.setBrand("Toyota");
        car2.setModel("Supra");
        car2.setYear(2002);
        car2.setColour("Blue");
        car2.setCurrentMileage(100000);
        car2.setUser(user);

        Car car3 = new Car();
        car3.setBrand("Toyota");
        car3.setModel("Corolla");
        car3.setYear(2003);
        car3.setColour("Silver");
        car3.setCurrentMileage(350000);
        car3.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of());

        List<CarResponseDTO> result = carService.searchCars("");

        assertEquals(0, result.size());

        verify(carRepository).findAll(
                any(Specification.class),
                any(Sort.class));
    }

    @Test
    void searchCars_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> carService.searchCars("")
        );

        verify(carRepository, never()).findAll(
                any(Specification.class),
                any(Sort.class)
        );
    }
}