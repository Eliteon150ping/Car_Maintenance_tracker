package sia.sever.dto.car;

public class UpdateCarDTO {

    // Fields to be sent to the frontend
    private String colour;
    private Integer currentMileage;

    // Constructor
    public UpdateCarDTO(){}
    public UpdateCarDTO(String colour, Integer currentMileage){

        this.colour = colour;
        this.currentMileage = currentMileage;
    }

    // Getters
    public String getColour(){return colour;}

    public Integer getCurrentMileage(){return currentMileage;}

    // Setters
    public void setColour(String colour){
        this.colour = colour;
    }

    public void setCurrentMileage(Integer currentMileage){
        this.currentMileage = currentMileage;
    }
}
