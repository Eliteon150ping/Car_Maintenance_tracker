package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sia.sever.dto.user.LoginDTO;
import sia.sever.dto.user.RegisterDTO;
import sia.sever.dto.user.UserResponseDTO;
import sia.sever.entity.User;
import sia.sever.exception.ValidationException;
import sia.sever.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Mapper for DTO and service to return a User object to the frontend
    public UserResponseDTO mapToUserResponseDTO(User user){
        return new UserResponseDTO(user.getId(), user.getUserName(), user.getEmail());
    }

    // Mapper for RegisterDTO to convert to a User object
    public User mapToEntity(RegisterDTO registerDTO){
        User user = new User();
        user.setUserName(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        return user;
    }

    // Mapper for LoginDTO to convert to a User object
    public User mapToEntity(LoginDTO loginDTO){
        User user = new User();
        user.setEmail(loginDTO.getEmail());
        user.setPassword(loginDTO.getPassword());
        return user;
    }

    // Register a user
    @Override
    public UserResponseDTO registerUser(RegisterDTO user){

        // Check if the email and username exists first
        User validateEmail = userRepository.findByEmail(user.getEmail());
        User validateUsername = userRepository.findByUserName(user.getUserName());
        List<String> errorList = new ArrayList<>();
        if(validateUsername != null){
            errorList.add("This username already exists");
        }
        if(validateEmail != null){
            errorList.add("This email already exists");
        }
        if(!errorList.isEmpty()){
            throw new ValidationException("Validation failed", errorList);
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
    public UserResponseDTO loginUser(LoginDTO user){

        // Find user by email
        User validateEmail = userRepository.findByEmail(user.getEmail());
        List<String> errorList = new ArrayList<>();
        if(validateEmail == null){
            errorList.add("Email not found, please register instead");
        }else if(!passwordEncoder.matches(user.getPassword(), validateEmail.getPassword())){
            // Compare passwords using the input one and the one stored in the db
            errorList.add("Password is incorrect");
        }
        if(!errorList.isEmpty()){
            throw new ValidationException("Login failed", errorList);
        }
        return mapToUserResponseDTO(validateEmail);
    }

    // Edit some info
    @Override
    public UserResponseDTO EditProfile(Long id, LoginDTO user){
        User convertToEntity = mapToEntity(user);
        User UpdatedUser = userRepository.save(convertToEntity);
        return mapToUserResponseDTO(UpdatedUser);
    }

    // Delete a user by id
    // Get all users
    // Get a user by id

    // Methods to help reduce duplicate code:

}
