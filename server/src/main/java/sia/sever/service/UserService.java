package sia.sever.service;

import sia.sever.dto.user.LoginDTO;
import sia.sever.dto.user.RegisterDTO;
import sia.sever.dto.user.UpdateUserDTO;
import sia.sever.dto.user.UserResponseDTO;
import java.util.List;

public interface UserService {

    UserResponseDTO registerUser(RegisterDTO user);
    UserResponseDTO loginUser(LoginDTO user);
    UserResponseDTO editProfile(Long id, UpdateUserDTO user);
    void deleteProfile(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
}
