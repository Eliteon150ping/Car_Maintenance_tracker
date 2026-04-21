package sia.sever.dto.user;

public class UserResponseDTO {

    // Fields
    private Long id;
    private String userName;
    private String email;

    // Constructor
    public UserResponseDTO(){}
    public UserResponseDTO(Long id, String userName, String email){

        this.id = id;
        this.userName = userName;
        this.email = email;
    }

    // Getters
    public Long getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }
}
