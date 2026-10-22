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
import sia.sever.dto.user.RegisterDTO;
import sia.sever.dto.user.UserResponseDTO;
import sia.sever.entity.User;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void teardown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser_shouldRegisterUserSuccessfully(){

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
}

//USER SERVICE TESTS
//│
//        ├── registerUser
//│   ├── shouldRegisterUserSuccessfully
//│   ├── shouldThrowExceptionWhenUsernameAlreadyExists
//│   ├── shouldThrowExceptionWhenEmailAlreadyExists
//│   └── shouldThrowExceptionWhenUsernameAndEmailAlreadyExist
//│
//        ├── loginUser
//│   ├── shouldLoginSuccessfully
//│   ├── shouldThrowExceptionWhenEmailDoesNotExist
//│   └── shouldThrowExceptionWhenPasswordIsIncorrect
//│
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
