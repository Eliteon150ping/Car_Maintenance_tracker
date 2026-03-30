package sia.sever.dto.car;

/* Response DTO(Data transfer Object) is what data you want to send to the Frontend, it's better than just
   sending the entity directly because it can cause issues later down the line, DTO's allow you to choose
   what you want to send instead                                                                             */
public class CarResponseDTO {

    // Fields to sent to the frontend
    private Long id;
    private String brand;
    private String model;
    private int year;
    private String colour;
    private int currentMileage;

    // Constructor
    public CarResponseDTO(Long id, String brand, String model, int year, String colour, int currentMileage){
        this.id  = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.colour = colour;
        this.currentMileage = currentMileage;
    }

    // Getters only as you don't want someone else to set info the backend gives
    public Long getId(){
        return id;
    }

    public String getBrand(){
        return brand;
    }

    public String getModel(){
        return model;
    }

    public int getYear(){
        return year;
    }

    public String getColour(){
        return colour;
    }

    public int getCurrentMileage(){
        return currentMileage;
    }

}
