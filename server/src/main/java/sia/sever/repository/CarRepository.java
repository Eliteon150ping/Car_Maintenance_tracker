package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sia.sever.entity.Car;
import sia.sever.entity.User;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor {

  /*   Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

   // So if you want custom methods for filtering, make them here or use Specification methods instead:
    List<Car> findAllByUserOrderByIdAsc(User user);
    Optional<Car> findByIdAndUser(Long id, User user);
}
