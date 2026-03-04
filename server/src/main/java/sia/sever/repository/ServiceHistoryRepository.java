package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceType;

import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {

    /* Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

    // So if you want custom methods for filtering, make them here:
    List<ServiceHistory> findByServiceDate(LocalDate serviceDate);
    List<ServiceHistory> findByServiceType(ServiceType serviceType);
    List<ServiceHistory> findByCar(Car car);
}
