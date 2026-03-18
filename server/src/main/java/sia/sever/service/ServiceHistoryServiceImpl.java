package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceHistoryServiceImpl implements ServiceHistoryService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final ServiceHistoryRepository serviceHistoryRepository;
    private final CarRepository carRepository;

    @Autowired
    public ServiceHistoryServiceImpl(ServiceHistoryRepository serviceHistoryRepository,
                                     CarRepository carRepository)
    {
        this.serviceHistoryRepository = serviceHistoryRepository;
        this.carRepository = carRepository;
    }

    // Create a service record
    @Override
    public ServiceHistory createServiceHistory(ServiceHistory serviceHistory, Long carId){

        // First retrieve an existing car through its id
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("No car found with id: " + carId));
        serviceHistory.setCar(car);
        ServiceHistory lastLatestServiceMileage = serviceHistoryRepository.findFirstByCarOrderByMileageAtServiceDesc(car);

        // Check if the service mileage is NOT more than the car's current mileage and NOT less than the last service
        validateMileage(serviceHistory);
        if(lastLatestServiceMileage != null) {
            if (serviceHistory.getMileageAtService() < lastLatestServiceMileage.getMileageAtService()) {
                throw new RuntimeException("New service mileage cannot be lower than the last latest service mileage");
            }
        }

        // Check if 'Other' service is selected then make use of custom notes for it
        validateOtherServiceDescription(serviceHistory);

        // Check if user did a service, give the next change interval/date
        serviceHistory.setNextDueMileage(calculateNextServiceMileage(serviceHistory));
        serviceHistory.setNextDueDate(calculateNextServiceDate(serviceHistory));

        // Check if user did not do a service, give the km/date remaining
        calculateRemainingKm(serviceHistory);
        calculateRemainingDays(serviceHistory);

        return serviceHistoryRepository.save(serviceHistory);
    }

    // Get all service records
    @Override
    public List<ServiceHistory> getAllServiceRecords(){
        return serviceHistoryRepository.findAll();
    }

    // Find the service record by id
    @Override
    public ServiceHistory getServiceHistoryById(Long id){
        return serviceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record found with id: " + id));
    }

    // Update the service record
    @Override
    public ServiceHistory updateServiceHistory(Long id, ServiceHistory updatedServiceHistory, Long carId){
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("No car found with id: " + carId));
        updatedServiceHistory.setCar(car);
        ServiceHistory existingServiceHistory = serviceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record found with id: " + id));

            // Check if 'Other' service is selected then make use of custom notes for it
            validateOtherServiceDescription(updatedServiceHistory);

            existingServiceHistory.setDescription(updatedServiceHistory.getDescription());
            existingServiceHistory.setCost(updatedServiceHistory.getCost());
        return serviceHistoryRepository.save(existingServiceHistory);
    }

    // Filter different Service categories
    @Override
    public List<ServiceHistory> getServiceHistoryByCategory(ServiceCategory serviceCategory){
        List<ServiceType> matchingServiceTypes = new ArrayList<>();
        for(ServiceType serviceTypes : ServiceType.values()){
            if(serviceTypes.getServiceCategory() == serviceCategory){
                matchingServiceTypes.add(serviceTypes);
            }
        }
        return serviceHistoryRepository.findByServiceTypeIn(matchingServiceTypes);
    }

    // Filter different Service types
    @Override
    public List<ServiceHistory> getServiceHistoryByServiceType(ServiceType serviceType){
        return serviceHistoryRepository.findByServiceType(serviceType);
    }

    // Filter different Services by dates
    @Override
    public List<ServiceHistory> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate){
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("No car found with id: " + carId));
        return serviceHistoryRepository.findByCarAndServiceDate(car, serviceDate);
    }

    // Filter all Service records for a car
    @Override
    public List<ServiceHistory> getServiceHistoryByCar(Long carId){
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("No car found with id: " + carId));
        return serviceHistoryRepository.findByCar(car);
    }

    // Methods to help reduce duplicate code:

    // Check if the service mileage is NOT more than the car's current mileage
    private void validateMileage(ServiceHistory serviceHistory){
        if(serviceHistory.getMileageAtService() > serviceHistory.getCar().getCurrentMileage()){
            throw new RuntimeException("Service mileage cannot be higher than Current Mileage");
        }
    }

    // Check if 'Other' service is selected then make use of custom notes for it
    private void validateOtherServiceDescription(ServiceHistory serviceHistory){
        if(serviceHistory.getServiceType() == ServiceType.OTHER && (serviceHistory.getDescription() == null
                || serviceHistory.getDescription().trim().isEmpty()))
        {
            throw new RuntimeException("Description cannot be empty when service type is OTHER");
        }
    }

    // Check if user did a service, give the next change interval
    private int calculateNextServiceMileage(ServiceHistory serviceHistory){
        return serviceHistory.getMileageAtService() + serviceHistory.getServiceType().getIntervalKm();
    }

    // Check if user did a service, give the next change date
    private LocalDate calculateNextServiceDate(ServiceHistory serviceHistory){
        return serviceHistory.getServiceDate().plusMonths(serviceHistory.getServiceType().getIntervalMonths());
    }

    // Check if user did not do a service, give the km remaining
    private int calculateRemainingKm(ServiceHistory serviceHistory){
        return calculateNextServiceMileage(serviceHistory) - serviceHistory.getCar().getCurrentMileage();
    }

    // Check if user did not do a service, give the days remaining
    private int calculateRemainingDays(ServiceHistory serviceHistory){
        return (int) LocalDate.now().until(calculateNextServiceDate(serviceHistory), ChronoUnit.DAYS);
    }
}
