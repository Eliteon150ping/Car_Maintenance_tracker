package sia.sever.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import org.junit.jupiter.api.Test;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sia.sever.enums.ServiceType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ServiceHistoryIntegrationTest {

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
}

//#	Integration test
//1	Create a service record successfully
//2	Reject a duplicate service record
//3	Update an existing service record
//4	Retrieve all service records
//5	Retrieve service records for a specific car
//6	Retrieve the latest service information
//7	Retrieve upcoming service records
//8	Retrieve overdue service records
//9	Retrieve all service records with pagination and filters
//10	Retrieve a car's service records with pagination and filters
//        11	Retrieve upcoming records with pagination
//12	Retrieve overdue records with pagination