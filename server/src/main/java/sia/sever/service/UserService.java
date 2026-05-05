package sia.sever.service;

import sia.sever.dto.user.*;

import java.util.List;

public interface UserService {

    UserResponseDTO registerUser(RegisterDTO user);
    AuthResponseDTO loginUser(LoginDTO user);
    UserResponseDTO editProfile(UpdateUserDTO user);
    void deleteProfile();
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getCurrentUserLogged();
}
