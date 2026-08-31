package sia.sever.dto.serviceRecord;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sia.sever.enums.ServiceType;

public class UpdateServiceRecordDTO {

    // Fields
    @Size(max= 500)
    private String description;

    @NotNull
    @Positive
    private Double cost;

    // Constructor
    public UpdateServiceRecordDTO(){}
    public UpdateServiceRecordDTO(String description, Double cost){

        this.description = description;
        this.cost = cost;
    }

    // Getters
    public String getDescription(){
        return description;
    }

    public Double getCost(){
        return cost;
    }

    // Setters
    public void setDescription(String description){
        this.description = description;
    }

    public void setCost(Double cost){
        this.cost = cost;
    }
}
