package sia.sever.service;

import sia.sever.dto.car.CarRequestDTO;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.entity.Car;
import java.util.List;

public interface CarService {

    // These methods must be defined in the class that uses this interface(eg. CarServiceImpl)
    CarResponseDTO createCar(CarRequestDTO car);
    List<CarResponseDTO> getAllCars();
    CarResponseDTO updateCar(Long id, CarRequestDTO car);
    void deleteCar(Long id);
    CarResponseDTO getCarById(Long id);
    List<CarResponseDTO> getAllCarsByBrandAndModelAndYear(String brand,String model, Integer year);

}
