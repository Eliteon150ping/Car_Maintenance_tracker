package sia.sever.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.dto.car.MileageValidationDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.repository.CarRepository;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    private String generateToken(User user) {

        return jwtUtility.generateToken(
                user.getId(),
                user.getEmail()
        );
    }

    @Test
    void createCar_shouldReturnCreatedResponse() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(multipart("/api/my-cars")
                        .header("Authorization", "Bearer " + token)
                        .param("brand", "Toyota")
                        .param("model", "Corolla RXI")
                        .param("year", "2001")
                        .param("colour", "White")
                        .param("currentMileage", "150000"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(150000))
                .andExpect(jsonPath("$.carImageUrl").value(nullValue()));

        Car savedCar = carRepository.findAll().get(0);

        assertEquals(1, carRepository.count());
        assertEquals("Toyota", savedCar.getBrand());
        assertEquals(user.getId(), savedCar.getUser().getId());
        assertNull(savedCar.getCarImageUrl());
    }

    @Test
    void createCar_shouldReturnBadRequestWhenFieldsAreInvalid() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(multipart("/api/my-cars")
                        .header("Authorization", "Bearer " + token)
                        .param("brand", "")
                        .param("model", "Corolla RXI")
                        .param("year", "2001")
                        .param("colour", "White")
                        .param("currentMileage", "150000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.brand")
                        .value("Brand is required"));

        assertEquals(0, carRepository.count());
    }

    @Test
    void createCar_shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(multipart("/api/my-cars"))
                .andExpect(status().isUnauthorized());

        assertEquals(0, carRepository.count());
    }

    @Test
    void getAllCars_shouldReturnCarsBelongingToAuthenticatedUser() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(get("/api/my-cars")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedCar.getId()))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].colour").value("White"))
                .andExpect(jsonPath("$[0].currentMileage").value(150000))
                .andExpect(jsonPath("$[0].carImageUrl").value(nullValue()));

        assertEquals(1, carRepository.count());
    }

    @Test
    void getAllCars_shouldReturnEmptyListWhenUserHasNoCars() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(get("/api/my-cars")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        assertEquals(0, carRepository.count());
    }

    @Test
    void getAllCars_shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/my-cars"))
                .andExpect(status().isUnauthorized());

        assertEquals(0, carRepository.count());
    }

    @Test
    void searchCars_shouldReturnMatchingCars() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(get("/api/my-cars/search")
                        .header("Authorization", "Bearer " + token)
                        .param("search", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(savedCar.getId()))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].colour").value("White"))
                .andExpect(jsonPath("$[0].currentMileage").value(150000))
                .andExpect(jsonPath("$[0].carImageUrl").value(nullValue()));

        assertEquals(1, carRepository.count());
    }

    @Test
    void searchCars_shouldReturnEmptyListWhenNoCarsMatch() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(get("/api/my-cars/search")
                        .header("Authorization", "Bearer " + token)
                        .param("search", "Nissan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        assertEquals(0, carRepository.count());
    }

    @Test
    void searchCars_shouldOnlyReturnCarsBelongingToAuthenticatedUser() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "password123"
        );
        user2 = userRepository.save(user2);
        String token2 = generateToken(user2);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user2
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(get("/api/my-cars/search")
                        .header("Authorization", "Bearer " + token)
                        .param("search", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/my-cars/search")
                        .header("Authorization", "Bearer " + token2)
                        .param("search", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(savedCar.getId()))
                .andExpect(jsonPath("$[0].brand").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Corolla RXI"))
                .andExpect(jsonPath("$[0].year").value(2001))
                .andExpect(jsonPath("$[0].colour").value("White"))
                .andExpect(jsonPath("$[0].currentMileage").value(150000))
                .andExpect(jsonPath("$[0].carImageUrl").value(nullValue()));

        assertEquals(1, carRepository.count());
    }

    @Test
    void getCarById_shouldReturnCarWhenItExists() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(get("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCar.getId()))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(150000))
                .andExpect(jsonPath("$.carImageUrl").value(nullValue()));

        assertEquals(1, carRepository.count());
    }

    @Test
    void getCarById_shouldReturnNotFoundWhenCarDoesNotExist() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(get("/api/my-cars/{id}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        assertEquals(0, carRepository.count());
    }

    @Test
    void getCarById_shouldReturnNotFoundWhenCarBelongsToAnotherUser() throws Exception {

        User user = createTestUser();

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "password123"
        );
        user2 = userRepository.save(user2);
        String token2 = generateToken(user2);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(get("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isNotFound());

        assertEquals(1, carRepository.count());
    }

    @Test
    void updateCar_shouldUpdateCarSuccessfully() throws Exception {

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(multipart("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("brand", "Toyota")
                        .param("model", "Corolla RXI")
                        .param("year", "2001")
                        .param("colour", "White")
                        .param("currentMileage", "160000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla RXI"))
                .andExpect(jsonPath("$.year").value(2001))
                .andExpect(jsonPath("$.colour").value("White"))
                .andExpect(jsonPath("$.currentMileage").value(160000))
                .andExpect(jsonPath("$.carImageUrl").value(nullValue()));

        Car updatedCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals("Toyota", updatedCar.getBrand());
        assertEquals("Corolla RXI", updatedCar.getModel());
        assertEquals(160000, updatedCar.getCurrentMileage());
        assertEquals(user.getId(), updatedCar.getUser().getId());
        assertNull(updatedCar.getCarImageUrl());
    }

    @Test
    void updateCar_shouldReturnBadRequestWhenMileageIsLower() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(multipart("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token)
                        .param("colour", "White")
                        .param("currentMileage", "140000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Updated failed"))
                .andExpect(jsonPath("$.errors.currentMileage")
                        .value("Updated mileage cannot be less than current mileage"));

        Car unchangedCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals(150000, unchangedCar.getCurrentMileage());
        assertEquals(1, carRepository.count());
    }

    @Test
    void updateCar_shouldReturnNotFoundWhenCarDoesNotExist() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        mockMvc.perform(multipart("/api/my-cars/{id}", 999999L)
                        .header("Authorization", "Bearer " + token)
                        .param("colour", "White")
                        .param("currentMileage", "140000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());

        assertEquals(0, carRepository.count());
    }

    @Test
    void updateCar_shouldReturnNotFoundWhenCarBelongsToAnotherUser() throws Exception{

        User user = createTestUser();

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "password123"
        );

        user2 = userRepository.save(user2);
        String token2 = generateToken(user2);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(multipart("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token2)
                        .param("colour", "White")
                        .param("currentMileage", "140000")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());

        Car unchangedCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals(150000, unchangedCar.getCurrentMileage());
        assertEquals(user.getId(), unchangedCar.getUser().getId());
        assertEquals(1, carRepository.count());
    }

    @Test
    void validateMileage_shouldReturnNoContentWhenMileageIsValid() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                160000
        );

        mockMvc.perform(put("/api/my-cars/" + savedCar.getId() + "/mileage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isNoContent());

        Car unchangedCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals(150000, unchangedCar.getCurrentMileage());
    }

    @Test
    void validateMileage_shouldReturnBadRequestWhenMileageIsLower() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                140000
        );

        mockMvc.perform(put("/api/my-cars/" + savedCar.getId() + "/mileage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Updated failed"))
                .andExpect(jsonPath("$.errors.currentMileage")
                        .value("Updated mileage cannot be less than current mileage"));
    }

    @Test
    void validateMileage_shouldReturnNotFoundWhenCarDoesNotExist() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                140000
        );

        mockMvc.perform(put("/api/my-cars/{id}/mileage", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void validateMileage_shouldReturnNotFoundWhenCarBelongsToAnotherUser() throws Exception{

        User user = createTestUser();

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "password123"
        );

        user2 = userRepository.save(user2);
        String token2 = generateToken(user2);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        MileageValidationDTO mileageValidationDTO = new MileageValidationDTO(
                160000
        );

        mockMvc.perform(put("/api/my-cars/" + savedCar.getId() +"/mileage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token2)
                        .content(objectMapper.writeValueAsString(mileageValidationDTO)))
                .andExpect(status().isNotFound());

        Car unchangedCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals(150000, unchangedCar.getCurrentMileage());
        assertEquals(user.getId(), unchangedCar.getUser().getId());
    }

    @Test
    void deleteCar_shouldDeleteCarSuccessfully() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(delete("/api/my-cars/" + savedCar.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertEquals(0, carRepository.count());
    }

    @Test
    void deleteCar_shouldReturnNotFoundWhenCarDoesNotExist() throws Exception{

        User user = createTestUser();
        String token = generateToken(user);


        mockMvc.perform(delete("/api/my-cars/{id}", 999999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        assertEquals(0, carRepository.count());
    }

    @Test
    void deleteCar_shouldReturnNotFoundWhenCarBelongsToAnotherUser() throws Exception{

        User user = createTestUser();

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "password123"
        );

        user2 = userRepository.save(user2);
        String token2 = generateToken(user2);

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(delete("/api/my-cars/" + savedCar.getId())
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isNotFound());

        assertEquals(1, carRepository.count());

        Car existingCar = carRepository.findById(savedCar.getId()).orElseThrow();

        assertEquals(user.getId(), existingCar.getUser().getId());
    }

    @Test
    void deleteCar_shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception{

        User user = createTestUser();

        Car car = new Car(
                "Toyota",
                "Corolla RXI",
                2001,
                "White",
                150000,
                null,
                user
        );

        Car savedCar = carRepository.save(car);

        mockMvc.perform(delete("/api/my-cars/" + savedCar.getId()))
                .andExpect(status().isUnauthorized());

        assertEquals(1, carRepository.count());

        Car existingCar = carRepository.findById(savedCar.getId()).orElseThrow();
        assertEquals(user.getId(), existingCar.getUser().getId());
    }
}