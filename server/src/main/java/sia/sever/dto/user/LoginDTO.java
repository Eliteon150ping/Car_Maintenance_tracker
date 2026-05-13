package sia.sever.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    // Fields
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid format")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;

    // Constructor
    public LoginDTO() {}
    public LoginDTO(String email, String password) {

        this.email = email;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
