package sia.sever.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterDTO {

    // Fields
    @NotBlank(message = "Username is required")
    private String userName;
    @NotBlank(message = "Email is required")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Email is invalid format")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;

    // Constructor
    public RegisterDTO(){}
    public RegisterDTO(String userName, String email, String password){

        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    // Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
