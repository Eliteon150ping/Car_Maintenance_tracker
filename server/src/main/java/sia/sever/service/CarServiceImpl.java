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
    public Car updateCar(Car car){
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
    public List<Car> getAllCarsByYear(int year){
        return carRepository.findByYear(year);
    }
}
