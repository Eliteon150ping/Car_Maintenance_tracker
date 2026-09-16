package sia.sever.dto.car;

public class MileageValidationDTO {

    // Fields
    private int mileage;

    // Constructor
    public MileageValidationDTO(){}
    public MileageValidationDTO(int mileage){
        this.mileage = mileage;
    }

    // Getters
    public int getMileage(){
        return mileage;
    }
}
