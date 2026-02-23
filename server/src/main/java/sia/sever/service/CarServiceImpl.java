package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.Car;
import sia.sever.repository.CarRepository;

import java.util.List;


@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository){

        this.carRepository = carRepository;
    }

    @Override
    public Car createCar(Car car){
        return carRepository.save(car);
    }

    @Override
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    @Override
    public Car updateCar(Long id, Car car){
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id){
       carRepository.deleteById(id);
    }

    @Override
    public Car getCarById(Long id){
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found by ID"));
    }

    @Override
    public List<Car> getAllCarsByBrand(String brand){
        return carRepository.findByBrand(brand);
    }

    @Override
    public List<Car> getAllCarsByModel(String model){
        return carRepository.findByModel(model);
    }

    @Override
    public List<Car> getAllCarsByYear(Integer year){
        return carRepository.findByYear(year);
    }

    @Override
    public List<Car> getAllCarsByBrandAndModelAndYear(String brand, String model, Integer year){
        if(brand != null && model != null && year != null){
            return carRepository.findByBrandAndModelAndYear(brand, model, year);
        }else if(brand != null && model != null){
            return carRepository.findByBrandAndModel(brand, model);
        }else if(brand != null){
            return carRepository.findByBrand(brand);
        } else if(model != null){
            return carRepository.findByModel(model);
        }else if(year != null){
            return carRepository.findByYear(year);
        }else {
            return carRepository.findAll();
        }
    }
}




