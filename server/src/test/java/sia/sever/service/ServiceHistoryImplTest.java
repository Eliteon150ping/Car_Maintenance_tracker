package sia.sever.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.entity.User;
import sia.sever.exception.*;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import sia.sever.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static sia.sever.enums.ServiceCategory.ENGINE;
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
    void createServiceHistory_shouldThrowExceptionWhenOtherServiceHasNoDescription() {

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
    void createServiceHistory_shouldCalculateNextServiceMileageAndDate() {

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
    void updateServiceHistory_shouldUpdateServiceRecordSuccessfully() {

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

        when(serviceHistoryRepository.findByIdAndCar(1L, car))
                .thenReturn(Optional.of(serviceRecord));

        UpdateServiceRecordDTO updateServiceRecord = new UpdateServiceRecordDTO(
                "Replaced Engine mounts(updated)",
                210.0
        );

        when(serviceHistoryRepository.save(any(ServiceHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRecordResponseDTO result = serviceHistoryService.updateServiceHistory(
                1L,
                updateServiceRecord,
                1L
        );

        assertEquals("Replaced Engine mounts(updated)", result.getDescription());
        assertEquals(210.0, result.getCost());

        ArgumentCaptor<ServiceHistory> serviceHistoryArgumentCaptor = ArgumentCaptor.forClass(ServiceHistory.class);

        verify(serviceHistoryRepository).save(serviceHistoryArgumentCaptor.capture());

        ServiceHistory updatedRecord = serviceHistoryArgumentCaptor.getValue();

        assertEquals("Replaced Engine mounts(updated)", updatedRecord.getDescription());
        assertEquals(210.0, updatedRecord.getCost());
    }

    @Test
    void updateServiceHistory_shouldUpdateOnlyProvidedFields() {

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

        when(serviceHistoryRepository.findByIdAndCar(1L, car))
                .thenReturn(Optional.of(serviceRecord));

        UpdateServiceRecordDTO updateServiceRecord = new UpdateServiceRecordDTO(
                "Replaced Engine mounts(updated)",
                210.0
        );

        when(serviceHistoryRepository.save(any(ServiceHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceRecordResponseDTO result = serviceHistoryService.updateServiceHistory(
                1L,
                updateServiceRecord,
                1L
        );

        assertEquals(LocalDate.of(2026, 1, 2), result.getServiceDate());
        assertEquals(150000, result.getMileageAtService());
        assertEquals(OTHER, result.getServiceType());
        assertEquals(210.0, result.getCost());
        assertEquals("Replaced Engine mounts(updated)", result.getDescription());

        ArgumentCaptor<ServiceHistory> serviceHistoryArgumentCaptor = ArgumentCaptor.forClass(ServiceHistory.class);

        verify(serviceHistoryRepository).save(serviceHistoryArgumentCaptor.capture());

        ServiceHistory updatedRecord = serviceHistoryArgumentCaptor.getValue();

        assertEquals(LocalDate.of(2026, 1, 2), updatedRecord.getServiceDate());
        assertEquals(150000, updatedRecord.getMileageAtService());
        assertEquals(OTHER, updatedRecord.getServiceType());
        assertEquals(210.0, updatedRecord.getCost());
        assertEquals("Replaced Engine mounts(updated)", updatedRecord.getDescription());
    }

    @Test
    void updateServiceHistory_shouldThrowExceptionWhenServiceRecordDoesNotExist() {

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

        when(serviceHistoryRepository.findByIdAndCar(1L, car))
                .thenReturn(Optional.empty());

        UpdateServiceRecordDTO updateServiceRecord = new UpdateServiceRecordDTO(
                "Replaced Engine mounts",
                210.0
        );

        assertThrows(ResourceNotFoundException.class, () ->
                serviceHistoryService.updateServiceHistory(1L, updateServiceRecord, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void updateServiceHistory_shouldThrowExceptionWhenOtherServiceHasNoDescription() {

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
        serviceRecord.setDescription("Existing description");
        serviceRecord.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository.findByIdAndCar(1L, car))
                .thenReturn(Optional.of(serviceRecord));

        UpdateServiceRecordDTO updateServiceRecord = new UpdateServiceRecordDTO(
                null,
                210.0
        );

        assertThrows(InvalidClassException.class, () ->
                serviceHistoryService.updateServiceHistory(1L, updateServiceRecord, 1L)
        );

        verify(serviceHistoryRepository, never()).save(any(ServiceHistory.class));
    }

    @Test
    void getUpcomingServiceRecords_shouldReturnUpcomingServices() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(151000);
        serviceRecord.setNextDueDate(LocalDate.now().plusDays(10));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getUpcomingServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());

    }

    @Test
    void getUpcomingServiceRecords_shouldExcludeOtherServices() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(151000);
        serviceRecord.setNextDueDate(LocalDate.now().plusDays(10));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getUpcomingServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getUpcomingServiceRecords_shouldUseLatestRecordForEachCarAndServiceType() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(151000);
        serviceRecord.setNextDueDate(LocalDate.now().plusDays(10));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 2, 2));
        serviceRecord2.setMileageAtService(151000);
        serviceRecord2.setServiceType(AIR_FILTER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced air filter");
        serviceRecord2.setCar(car);
        serviceRecord2.setNextDueMileage(162000);
        serviceRecord2.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getUpcomingServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void getOverdueServiceRecords_shouldReturnOverdueServices() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getOverdueServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getOverdueServiceRecords_shouldExcludeOtherServices() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getOverdueServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getOverdueServiceRecords_shouldUseLatestRecordForEachCarAndServiceType() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(AIR_FILTER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced Air filter");
        serviceRecord2.setCar(car);
        serviceRecord2.setNextDueMileage(165000);
        serviceRecord2.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getOverdueServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
    }

    @Test
    void getServiceHistoryByCar_shouldReturnPaginatedRecords() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(AIR_FILTER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced Air filter");
        serviceRecord2.setCar(car);
        serviceRecord2.setNextDueMileage(165000);
        serviceRecord2.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository
                .findByCarOrderByServiceDateDescMileageAtServiceDesc(car))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getServiceHistoryByCar(
                        List.of(),
                        List.of(),
                        1L,
                        0,
                        1
                );

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getServiceHistoryByCar_shouldFilterByServiceType() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository
                .findByCarOrderByServiceDateDescMileageAtServiceDesc(car))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getServiceHistoryByCar(
                        List.of(AIR_FILTER),
                        List.of(),
                        1L,
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getServiceHistoryByCar_shouldFilterByServiceCategory() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository
                .findByCarOrderByServiceDateDescMileageAtServiceDesc(car))
                .thenReturn(List.of(serviceRecord, serviceRecord2));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getServiceHistoryByCar(
                        List.of(),
                        List.of(ENGINE),
                        1L,
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getServiceHistoryByCar_shouldFilterByServiceTypeAndCategory() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        ServiceHistory serviceRecord3 = new ServiceHistory();
        serviceRecord3.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord3.setMileageAtService(150000);
        serviceRecord3.setServiceType(ENGINE_OIL_AND_FILTER);
        serviceRecord3.setCost(200.0);
        serviceRecord3.setDescription("Replaced oil filter");
        serviceRecord3.setCar(car);
        serviceRecord3.setNextDueMileage(160000);
        serviceRecord3.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(carRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(car));

        when(serviceHistoryRepository
                .findByCarOrderByServiceDateDescMileageAtServiceDesc(car))
                .thenReturn(List.of(serviceRecord, serviceRecord2, serviceRecord3));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getServiceHistoryByCar(
                        List.of(AIR_FILTER),
                        List.of(ENGINE),
                        1L,
                        0,
                        10
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getAllServiceRecords_shouldFilterByServiceTypeAndCategory() {

        User user = new User();

        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2001);
        car.setColour("White");
        car.setCurrentMileage(150000);
        car.setUser(user);

        Car car2 = new Car();
        car2.setBrand("Toyota");
        car2.setModel("Corolla RXI");
        car2.setYear(2001);
        car2.setColour("White");
        car2.setCurrentMileage(180000);
        car2.setUser(user);

        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(LocalDate.of(2026, 1, 2));
        serviceRecord.setMileageAtService(150000);
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        ServiceHistory serviceRecord3 = new ServiceHistory();
        serviceRecord3.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord3.setMileageAtService(150000);
        serviceRecord3.setServiceType(AIR_FILTER);
        serviceRecord3.setCost(200.0);
        serviceRecord3.setDescription("Replaced oil filter");
        serviceRecord3.setCar(car2);
        serviceRecord3.setNextDueMileage(160000);
        serviceRecord3.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findByCarUserAndServiceTypeInOrderByServiceDateDescMileageAtServiceDesc(
                        eq(user),
                        eq(List.of(AIR_FILTER)),
                        any(Pageable.class)
                ))
                .thenReturn(new PageImpl<>(
                        List.of(serviceRecord, serviceRecord3),
                        PageRequest.of(0, 10),
                        2
                ));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getAllServiceRecords(
                        List.of(AIR_FILTER),
                        List.of(ENGINE),
                        0,
                        10
                );

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(AIR_FILTER, result.getContent().get(0).getServiceType());
    }

    @Test
    void getAllServiceRecords_shouldReturnAllRecordsWhenNoFiltersSelected() {

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
        serviceRecord.setServiceType(AIR_FILTER);
        serviceRecord.setCost(200.0);
        serviceRecord.setDescription("Replaced air filter");
        serviceRecord.setCar(car);
        serviceRecord.setNextDueMileage(149999);
        serviceRecord.setNextDueDate(LocalDate.now().minusDays(1));

        ServiceHistory serviceRecord2 = new ServiceHistory();
        serviceRecord2.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord2.setMileageAtService(150000);
        serviceRecord2.setServiceType(OTHER);
        serviceRecord2.setCost(200.0);
        serviceRecord2.setDescription("Replaced CV Joints");
        serviceRecord2.setCar(car);

        ServiceHistory serviceRecord3 = new ServiceHistory();
        serviceRecord3.setServiceDate(LocalDate.of(2027, 1, 2));
        serviceRecord3.setMileageAtService(150000);
        serviceRecord3.setServiceType(AIR_FILTER);
        serviceRecord3.setCost(200.0);
        serviceRecord3.setDescription("Replaced oil filter");
        serviceRecord3.setCar(car);
        serviceRecord3.setNextDueMileage(160000);
        serviceRecord3.setNextDueDate(LocalDate.now().plusDays(60));

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(user);

        when(serviceHistoryRepository
                .findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(
                        eq(user),
                        any(Pageable.class)
                ))
                .thenReturn(new PageImpl<>(
                        List.of(serviceRecord, serviceRecord2, serviceRecord3),
                        PageRequest.of(0, 10),
                        3
                ));

        Page<ServiceRecordResponseDTO> result =
                serviceHistoryService.getAllServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                );

        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
    }

    @Test
    void getLatestServiceInfo_shouldThrowExceptionWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> serviceHistoryService.getLatestServiceInfo(1L)
        );
    }

    @Test
    void getAllServiceRecords_shouldThrowExceptionWhenUserIsNotAuthenticated(){

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> serviceHistoryService.getAllServiceRecords(
                        List.of(),
                        List.of(),
                        0,
                        10
                )
        );
    }

    @Test
    void getServiceHistoryByCar_shouldThrowExceptionWhenUserIsNotAuthenticated(){

        SecurityContextHolder.clearContext();

        assertThrows(
                UnauthorizedException.class,
                () -> serviceHistoryService.getServiceHistoryByCar(
                        List.of(),
                        List.of(),
                        1L,
                        0,
                        10
                )
        );
    }
}