package sia.sever.dto.serviceRecord;

import jakarta.validation.constraints.*;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;

public class ServiceRecordRequestDTO {

    // Fields
    @NotNull
    @PastOrPresent(message = "Service date cannot be in the future.")
    private LocalDate serviceDate;
    @NotNull
    @Min(0)
    private Integer mileageAtService;
    @NotNull
    private ServiceType serviceType;
    @NotNull
    @Positive
    private Double cost;
    @Size(max = 500)
    private String description;

    // Constructor
    public ServiceRecordRequestDTO(){}
    public ServiceRecordRequestDTO(LocalDate serviceDate, Integer mileageAtService, ServiceType serviceType,
                                   Double cost, String description)
    {
        this.serviceDate = serviceDate;
        this.mileageAtService = mileageAtService;
        this.serviceType = serviceType;
        this.cost = cost;
        this.description = description;
    }

    // Getters
    public LocalDate getServiceDate(){
        return serviceDate;
    }

    public Integer getMileageAtService(){
        return mileageAtService;
    }

    public ServiceType getServiceType(){
        return serviceType;
    }

    public Double getCost(){
        return cost;
    }

    public String getDescription(){
        return description;
    }

    // Setters
    public void setServiceDate(LocalDate serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setMileageAtService(Integer mileageAtService){
        this.mileageAtService = mileageAtService;
    }

    public void setServiceType(ServiceType serviceType){
        this.serviceType = serviceType;
    }

    public void setCost(Double cost){
        this.cost = cost;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
