package sia.sever.dto.user;

public class UsernameValidationDTO {

    // Fields
    private String userName;

    // Constructor
    public UsernameValidationDTO(){}
    public UsernameValidationDTO(String userName){
        this.userName = userName;
    }

    // Getters
    public String getUserName(){
        return userName;
    }
}
