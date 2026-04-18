package sia.sever.entity;

import jakarta.persistence.*;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;

@Entity
@Table(name = "service_History")
public class ServiceHistory {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private int mileageAtService;

    @Column(nullable = false)
    private Integer nextDueMileage;

    @Column(nullable = true)
    private LocalDate nextDueDate;

    @Enumerated(EnumType.STRING)            // Used for enums to show the actual name of
    private ServiceType serviceType;        // the constant in the db instead of its index value
                                            // making it easier to read in the db.

    @Column(nullable = false)
    private double cost;

    @Column(length = 500, nullable = true)
    private String description;

    // Multiple services belong to a car over time(Many-to-one relationship)
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    // Constructor
    public ServiceHistory(){}
    public ServiceHistory(LocalDate serviceDate, int mileageAtService, Integer nextDueMileage,
                          LocalDate nextDueDate, ServiceType serviceType, double cost,
                          String description, Car car)
    {
        this.serviceDate = serviceDate;
        this.mileageAtService = mileageAtService;
        this.nextDueMileage = nextDueMileage;
        this.nextDueDate = nextDueDate;
        this.serviceType = serviceType;
        this.cost = cost;
        this.description = description;
        this.car = car;
    }

    // Getters
    public Long getId(){
        return id;
    }

    public LocalDate getServiceDate(){
        return serviceDate;
    }

    public int getMileageAtService(){
        return mileageAtService;
    }

    public Integer getNextDueMileage() {
        return nextDueMileage;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
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

    public Car getCar(){
        return car;
    }

    // Setters
    public void setServiceDate(LocalDate serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setMileageAtService(int mileageAtService){
        this.mileageAtService = mileageAtService;
    }

    public void setNextDueMileage(Integer nextDueMileage){
        this.nextDueMileage = nextDueMileage;
    }

    public void setNextDueDate(LocalDate nextDueDate){
        this.nextDueDate = nextDueDate;
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

    public void setCar(Car car){
        this.car = car;
    }
}

/*

Json format body for a car's service history:

{

"serviceType": "",
"serviceDate": "",
"mileageAtService":  ,
"cost": ,
"description": "", (Optional unless its OTHER)

}

*/