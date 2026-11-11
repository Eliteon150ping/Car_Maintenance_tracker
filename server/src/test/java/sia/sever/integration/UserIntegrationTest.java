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
import org.springframework.test.web.servlet.MvcResult;
import sia.sever.dto.user.LoginDTO;
import sia.sever.dto.user.RegisterDTO;
import sia.sever.dto.user.UpdateUserDTO;
import sia.sever.dto.user.UsernameValidationDTO;
import sia.sever.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldReturnCreatedResponse() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        assertEquals(1, userRepository.count());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenUsernameIsInvalid() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "Te",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userName")
                        .value("Username must be at least 3 characters long"));

        assertEquals(0, userRepository.count());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "testExampleCom",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email")
                        .value("Email is invalid format"));

        assertEquals(0, userRepository.count());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "p"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password")
                        .value("Password must be at least 8 characters long"));

        assertEquals(0, userRepository.count());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        RegisterDTO registerDTO2 = new RegisterDTO(
                "NewUser",
                "test@example.com",
                "password123456"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());

        assertEquals(1, userRepository.count());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenUsernameAlreadyExists() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        RegisterDTO registerDTO2 = new RegisterDTO(
                "TestUser",
                "test2@example.com",
                "password123456"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.userName").exists());

        assertEquals(1, userRepository.count());
    }

    @Test
    void loginUser_shouldReturnAuthResponse() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        MvcResult registrationResult = mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        Long userId = objectMapper.readTree(registrationResult.getResponse().getContentAsString())
                .get("id")
                .asLong();

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userResponseDTO.id").value(userId))
                .andExpect(jsonPath("$.userResponseDTO.userName").value("TestUser"))
                .andExpect(jsonPath("$.userResponseDTO.email").value("test@example.com"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenEmailIsInvalid() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email is required"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenPasswordIsBlank() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                ""
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").value("Password is required"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenEmailDoesNotExist() throws Exception{

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Login failed"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Email not found, please register instead"));

        assertEquals(0, userRepository.count());
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenPasswordIsIncorrect() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password1234"
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Login failed"))
                .andExpect(jsonPath("$.errors.password").value("Password is incorrect"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void getCurrentUser_shouldReturnAuthenticatedUser() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        mockMvc.perform(get("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getCurrentUser_shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editProfile_shouldReturnUpdatedProfile() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUserName",
                "password1234"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("NewUserName"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        assertEquals("NewUserName",
                userRepository.findByEmail("test@example.com")
                        .getUserName());
    }

    @Test
    void editProfile_shouldReturnBadRequestWhenUsernameIsInvalid() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "Ne",
                "password123"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.userName")
                        .value("Username must be at least 3 characters long"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void editProfile_shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "TestUser",
                "p"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password")
                        .value("Password must be at least 8 characters long"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void editProfile_shouldReturnBadRequestWhenUsernameAlreadyExists() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        RegisterDTO registerDTO2 = new RegisterDTO(
                "TestUser2",
                "test2@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser2"))
                .andExpect(jsonPath("$.email").value("test2@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test2@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "TestUser",
                "password123"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Update failed"))
                .andExpect(jsonPath("$.errors.userName")
                        .value("Username already exists. Please type a different one"));

        assertEquals(2, userRepository.count());

        assertEquals("TestUser2",
                userRepository.findByEmail("test2@example.com")
                        .getUserName());
    }

    @Test
    void editProfile_shouldAllowKeepingCurrentUsername() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "TestUser",
                "password123"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        assertEquals(1, userRepository.count());

        assertEquals("TestUser",
                userRepository.findByEmail("test@example.com")
                        .getUserName());
    }

    @Test
    void validateUserName_shouldReturnNoContentWhenUsernameIsAvailable() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UsernameValidationDTO usernameValidationDTO =
                new UsernameValidationDTO("AvailableUser");

        mockMvc.perform(put("/api/users/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(usernameValidationDTO)))
                .andExpect(status().isNoContent());

        assertEquals(1, userRepository.count());

        assertEquals("TestUser",
                userRepository.findByEmail("test@example.com").getUserName());
    }

    @Test
    void validateUserName_shouldReturnBadRequestWhenUsernameIsTaken() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        RegisterDTO registerDTO2 = new RegisterDTO(
                "TestUser2",
                "test2@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser2"))
                .andExpect(jsonPath("$.email").value("test2@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test2@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UsernameValidationDTO usernameValidationDTO =
                new UsernameValidationDTO("TestUser");

        mockMvc.perform(put("/api/users/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(usernameValidationDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userName")
                        .value("Username already exists. Please type a different one"));

        assertEquals(2, userRepository.count());

        assertEquals("TestUser2",
                userRepository.findByEmail("test2@example.com").getUserName());
    }

    @Test
    void validateUserName_shouldAllowCurrentUsername() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        UsernameValidationDTO usernameValidationDTO =
                new UsernameValidationDTO("TestUser");

        mockMvc.perform(put("/api/users/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(usernameValidationDTO)))
                .andExpect(status().isNoContent());

        assertEquals(1, userRepository.count());

        assertEquals("TestUser",
                userRepository.findByEmail("test@example.com").getUserName());
    }

    @Test
    void deleteProfile_shouldReturnNoContent() throws Exception{

        RegisterDTO registerDTO = new RegisterDTO(
                "TestUser",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.userName").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(
                loginResult.getResponse().getContentAsString()
        ).get("token").asText();

        assertEquals(1, userRepository.count());

        mockMvc.perform(delete("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertEquals(0, userRepository.count());
    }

    @Test
    void deleteProfile_shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception{

        mockMvc.perform(delete("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }
}
