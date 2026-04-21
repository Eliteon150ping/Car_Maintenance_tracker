package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
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
        user.setPassword(registerDTO.getPassword());
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

        // Check if the email exists first
        User validateEmail = userRepository.findByEmail(user.getEmail());
        User validateUsername = userRepository.findByUserName(user.getUserName());
        List<String> errorList = new ArrayList<>();
        if((validateEmail != null && validateUsername != null)){
            errorList.add("This username already exists");
            errorList.add("This email already exists");
            throw new ValidationException("Validation failed", errorList);
        }
        if(validateUsername != null){
            errorList.add("This username already exists");
            throw new ValidationException("Validation failed", errorList);
        }
        if(validateEmail != null){
            errorList.add("This email already exists");
            throw new ValidationException("Validation failed", errorList);
        }
        User convertToEntity = mapToEntity(user);
        User savedUser = userRepository.save(convertToEntity);
        return mapToUserResponseDTO(savedUser);
    }

    // Login
    @Override
    public UserResponseDTO loginUser(LoginDTO user){
        User convertToEntity = mapToEntity(user);
        User LoggedUser = userRepository.save(convertToEntity);
        return mapToUserResponseDTO(LoggedUser);
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
