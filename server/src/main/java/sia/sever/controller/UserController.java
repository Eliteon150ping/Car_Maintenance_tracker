package sia.sever.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "Register a new user",
            description = "Register a new account",
            tags = {"Users"}
    )
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterDTO user) {
        UserResponseDTO registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // Login User
    @Operation(
            summary = "Log in a user",
            description = "Log into your account",
            tags = {"Users"}
    )
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginDTO user) {
        AuthResponseDTO loggedInUser = userService.loginUser(user);
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }

    // Edit profile
    @Operation(
            summary = "Update the current user's profile",
            description = "Edit your profile details",
            tags = {"Users"}
    )
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> editProfile(@Valid @RequestBody UpdateUserDTO user){
        UserResponseDTO editedProfile = userService.editProfile(user);
        return ResponseEntity.ok(editedProfile);
    }

    // Check the username before showing the confirmation box(insane how all this needs to be done for that)
    @Operation(
            summary = "Validate username",
            description = "Checks whether the requested username is available before updating the user's profile.",
            tags = {"Users"}
    )
    @PutMapping("/profile/username")
    public ResponseEntity<Void> validateUserName(@RequestBody UsernameValidationDTO userName){
        userService.validateUserName(userName.getUserName());
        return ResponseEntity.noContent().build();
    }

    // Delete profile
    @Operation(
            summary = "Delete the current user's account",
            description = "Delete your account",
            tags = {"Users"}
    )
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(){
        userService.deleteProfile();
        return ResponseEntity.noContent().build();
    }

    // Get current User's JWT checked if already logged in on the frontend when refreshing(F5)
    @Operation(
            summary = "Get current user",
            description = "Gets the profile details of the currently authenticated user.",
            tags = {"Users"}
    )
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getCurrentUser(){
        UserResponseDTO getCurrentUser = userService.getCurrentUserLogged();
        return ResponseEntity.ok(getCurrentUser);
    }
}
