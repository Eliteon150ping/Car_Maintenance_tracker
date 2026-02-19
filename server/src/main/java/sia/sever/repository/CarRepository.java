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

    List<Car> findByBrand(String brand);
    List<Car> findByModel(String model);
    List<Car> findByYear(int year);
    List<Car> findByYearAndModel(int year, String model);

}
