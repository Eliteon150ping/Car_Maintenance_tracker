package sia.sever.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sia.sever.dto.user.*;
import sia.sever.exception.ErrorResponse;
import sia.sever.exception.InvalidClassException;
import sia.sever.exception.ValidationException;
import sia.sever.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void registerUser_shouldReturnCreated() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        UserResponseDTO responseDTO = new UserResponseDTO(
                1L,
                "testUser",
                "test@example.com"
        );

        when(userService.registerUser(any(RegisterDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("email", "This email already exists");

        when(userService.registerUser(any(RegisterDTO.class)))
                .thenThrow(new ValidationException("Validation failed", errors));

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("This email already exists"));
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        RegisterDTO registerDTO = new RegisterDTO(
                "te",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.userName").exists());

        verify(userService, never()).registerUser(any(RegisterDTO.class));
    }

    @Test
    void loginUser_shouldReturnOk() throws Exception {

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        AuthResponseDTO authResponseDTO = new AuthResponseDTO(
                "abc123",
                new UserResponseDTO(
                        1L,
                        "testUser",
                        "test@example.com"
                )
        );

        when(userService.loginUser(any(LoginDTO.class)))
                .thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc123"))
                .andExpect(jsonPath("$.userResponseDTO.id").value(1))
                .andExpect(jsonPath("$.userResponseDTO.userName").value("testUser"))
                .andExpect(jsonPath("$.userResponseDTO.email").value("test@example.com"));
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password12"
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("password", "Password is incorrect");

        when(userService.loginUser(any(LoginDTO.class)))
                .thenThrow(new ValidationException("Validation failed", errors));

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").value("Password is incorrect"));
    }

    @Test
    void loginUser_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        LoginDTO loginDTO = new LoginDTO(
                "testexamplecom",
                "password123"
        );

        mockMvc.perform(post("/api/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email is invalid format"));

        verify(userService, never()).loginUser(any(LoginDTO.class));
    }

    @Test
    void editProfile_shouldReturnOk() throws Exception {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                "password123"
        );

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "NewUsername",
                "test@example.com"
        );

        when(userService.editProfile(any(UpdateUserDTO.class)))
                .thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("NewUsername"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void editProfile_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                "password123"
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("userName", "Username already exists");

        when(userService.editProfile(any(UpdateUserDTO.class)))
                .thenThrow(new ValidationException("Validation failed", errors));

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.userName").value("Username already exists"));
    }

    @Test
    void editProfile_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                "a"
        );

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").value("Password must be at least 8 characters long"));

        verify(userService, never()).editProfile(any(UpdateUserDTO.class));
    }

    @Test
    void validateUserName_shouldReturnNoContent() throws Exception {

        UsernameValidationDTO usernameDTO = new UsernameValidationDTO(
                "NewUsername"
        );

        mockMvc.perform(put("/api/users/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usernameDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void validateUserName_shouldReturnBadRequestWhenServiceThrowsValidationException() throws Exception {

        UsernameValidationDTO usernameDTO = new UsernameValidationDTO(
                "NewUsername"
        );

        Map<String, String> errors = new HashMap<>();
        errors.put("userName", "Username already exists");

        doThrow(new ValidationException("Validation failed", errors))
                .when(userService).validateUserName(any(String.class));

        mockMvc.perform(put("/api/users/profile/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usernameDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.userName").value("Username already exists"));
    }

    @Test
    void deleteProfile_shouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/api/users/profile"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProfile_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {

        doThrow(new RuntimeException("Something went wrong"))
                .when(userService).deleteProfile();

        mockMvc.perform(delete("/api/users/profile"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCurrentUser_shouldReturnOk() throws Exception{

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "testUser",
                "test@example.com"
        );

        when(userService.getCurrentUserLogged())
                .thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getCurrentUser_shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception{

        doThrow(new RuntimeException("Something went wrong"))
                .when(userService).getCurrentUserLogged();

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isInternalServerError());
    }
}

//Get current user — 2 tests
//- [ ] getCurrentUser_shouldReturnInternalServerErrorWhenServiceThrowsException