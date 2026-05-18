package sia.sever.dto.lookup;

public class ServiceCategoryLookupDTO {

    // Fields
    private String value;
    private String displayName;

    // Constructor
    public ServiceCategoryLookupDTO(String value, String displayName){
        this.value = value;
        this.displayName = displayName;
    }

    // Getters
    public String getValue(){
        return value;
    }

    public String getDisplayName(){
        return displayName;
    }
}
