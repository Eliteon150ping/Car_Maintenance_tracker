package sia.sever.dto.serviceRecord;

import sia.sever.enums.ServiceType;

import java.time.LocalDate;

public class ServiceRecordRequestDTO {

    // Fields
    private LocalDate serviceDate;
    private int mileageAtService;
    private ServiceType serviceType;
    private double cost;
    private String description;

    // Constructor
    public ServiceRecordRequestDTO(){}
    public ServiceRecordRequestDTO(LocalDate serviceDate, int mileageAtService, ServiceType serviceType,
                                   double cost, String description)
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

    public int getMileageAtService(){
        return mileageAtService;
    }

    public ServiceType getServiceType(){
        return serviceType;
    }

    public double getCost(){
        return cost;
    }

    public String getDescription(){
        return description;
    }

    // Setters
    public void setServiceDate(LocalDate serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setMileageAtService(int mileageAtService){
        this.mileageAtService = mileageAtService;
    }

    public void setServiceType(ServiceType serviceType){
        this.serviceType = serviceType;
    }

    public void setCost(double cost){
        this.cost = cost;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
