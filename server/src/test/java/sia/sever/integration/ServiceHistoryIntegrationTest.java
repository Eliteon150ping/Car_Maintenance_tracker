package sia.sever.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.dto.car.CarSummaryDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import org.junit.jupiter.api.Test;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;

import java.time.LocalDate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sia.sever.enums.ServiceType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ServiceHistoryIntegrationTest.FixedClockConfig.class)
public class ServiceHistoryIntegrationTest {

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        public Clock fixedClock() {

            ZoneId zone = ZoneId.of("Africa/Johannesburg");

            return Clock.fixed(
                    LocalDate.of(2026, 11, 10)
                            .atStartOfDay(zone)
                            .toInstant(),
                    zone
            );
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceHistoryRepository serviceHistoryRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtility jwtUtility;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @BeforeEach
    void cleanDatabase() {
        serviceHistoryRepository.deleteAll();
        carRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createTestUser() {

        User user = new User(
                "testUser",
                "test@example.com",
                "password123"
        );

        return userRepository.save(user);
    }

    private Car createTestCar(User user) {

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        return carRepository.save(car);
    }

    private String generateToken(User user) {

        return jwtUtility.generateToken(
                user.getId(),
                user.getEmail()
        );
    }

    @Test
    void createServiceRecord_shouldReturnCreatedResponse() throws Exception {

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.mileageAtService").value(150000))
                .andExpect(jsonPath("$.serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.cost").value(1200.0))
                .andExpect(jsonPath("$.description").value("Changed oil and filter"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void rejectDuplicateServiceRecord_shouldReturnConflict() throws Exception {

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        // First request - create service record
        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        // Second request - attempt to create duplicate
        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isConflict());

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void updateServiceRecord_shouldReturnUpdatedResponse() throws Exception {

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        String responseBody = mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ServiceRecordResponseDTO createdRecord = objectMapper.readValue(
                responseBody,
                ServiceRecordResponseDTO.class
        );

        UpdateServiceRecordDTO updateServiceRecordDTO = new UpdateServiceRecordDTO(
                "Changed oil and filter(updated)",
                1200.1
        );

        mockMvc.perform(put("/api/service-records/car/" + car.getId() + "/service/" + createdRecord.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateServiceRecordDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.mileageAtService").value(150000))
                .andExpect(jsonPath("$.serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.cost").value(1200.1))
                .andExpect(jsonPath("$.description").value("Changed oil and filter(updated)"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getAllServiceRecords_shouldReturnAllRecords() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

       mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(1200.0))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getServiceHistoryByCar_shouldReturnCarRecords() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/car/" + car.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(1200.0))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getLatestServiceInfo_shouldReturnLatestInformation() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/car/" + car.getId() + "/latest-info")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestServiceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.latestServiceMileage").value(150000));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getUpcomingServiceRecords_shouldReturnUpcomingRecords() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 10, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        car.setCurrentMileage(159000);
        car = carRepository.save(car);

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/upcoming")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceDate").value("2026-10-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2027-04-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(1200.0))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(1000))
                .andExpect(jsonPath("$[0].remainingDays").value(143))
                .andExpect(jsonPath("$[0].car.id").value(car.getId()))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(159000))
                .andExpect(jsonPath("$[0].latestRecord").value(true));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getOverdueServiceRecords_shouldReturnOverdueRecords() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 10, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        car.setCurrentMileage(161000);
        car = carRepository.save(car);

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/overdue")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceDate").value("2026-10-02"))
                .andExpect(jsonPath("$[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$[0].nextDueDate").value("2027-04-02"))
                .andExpect(jsonPath("$[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$[0].cost").value(1200.0))
                .andExpect(jsonPath("$[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$[0].remainingKm").value(-1000))
                .andExpect(jsonPath("$[0].remainingDays").value(143))
                .andExpect(jsonPath("$[0].car.id").value(car.getId()))
                .andExpect(jsonPath("$[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].car.year").value(2001))
                .andExpect(jsonPath("$[0].car.currentMileage").value(161000))
                .andExpect(jsonPath("$[0].latestRecord").value(true));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getAllServiceRecordsPaginated_shouldReturnPage() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/page")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.content[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$.content[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.content[0].cost").value(1200.0))
                .andExpect(jsonPath("$.content[0].description").value("Changed oil and filter"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getServiceHistoryByCarPaginated_shouldReturnPage() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 1, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/car/" + car.getId() + "/page")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].serviceDate").value("2026-01-02"))
                .andExpect(jsonPath("$.content[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$.content[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.content[0].cost").value(1200.0))
                .andExpect(jsonPath("$.content[0].description").value("Changed oil and filter"));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getUpcomingServiceRecordsPaginated_shouldReturnPage() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 10, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        car.setCurrentMileage(159000);
        car = carRepository.save(car);

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/upcoming/page")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].serviceDate").value("2026-10-02"))
                .andExpect(jsonPath("$.content[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$.content[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$.content[0].nextDueDate").value("2027-04-02"))
                .andExpect(jsonPath("$.content[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.content[0].cost").value(1200.0))
                .andExpect(jsonPath("$.content[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$.content[0].remainingKm").value(1000))
                .andExpect(jsonPath("$.content[0].remainingDays").value(143))
                .andExpect(jsonPath("$.content[0].car.id").value(car.getId()))
                .andExpect(jsonPath("$.content[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$.content[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.content[0].car.year").value(2001))
                .andExpect(jsonPath("$.content[0].car.currentMileage").value(159000))
                .andExpect(jsonPath("$.content[0].latestRecord").value(true));

        assertEquals(1, serviceHistoryRepository.count());
    }

    @Test
    void getOverdueServiceRecordsPaginated_shouldReturnPage() throws Exception{

        User user = createTestUser();
        Car car = createTestCar(user);
        String token = generateToken(user);

        CreateServiceRecordDTO serviceRecordDTO = new CreateServiceRecordDTO(
                LocalDate.of(2026, 10, 2),
                150000,
                ENGINE_OIL_AND_FILTER,
                1200.0,
                "Changed oil and filter"
        );

        car.setCurrentMileage(161000);
        car = carRepository.save(car);

        mockMvc.perform(post("/api/service-records/car/" + car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(serviceRecordDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/service-records/overdue/page")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].serviceDate").value("2026-10-02"))
                .andExpect(jsonPath("$.content[0].mileageAtService").value(150000))
                .andExpect(jsonPath("$.content[0].nextDueMileage").value(160000))
                .andExpect(jsonPath("$.content[0].nextDueDate").value("2027-04-02"))
                .andExpect(jsonPath("$.content[0].serviceType").value("ENGINE_OIL_AND_FILTER"))
                .andExpect(jsonPath("$.content[0].cost").value(1200.0))
                .andExpect(jsonPath("$.content[0].description").value("Changed oil and filter"))
                .andExpect(jsonPath("$.content[0].remainingKm").value(-1000))
                .andExpect(jsonPath("$.content[0].remainingDays").value(143))
                .andExpect(jsonPath("$.content[0].car.id").value(car.getId()))
                .andExpect(jsonPath("$.content[0].car.brand").value("Toyota"))
                .andExpect(jsonPath("$.content[0].car.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.content[0].car.year").value(2001))
                .andExpect(jsonPath("$.content[0].car.currentMileage").value(161000))
                .andExpect(jsonPath("$.content[0].latestRecord").value(true));

        assertEquals(1, serviceHistoryRepository.count());
    }
}