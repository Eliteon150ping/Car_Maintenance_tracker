package sia.sever.service;

import sia.sever.entity.Car;
import java.util.List;

public interface CarService {

    Car createCar(Car car);
    List<Car> getAllCars();
    Car updateCar(Car car);
    void deleteCar(Long id);
    Car getCarById(Long id);
    List<Car> getAllCarsByBrand(String brand);
    List<Car> getAllCarsByModel(String model);
    List<Car> getAllCarsByYear(int year);

}
