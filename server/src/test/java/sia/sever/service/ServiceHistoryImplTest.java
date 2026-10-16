package sia.sever.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.entity.User;
import sia.sever.exception.*;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import sia.sever.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static sia.sever.enums.ServiceType.*;

@ExtendWith(MockitoExtension.class)
public class ServiceHistoryImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceHistoryRepository serviceHistoryRepository;

    @InjectMocks
    private ServiceHistoryServiceImpl serviceHistoryService;

    @BeforeEach
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
    void createServiceHistory_shouldCreateServiceRecordSuccessfully() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        ServiceHistory[] savedRecord = new ServiceHistory[1];

        when(serviceHistoryRepository.save(any(ServiceHistory.class)))
                .thenAnswer(invocation -> {
                    savedRecord[0] = invocation.getArgument(0);
                    return savedRecord[0];
                });

        when(serviceHistoryRepository
                .findFirstByCarAndServiceTypeOrderByServiceDateDescMileageAtServiceDesc(
                        any(Car.class),
                        eq(OTHER)))
                .thenAnswer(invocation -> savedRecord[0]);

        ServiceRecordResponseDTO result = serviceHistoryService.createServiceHistory(
                serviceRecordDTO,
                1L
        );

        assertEquals(LocalDate.of(2026, 1, 2), result.getServiceDate());
        assertEquals(150000, result.getMileageAtService());
        assertEquals(OTHER, result.getServiceType());
        assertEquals(1200.0, result.getCost());
        assertEquals("Replaced CV Joints", result.getDescription());

        ArgumentCaptor<ServiceHistory> serviceHistoryCaptor = ArgumentCaptor.forClass(ServiceHistory.class);

        verify(serviceHistoryRepository).save(serviceHistoryCaptor.capture());

        ServiceHistory savedServiceRecord = serviceHistoryCaptor.getValue();

        assertEquals(car, savedServiceRecord.getCar());
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                UnauthorizedException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenCarDoesNotExist() {

        User user = new User();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenServiceMileageExceedsCurrentMileage() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                160000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                InvalidMileageException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenServiceMileageIsLowerThanLatestServiceMileage() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord.setMileageAtService(150000);
        serviceRecord.setServiceType(OTHER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced Engine mounts");
        serviceRecord.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository.findFirstByCarOrderByMileageAtServiceDesc(car))
                .thenReturn(serviceRecord);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 3),
                140000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                InvalidMileageException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenServiceDateIsBeforeCarYear() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(1999, 1, 3),
                150000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                InvalidDateException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenServiceDateIsBeforeLatestServiceDate() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(160000);
        car.setUser(user);

        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord.setMileageAtService(150000);
        serviceRecord.setServiceType(OTHER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced Engine mounts");
        serviceRecord.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository.findFirstByCarOrderByServiceDateDesc(car))
                .thenReturn(serviceRecord);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2025, 1, 3),
                155000,
                OTHER,
                1200.0,
                "Replaced CV Joints"
        );

        assertThrows(
                InvalidDateException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenDuplicateRecordExists() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(160000);
        car.setUser(user);

        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord.setMileageAtService(150000);
        serviceRecord.setServiceType(OTHER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced Engine mounts");
        serviceRecord.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository.existsByCarAndServiceTypeAndServiceDateAndMileageAtService(
                car,
                serviceRecord.getServiceType(),
                serviceRecord.getServiceDate(),
                serviceRecord.getMileageAtService()

        )).thenReturn(true);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                OTHER,
                200.0,
                "Replaced Engine mounts"
        );

        assertThrows(
                InvalidClassException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldThrowExceptionWhenOtherServiceHasNoDescription(){

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                OTHER,
                1200.0,
                null
        );

        assertThrows(
                InvalidClassException.class,
                () -> serviceHistoryService.createServiceHistory(serviceRecordDTO, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void createServiceHistory_shouldCalculateNextServiceMileageAndDate(){

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                AIR_FILTER,
                1200.0,
                "Replaced air filter"
        );

        ServiceHistory[] savedRecord = new ServiceHistory[1];

        when(serviceHistoryRepository.save(any(ServiceHistory.class)))
                .thenAnswer(invocation -> {
                    savedRecord[0] = invocation.getArgument(0);
                    return savedRecord[0];
                });

        when(serviceHistoryRepository
                .findFirstByCarAndServiceTypeOrderByServiceDateDescMileageAtServiceDesc(
                        any(Car.class),
                        eq(AIR_FILTER)))
                .thenAnswer(invocation -> savedRecord[0]);

        ServiceRecordResponseDTO result = serviceHistoryService.createServiceHistory(
                serviceRecordDTO,
                1L
        );

        assertEquals(LocalDate.of(2027, 1, 2), result.getNextDueDate());
        assertEquals(165000, result.getNextDueMileage());
    }

    @Test
    void updateServiceHistory_shouldUpdateServiceRecordSuccessfully(){

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord.setMileageAtService(150000);
        serviceRecord.setServiceType(OTHER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced Engine mounts");
        serviceRecord.setCar(car);

    }
}

// 2) updateServiceHistory
// updateServiceHistory_shouldUpdateServiceRecordSuccessfully
// updateServiceHistory_shouldUpdateOnlyProvidedFields
// updateServiceHistory_shouldThrowExceptionWhenServiceRecordDoesNotExist
// updateServiceHistory_shouldThrowExceptionWhenOtherServiceHasNoDescription

// 3) Upcoming / overdue services
// getUpcomingServiceRecords_shouldReturnUpcomingServices
// getUpcomingServiceRecords_shouldExcludeOtherServices
// getUpcomingServiceRecords_shouldUseLatestRecordForEachCarAndServiceType
// getOverdueServiceRecords_shouldReturnOverdueServices
// getOverdueServiceRecords_shouldExcludeOtherServices
// getOverdueServiceRecords_shouldUseLatestRecordForEachCarAndServiceType

// 4) Per-car history
// getServiceHistoryByCar_shouldReturnPaginatedRecords
// getServiceHistoryByCar_shouldFilterByServiceType
// getServiceHistoryByCar_shouldFilterByServiceCategory
// getServiceHistoryByCar_shouldFilterByServiceTypeAndCategory

// 5) All records
// getAllServiceRecords_shouldFilterByServiceTypeAndCategory
// getAllServiceRecords_shouldReturnAllRecordsWhenNoFiltersSelected

// 6) authorization
// getLatestServiceInfo_shouldThrowExceptionWhenUserIsNotAuthenticated
// getAllServiceRecords_shouldThrowExceptionWhenUserIsNotAuthenticated
// getServiceHistoryByCar_shouldThrowExceptionWhenUserIsNotAuthenticated