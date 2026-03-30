package sia.sever.dto.car;

public class CarRequestDTO {

    // Fields to send to the backend
    private String brand;
    private String model;
    private int year;
    private String colour;
    private int currentMileage;

    // Constructor
    public CarRequestDTO(String brand, String model, int year, String colour, int currentMileage){
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
