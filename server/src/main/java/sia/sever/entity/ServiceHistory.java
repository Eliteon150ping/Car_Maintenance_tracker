package sia.sever.entity;

import jakarta.persistence.*;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;

@Entity
@Table(name = "ServiceHistory")
public class ServiceHistory {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private int mileageAtService;

    @Enumerated(EnumType.STRING)            // Used for enums to show the actual name of
    private ServiceType serviceType;        // the constant in the db instead of its index value
                                            // making it easier to read in the db.

    // Multiple services belong to a car over time(Many-to-one relationship)
    @ManyToOne
    @JoinColumn(name = "car_id",nullable = false)
    private Car car;

    // Constructor
    public ServiceHistory(){}
    public ServiceHistory(LocalDate serviceDate, int mileageAtService, ServiceType serviceType, Car car) {
        this.serviceDate = serviceDate;
        this.mileageAtService = mileageAtService;
        this.serviceType = serviceType;
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

    public ServiceType getServiceType(){
        return serviceType;
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

    public void setServiceType(ServiceType serviceType){
        this.serviceType = serviceType;
    }

    public void setCar(Car car){
        this.car = car;
    }
}
