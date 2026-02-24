package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.Car;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

  /*   Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

   // So if you want custom methods for filtering, make them here:

    List<Car> findByBrandContainingIgnoreCase(String brand);
    List<Car> findByModelContainingIgnoreCase(String model);
    List<Car> findByYear(int year);
    List<Car> findByBrandContainingIgnoreCaseAndModelContainingIgnoreCase(String brand, String model);
    List<Car> findByBrandContainingIgnoreCaseAndYear(String brand, int year);
    List<Car> findByModelContainingIgnoreCaseAndYear(String model, int year);
    List<Car> findByBrandContainingIgnoreCaseAndModelContainingIgnoreCaseAndYear(String brand, String model, int year);

}
