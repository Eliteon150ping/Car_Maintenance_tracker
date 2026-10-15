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
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.entity.User;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import sia.sever.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    void createServiceHistory_shouldCreateServiceRecordSuccessfully() throws IOException {

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
}

// 1) createServiceHistory
// createServiceHistory_shouldCreateServiceRecordSuccessfully
// createServiceHistory_shouldThrowExceptionWhenUserIsNotAuthenticated
// createServiceHistory_shouldThrowExceptionWhenCarDoesNotExist
// createServiceHistory_shouldThrowExceptionWhenServiceMileageExceedsCurrentMileage
// createServiceHistory_shouldThrowExceptionWhenServiceMileageIsLowerThanLatestServiceMileage
// createServiceHistory_shouldThrowExceptionWhenServiceDateIsBeforeCarYear
// createServiceHistory_shouldThrowExceptionWhenServiceDateIsBeforeLatestServiceDate
// createServiceHistory_shouldThrowExceptionWhenDuplicateRecordExists
// createServiceHistory_shouldThrowExceptionWhenOtherServiceHasNoDescription
// createServiceHistory_shouldCalculateNextServiceMileageAndDate

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