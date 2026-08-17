package sia.sever.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserDTO {

    // Fields
    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String userName;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // Constructor
    public UpdateUserDTO() {}
    public UpdateUserDTO(String userName, String password) {

        this.userName = userName;
        this.password = password;
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
