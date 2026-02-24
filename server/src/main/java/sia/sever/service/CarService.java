package sia.sever.service;

import sia.sever.entity.Car;
import java.util.List;

public interface CarService {

    Car createCar(Car car);
    List<Car> getAllCars();
    Car updateCar(Long id, Car car);
    void deleteCar(Long id);
    Car getCarById(Long id);
    List<Car> getAllCarsByBrandAndModelAndYear(String brand,String model, Integer year);

}
