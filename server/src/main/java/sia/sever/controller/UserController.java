package sia.sever.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.sever.dto.user.*;
import sia.sever.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // NB: Once jwt is implemented, you do NOT pass path-variable/id for users in the endpoints since the
    // backend knows the user from the jwt token

    // Register User
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterDTO user) {
        UserResponseDTO registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // Login User
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginDTO user) {
        AuthResponseDTO loggedInUser = userService.loginUser(user);
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }

    // Edit profile
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> editProfile(@Valid @RequestBody UpdateUserDTO user){
        UserResponseDTO editedProfile = userService.editProfile(user);
        return ResponseEntity.ok(editedProfile);
    }

    // Delete profile
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(){
        userService.deleteProfile();
        return ResponseEntity.noContent().build();
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    // Get current User's JWT checked if already logged in on the frontend when refreshing(F5)
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUser(){
        UserResponseDTO getCurrentUser = userService.getCurrentUserLogged();
        return ResponseEntity.ok(getCurrentUser);
    }
}
