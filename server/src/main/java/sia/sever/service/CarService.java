package sia.sever.service;

import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.UpdateCarDTO;

import java.util.List;

public interface CarService {

    // These methods must be defined in the class that uses this interface(eg. CarServiceImpl)
    CarResponseDTO createCar(CreateCarDTO car);
    List<CarResponseDTO> getAllCars();
    CarResponseDTO updateCar(Long id, UpdateCarDTO car);
    void deleteCar(Long id);
    CarResponseDTO getCarById(Long id);
    List<CarResponseDTO> getAllCarsByBrandAndModelAndYear(String brand,String model, Integer year);

}
