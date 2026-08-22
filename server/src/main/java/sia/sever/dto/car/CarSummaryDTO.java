package sia.sever.dto.car;

public class CarSummaryDTO {

    /*  Made a summary of car info to use in other dtos to show only the necessary info                      */

    // Fields to sent to the frontend
    private Long id;
    private String brand;
    private String model;
    private int year;
    private int currentMileage;

    // Constructor
    public CarSummaryDTO(Long id, String brand, String model, int year, int currentMileage) {

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.currentMileage = currentMileage;
    }

    // Getters only as you don't want someone else to set info the backend gives
    public Long getId(){
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

}


