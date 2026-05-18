package sia.sever.dto.lookup;

import sia.sever.enums.ServiceCategory;

public class ServiceTypeLookupDTO {

    // Fields
    private String value;
    private String displayName;
    private ServiceCategory serviceCategory;
    private int intervalKM;
    private int intervalMonths;

    // Constructor
    public ServiceTypeLookupDTO(String value, String displayName, ServiceCategory serviceCategory, int intervalKM, int intervalMonths){
        this.value = value;
        this.displayName = displayName;
        this.serviceCategory = serviceCategory;
        this.intervalKM = intervalKM;
        this.intervalMonths = intervalMonths;
    }

    // Getters
    public String getValue(){
        return value;
    }

    public String getDisplayName(){
        return displayName;
    }

    public ServiceCategory getServiceCategory(){
        return serviceCategory;
    }

    public int getIntervalKM(){
        return intervalKM;
    }

    public int getIntervalMonths(){
        return intervalMonths;
    }
}
