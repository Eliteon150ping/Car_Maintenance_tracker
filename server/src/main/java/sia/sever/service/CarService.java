package sia.sever.service;

import org.springframework.web.multipart.MultipartFile;
import sia.sever.dto.car.CreateCarDTO;
import sia.sever.dto.car.CarResponseDTO;
import sia.sever.dto.car.UpdateCarDTO;

import java.io.IOException;
import java.util.List;

public interface CarService {

    // These methods must be defined in the class that uses this interface(eg. CarServiceImpl)
    CarResponseDTO createCar(CreateCarDTO car, MultipartFile image) throws IOException;
    List<CarResponseDTO> getAllCars();
    CarResponseDTO updateCar(Long id, UpdateCarDTO car, MultipartFile image) throws IOException;
    void deleteCar(Long id) throws IOException;
    CarResponseDTO getCarById(Long id);
    List<CarResponseDTO> getAllCarsByBrandAndModelAndYear(String brand,String model, Integer year);
    List<CarResponseDTO> searchCars(String search);
    void validateUpdateMileage(Long id, int updateMileage);
}
