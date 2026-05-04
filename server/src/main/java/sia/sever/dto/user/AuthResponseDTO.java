package sia.sever.dto.user;

public class AuthResponseDTO {

    // Fields
    private String token;
    private UserResponseDTO userResponseDTO;

    // Constructor
    public AuthResponseDTO(String token, UserResponseDTO userResponseDTO){

        this.token = token;
        this.userResponseDTO = userResponseDTO;
    }

    // Getters
    public String getToken(){
        return token;
    }

    public UserResponseDTO getUserResponseDTO(){
        return userResponseDTO;
    }
}
