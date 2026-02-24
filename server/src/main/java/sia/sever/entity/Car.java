package sia.sever.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Car")
public class Car {

    //Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String colour;

    @Column(nullable = false)
    private int mileage;

    //Constructor
    public Car(){}
    public Car(String brand, String model, int year, String colour, int mileage) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.colour = colour;
        this.mileage = mileage;
    }

    //Getters
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

    public int getMileage(){
        return mileage;
    }

    //Setters
    public void setBrand(String brand){
        this.brand = brand;
    }

    public void setModel(String model){
        this.model = model;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setColour(String colour){
        this.colour = colour;
    }

    public void setMileage(int mileage){
        this.mileage = mileage;
    }

}
