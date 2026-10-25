package sia.sever.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sia.sever.dto.user.AuthResponseDTO;
import sia.sever.dto.user.LoginDTO;
import sia.sever.dto.user.RegisterDTO;
import sia.sever.dto.user.UserResponseDTO;
import sia.sever.entity.User;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ValidationException;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetails;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtility jwtUtility;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser_shouldRegisterUserSuccessfully() {

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(registerDTO.getEmail()))
                .thenReturn(null);

        when(userRepository.findByUserName(registerDTO.getUserName()))
                .thenReturn(null);

        when(passwordEncoder.encode(registerDTO.getPassword()))
                .thenReturn("hashedPassword");

        User savedUser = new User(
                "testUser",
                "test@example.com",
                "hashedPassword"
        );

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDTO result = userService.registerUser(registerDTO);

        assertEquals("testUser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));

    }

    @Test
    void registerUser_shouldThrowExceptionWhenUsernameAlreadyExists() {

        User user = new User();
        user.setUserName("ExistingUser");

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(registerDTO.getEmail()))
                .thenReturn(null);

        when(userRepository.findByUserName(registerDTO.getUserName()))
                .thenReturn(user);

        assertThrows(
                ValidationException.class,
                () -> userService.registerUser(registerDTO)
        );
    }

    @Test
    void registerUser_shouldThrowExceptionWhenEmailAlreadyExists() {

        User user = new User();
        user.setEmail("existingUser@gmail.com");

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(registerDTO.getEmail()))
                .thenReturn(user);

        assertThrows(
                ValidationException.class,
                () -> userService.registerUser(registerDTO)
        );
    }

    @Test
    void registerUser_shouldThrowExceptionWhenUsernameAndEmailAlreadyExist() {

        User user = new User();
        user.setUserName("ExistingUser");
        user.setEmail("existingUser@gmail.com");

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(registerDTO.getEmail()))
                .thenReturn(user);

        when(userRepository.findByUserName(registerDTO.getUserName()))
                .thenReturn(user);

        assertThrows(
                ValidationException.class,
                () -> userService.registerUser(registerDTO)
        );
    }

    @Test
    void loginUser_shouldLoginSuccessfully() {

        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUserName()).thenReturn("testUser");
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getPassword()).thenReturn("password123");

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(loginDTO.getEmail()))
                .thenReturn(user);

        when(passwordEncoder.matches(
                loginDTO.getPassword(),
                user.getPassword()
        )).thenReturn(true);

        when(jwtUtility.generateToken(
                user.getId(),
                user.getEmail()
        )).thenReturn("test-token");

        AuthResponseDTO result = userService.loginUser(loginDTO);

        assertEquals("test-token", result.getToken());
        assertEquals("testUser", result.getUserResponseDTO().getUserName());
        assertEquals("test@example.com", result.getUserResponseDTO().getEmail());

        verify(passwordEncoder).matches("password123", "password123");
        verify(jwtUtility).generateToken(1L, "test@example.com");
    }

    @Test
    void loginUser_shouldThrowExceptionWhenEmailDoesNotExist(){

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password123"
        );

        when(userRepository.findByEmail(loginDTO.getEmail()))
                .thenReturn(null);

        assertThrows(
                ValidationException.class,
                () -> userService.loginUser(loginDTO)
        );
    }

    @Test
    void loginUser_shouldThrowExceptionWhenPasswordIsIncorrect(){

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        LoginDTO loginDTO = new LoginDTO(
                "test@example.com",
                "password"
        );

        when(userRepository.findByEmail(loginDTO.getEmail()))
                .thenReturn(user);

        when(passwordEncoder.matches(
                loginDTO.getPassword(),
                user.getPassword()
        )).thenReturn(false);

        assertThrows(
                ValidationException.class,
                () -> userService.loginUser(loginDTO)
        );
    }
}

//USER SERVICE TESTS

//        ├── editProfile
//│   ├── shouldUpdateUsernameSuccessfully
//│   ├── shouldUpdatePasswordSuccessfully
//│   ├── shouldUpdateUsernameAndPasswordSuccessfully
//│   ├── shouldThrowExceptionWhenUsernameAlreadyExists
//│   ├── shouldAllowCurrentUsername
//│   └── shouldAllowEmptyFields
//│
//        ├── validateUserName
//│   ├── shouldPassWhenUsernameIsAvailable
//│   ├── shouldThrowExceptionWhenUsernameAlreadyExists
//│   ├── shouldAllowCurrentUsername
//│   └── shouldIgnoreBlankUsername
//│
//        ├── deleteProfile
//│   └── shouldDeleteCurrentUser
//│
//        ├── getCurrentUserLogged
//│   ├── shouldReturnCurrentUser
//│   ├── shouldThrowExceptionWhenUserDoesNotExist
//│   ├── shouldThrowExceptionWhenUserIsNotAuthenticated
//│   └── shouldThrowExceptionWhenPrincipalIsInvalid
//│
//        └── mappers
//    ├── mapToUserResponseDTO_shouldMapUserCorrectly
//    └── mapToEntity_shouldMapRegisterDTOCorrectly
