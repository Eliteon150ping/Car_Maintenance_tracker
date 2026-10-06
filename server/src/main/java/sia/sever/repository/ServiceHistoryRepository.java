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

    List<ServiceHistory> findByCarOrderByServiceDateDescMileageAtServiceDesc(Car car);

    List<ServiceHistory> findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(User user);

    ServiceHistory findFirstByCarOrderByMileageAtServiceDesc(Car car);

    ServiceHistory findFirstByCarOrderByServiceDateDesc(Car car);

    boolean existsByCarAndServiceTypeAndServiceDateAndMileageAtService(
            Car car,
            ServiceType serviceType,
            LocalDate serviceDate,
            int mileageAtService);

    ServiceHistory findFirstByCarAndServiceTypeOrderByServiceDateDescMileageAtServiceDesc(
            Car car,
            ServiceType serviceType
    );

    // Pagination methods
    Page<ServiceHistory> findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(User user, Pageable pageable);

    Page<ServiceHistory> findByCarUserAndServiceTypeInOrderByServiceDateDescMileageAtServiceDesc(User user, List<ServiceType> filteredServiceTypes, Pageable pageable
    );
}
