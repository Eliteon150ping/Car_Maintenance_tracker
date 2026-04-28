package sia.sever.dto.user;

public class UpdateUserDTO {

    // Fields
    private String userName;
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
