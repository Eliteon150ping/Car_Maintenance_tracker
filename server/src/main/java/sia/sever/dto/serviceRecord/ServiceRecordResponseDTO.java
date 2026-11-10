package sia.sever.dto.serviceRecord;

import sia.sever.dto.car.CarSummaryDTO;
import sia.sever.enums.ServiceType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ServiceRecordResponseDTO {

    // Fields
    private Long id;
    private LocalDate serviceDate;
    private int mileageAtService;
    private Integer nextDueMileage;
    private LocalDate nextDueDate;
    private ServiceType serviceType;
    private double cost;
    private String description;
    private Integer remainingKm;
    private Integer remainingDays;
    private CarSummaryDTO car;
    private boolean isLatestRecord;

    // Constructor
    @JsonCreator
    public ServiceRecordResponseDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("serviceDate") LocalDate serviceDate,
            @JsonProperty("mileageAtService") int mileageAtService,
            @JsonProperty("nextDueMileage") Integer nextDueMileage,
            @JsonProperty("nextDueDate") LocalDate nextDueDate,
            @JsonProperty("serviceType") ServiceType serviceType,
            @JsonProperty("cost") double cost,
            @JsonProperty("description") String description,
            @JsonProperty("remainingKm") Integer remainingKm,
            @JsonProperty("remainingDays") Integer remainingDays,
            @JsonProperty("car")  CarSummaryDTO car,
            @JsonProperty("latestRecord")  boolean isLatestRecord) {

        this.id = id;
        this.serviceDate = serviceDate;
        this.mileageAtService = mileageAtService;
        this.nextDueMileage = nextDueMileage;
        this.nextDueDate = nextDueDate;
        this.serviceType = serviceType;
        this.cost = cost;
        this.description = description;
        this.remainingKm = remainingKm;
        this.remainingDays = remainingDays;
        this.car = car;
        this.isLatestRecord = isLatestRecord;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public int getMileageAtService() {
        return mileageAtService;
    }

    public Integer getNextDueMileage() {
        return nextDueMileage;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public double getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public Integer getRemainingKm() {
        return remainingKm;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public CarSummaryDTO getCar() {
        return car;
    }

    public boolean isLatestRecord() {
        return isLatestRecord;
    }
}

