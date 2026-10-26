package sia.sever.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sia.sever.dto.user.*;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import sia.sever.exception.InvalidClassException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.ValidationException;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetails;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void loginUser_shouldThrowExceptionWhenEmailDoesNotExist() {

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
    void loginUser_shouldThrowExceptionWhenPasswordIsIncorrect() {

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

    @Test
    void editProfile_shouldUpdateUsernameSuccessfully() throws Exception {

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                null
        );

        when(userRepository.findByUserName("NewUsername"))
                .thenReturn(null);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.editProfile(updateUserDTO);

        assertEquals("NewUsername", result.getUserName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();

        assertEquals("NewUsername", updatedUser.getUserName());
    }

    @Test
    void editProfile_shouldUpdatePasswordSuccessfully() throws Exception {

        User user = new User(
                "testUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                null,
                "hashedPasswordUpdated"
        );

        when(passwordEncoder.encode(updateUserDTO.getPassword()))
                .thenReturn("newHashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.editProfile(updateUserDTO);

        assertEquals("testUser", result.getUserName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        verify(passwordEncoder).encode("hashedPasswordUpdated");

        User updatedUser = userCaptor.getValue();

        assertEquals("newHashedPassword", updatedUser.getPassword());
    }

    @Test
    void editProfile_shouldUpdateUsernameAndPasswordSuccessfully() throws Exception {

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                "newPassword123"
        );

        when(userRepository.findByUserName("NewUsername"))
                .thenReturn(null);

        when(passwordEncoder.encode(updateUserDTO.getPassword()))
                .thenReturn("newHashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.editProfile(updateUserDTO);

        assertEquals("NewUsername", result.getUserName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        verify(passwordEncoder).encode("newPassword123");

        User updatedUser = userCaptor.getValue();

        assertEquals("NewUsername", updatedUser.getUserName());
        assertEquals("newHashedPassword", updatedUser.getPassword());
    }

    @Test
    void editProfile_shouldThrowExceptionWhenUsernameAlreadyExists() throws Exception {

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        User existingUser = new User(
                "NewUsername",
                "other@example.com",
                "hashedPassword"
        );

        Field existingUserIdField = User.class.getDeclaredField("id");
        existingUserIdField.setAccessible(true);
        existingUserIdField.set(existingUser, 2L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "NewUsername",
                null
        );

        when(userRepository.findByUserName(updateUserDTO.getUserName()))
                .thenReturn(existingUser);

        assertThrows(
                ValidationException.class,
                () -> userService.editProfile(updateUserDTO)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editProfile_shouldAllowCurrentUsername() throws Exception {

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                "oldUser",
                null
        );

        when(userRepository.findByUserName("oldUser"))
                .thenReturn(user);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.editProfile(updateUserDTO);

        assertEquals("oldUser", result.getUserName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();

        assertEquals("oldUser", updatedUser.getUserName());
    }

    @Test
    void editProfile_shouldAllowEmptyFields() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UpdateUserDTO updateUserDTO = new UpdateUserDTO(
                null,
                null
        );

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.editProfile(updateUserDTO);

        assertEquals("oldUser", result.getUserName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());
        verifyNoInteractions(passwordEncoder);

        User updatedUser = userCaptor.getValue();

        assertEquals("oldUser", updatedUser.getUserName());
        assertEquals("hashedPassword", updatedUser.getPassword());
    }

    @Test
    void validateUserName_shouldPassWhenUsernameIsAvailable() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        when(userRepository.findByUserName("testUserUpdated"))
                .thenReturn(null);

        assertDoesNotThrow(() ->
                userService.validateUserName("testUserUpdated")
        );
    }

    @Test
    void validateUserName_shouldThrowExceptionWhenUsernameAlreadyExists() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        User existingUser = new User(
                "testUserUpdated",
                "other@example.com",
                "hashedPassword"
        );

        Field existingUserIdField = User.class.getDeclaredField("id");
        existingUserIdField.setAccessible(true);
        existingUserIdField.set(existingUser, 2L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        when(userRepository.findByUserName("testUserUpdated"))
                .thenReturn(existingUser);

        assertThrows(
                ValidationException.class,
                () -> userService.validateUserName("testUserUpdated")
        );
    }

    @Test
    void validateUserName_shouldAllowCurrentUsername() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        when(userRepository.findByUserName("oldUser"))
                .thenReturn(user);

        assertDoesNotThrow(() ->
                userService.validateUserName("oldUser")
        );
    }

    @Test
    void deleteProfile_shouldDeleteCurrentUser() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        userService.deleteProfile();

        verify(userRepository).deleteById(1L);
    }

    @Test
    void getCurrentUserLogged_shouldReturnCurrentUser() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        UserResponseDTO result = userService.getCurrentUserLogged();

        assertEquals("oldUser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getCurrentUserLogged_shouldThrowExceptionWhenUserDoesNotExist() throws Exception{

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        java.util.List.of()
                )
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getCurrentUserLogged()
        );
    }

    @Test
    void getCurrentUserLogged_shouldThrowExceptionWhenUserIsNotAuthenticated(){

        assertThrows(
                AccessDeniedException.class,
                () -> userService.getCurrentUserLogged()
        );
    }

    @Test
    void getCurrentUserLogged_shouldThrowExceptionWhenPrincipalIsInvalid() throws Exception{

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "invalid-principal",
                        null,
                        java.util.List.of()
                )
        );

        assertThrows(
                AccessDeniedException.class,
                () -> userService.getCurrentUserLogged()
        );
    }

    @Test
    void mapToUserResponseDTO_shouldMapUserCorrectly() throws Exception {

        User user = new User(
                "oldUser",
                "test@example.com",
                "hashedPassword"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        UserResponseDTO result = userService.mapToUserResponseDTO(user);

        assertEquals(1L, result.getId());
        assertEquals("oldUser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());

    }

    @Test
    void mapToEntity_shouldMapRegisterDTOCorrectly() {

        RegisterDTO registerDTO = new RegisterDTO(
                "testUser",
                "test@example.com",
                "hashedPassword"
        );

        User result = userService.mapToEntity(registerDTO);

        assertEquals("testUser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getPassword());
    }
}
