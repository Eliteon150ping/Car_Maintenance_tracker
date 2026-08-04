package sia.sever.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.entity.User;
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
    Optional<ServiceHistory> findByIdAndCarUser(Long id, User user);
    List<ServiceHistory> findByCarAndServiceDate(Car car, LocalDate serviceDate);
    List<ServiceHistory> findByCarUserAndServiceType(User user, ServiceType serviceType);
    List<ServiceHistory> findByCarUserAndServiceTypeIn(User user, List<ServiceType> filteredServiceTypes);
    List<ServiceHistory> findByCarOrderByServiceDateDesc(Car car);
    List<ServiceHistory> findAllByCarUser(User user);
    List<ServiceHistory> findByCarAndServiceType(Car car, ServiceType serviceType);
    List<ServiceHistory> findByCarAndServiceTypeIn(Car car, List<ServiceType> serviceType);
    ServiceHistory findFirstByCarOrderByMileageAtServiceDesc(Car car);
    ServiceHistory findFirstByCarOrderByServiceDateDesc(Car car);

    // Pagination methods
    Page<ServiceHistory> findByCarAndServiceDate(Car car, LocalDate serviceDate, Pageable pageable);
    Page<ServiceHistory> findByCar(Car car, Pageable pageable);
    Page<ServiceHistory> findByCarAndServiceType(Car car, ServiceType serviceType, Pageable pageable);
    Page<ServiceHistory> findByCarAndServiceTypeIn(Car car, List<ServiceType> filteredServiceTypes, Pageable pageable);
    Page<ServiceHistory> findByCarUserAndServiceType(User user, ServiceType serviceType, Pageable pageable);
    Page<ServiceHistory> findByCarUserAndServiceTypeIn(User user, List<ServiceType> filteredServiceTypes, Pageable pageable);

}
