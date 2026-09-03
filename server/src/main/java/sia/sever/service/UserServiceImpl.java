package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sia.sever.dto.user.*;
import sia.sever.entity.User;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.exception.ValidationException;
import sia.sever.repository.UserRepository;
import sia.sever.security.jwt.JwtUtility;
import sia.sever.security.userDetails.CustomUserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtility jwtUtility) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtility = jwtUtility;
    }

    // NB: Only create entity mappers when you're creating new records — not when reading or updating

    // Mapper for DTO and service to return a User object to the frontend
    public UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUserName(), user.getEmail());
    }

    // Mapper for RegisterDTO to convert to a User object
    public User mapToEntity(RegisterDTO registerDTO) {
        User user = new User();
        user.setUserName(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        return user;
    }

    // Register a user
    @Override
    public UserResponseDTO registerUser(RegisterDTO user) {

        // Check if the email and username exists first
        User validateEmail = userRepository.findByEmail(user.getEmail());
        User validateUsername = userRepository.findByUserName(user.getUserName());
        Map<String, String> errors = new HashMap<>();
        if (validateUsername != null) {
            errors.put("userName" ," This username already exists, please try a different one ");
        }
        if (validateEmail != null) {
            errors.put("email" ," This email already exists, please try a different one ");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }

        // Encode password and save the user
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        User convertToEntity = mapToEntity(user);
        convertToEntity.setPassword(hashedPassword);
        User savedUser = userRepository.save(convertToEntity);
        return mapToUserResponseDTO(savedUser);
    }

    // Login
    @Override
    public AuthResponseDTO loginUser(LoginDTO user) {

        // Find user by email
        User validateUser = userRepository.findByEmail(user.getEmail());
        Map<String, String> errors = new HashMap<>();

        if (validateUser == null) {
            errors.put("email" ,"Email not found, please register instead");
        } else if (!passwordEncoder.matches(user.getPassword(), validateUser.getPassword())) {
            // Compare passwords using the input one and the one stored in the db
            errors.put("password", "Password is incorrect");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException("Login failed", errors);
        }

        // This is added after jwt is implemented
        String token = jwtUtility.generateToken(
                validateUser.getId(),
                validateUser.getEmail()
        );

        return new AuthResponseDTO(token, mapToUserResponseDTO(validateUser));
    }

    // Edit profile info
    @Override
    public UserResponseDTO editProfile(UpdateUserDTO user) {

        User existingUser = getCurrentUser();
        Map<String, String> errors = new HashMap<>();

        if (user.getUserName() != null && !user.getUserName().isBlank()) {

            User validateUserName = userRepository.findByUserName(user.getUserName());

            if(validateUserName != null && !validateUserName.getId().equals(existingUser.getId())){
                errors.put("userName" ,"Username already exists. Please type a different one");
            }else{
                existingUser.setUserName(user.getUserName());
            }
        }
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        if(!errors.isEmpty()){
            throw new ValidationException("Update failed", errors);
        }

        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponseDTO(updatedUser);
    }

    // Delete a user by id
    @Override
    public void deleteProfile() {
        User existingUser = getCurrentUser();
        userRepository.deleteById(existingUser.getId());
    }

    // Get all users
    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> getAllUsers = userRepository.findAll();
        return getAllUsers.stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    // Get a user by id
    @Override
    public UserResponseDTO getCurrentUserLogged() {
        User existingUser = getCurrentUser();
        return mapToUserResponseDTO(existingUser);
    }

    // Get current user(helps prevent some users from accessing other user's info)
    private User getCurrentUser() {

        if (SecurityContextHolder.getContext().getAuthentication() == null || !(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Unauthorized");
        }
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id: " + userDetails.getUserId()));
    }
}
