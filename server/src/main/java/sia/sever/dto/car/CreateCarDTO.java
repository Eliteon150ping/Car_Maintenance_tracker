package sia.sever.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCarDTO {

    // Fields to send to the backend
    @NotBlank(message = "Brand is required")
    private String brand;
    @NotBlank(message = "Model is required")
    private String model;
    @NotNull
    private int year;
    @NotBlank(message = "Colour is required")
    private String colour;
    @NotNull
    private int currentMileage;

    // Constructor
    public CreateCarDTO(){} // Needed for deserializing Incoming JSON to convert to Java, only for request dtos
    public CreateCarDTO(String brand, String model, int year, String colour, int currentMileage){
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.colour = colour;
        this.currentMileage = currentMileage;
    }

    // Getters
    public String getBrand() {
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

    // Setters
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }
}
