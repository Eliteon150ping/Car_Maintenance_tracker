package sia.sever.dto.serviceRecord;

import java.time.LocalDate;

public class LatestServiceInfoDTO {

    private Integer latestServiceMileage;
    private LocalDate latestServiceDate;

    public LatestServiceInfoDTO(Integer latestServiceMileage, LocalDate latestServiceDate) {
        this.latestServiceMileage = latestServiceMileage;
        this.latestServiceDate = latestServiceDate;
    }

    public Integer getLatestServiceMileage() {
        return latestServiceMileage;
    }

    public LocalDate getLatestServiceDate() {
        return latestServiceDate;
    }
}