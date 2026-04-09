package sia.sever.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {

    /* Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

    // So if you want custom methods for filtering, make them here:
    Optional<ServiceHistory> findByIdAndCar(Long id, Car car);
    List<ServiceHistory> findByCarAndServiceDate(Car car, LocalDate serviceDate);
    List<ServiceHistory> findByServiceType(ServiceType serviceType);
    List<ServiceHistory> findByServiceTypeIn(List<ServiceType> filteredServiceTypes);
    List<ServiceHistory> findByCar(Car car);

    // Pagination methods
    Page<ServiceHistory> findByCarAndServiceDate(Car car, LocalDate serviceDate, Pageable pageable);
    Page<ServiceHistory> findByServiceType(ServiceType serviceType, Pageable pageable);
    Page<ServiceHistory> findByServiceTypeIn(List<ServiceType> filteredServiceTypes, Pageable pageable);
    Page<ServiceHistory> findByCar(Car car, Pageable pageable);
    ServiceHistory findFirstByCarOrderByMileageAtServiceDesc(Car car);
}
