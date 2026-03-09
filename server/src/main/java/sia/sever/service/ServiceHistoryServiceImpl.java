package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.repository.ServiceHistoryRepository;
import java.time.LocalDate;
import java.util.List;

@Service
public class ServiceHistoryServiceImpl implements ServiceHistoryService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final ServiceHistoryRepository serviceHistoryRepository;

    @Autowired
    public ServiceHistoryServiceImpl(ServiceHistoryRepository serviceHistoryRepository) {
        this.serviceHistoryRepository = serviceHistoryRepository;
    }

    // Create a service record
    @Override
    public ServiceHistory createServiceHistory(ServiceHistory serviceHistory){

        // Check if the service mileage is NOT more than the car's current mileage
        if(serviceHistory.getMileageAtService() > serviceHistory.getCar().getCurrentMileage()){
            throw new RuntimeException("Service mileage cannot be higher than Current Mileage");
        }

        // Check if 'Other' service is selected then make use of custom notes for it
        if(serviceHistory.getServiceType() == ServiceType.OTHER && (serviceHistory.getDescription() == null
                || serviceHistory.getDescription().trim().isEmpty()))
        {
            throw new RuntimeException("Description cannot be empty when service type is OTHER");
        }
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
    public ServiceHistory updateServiceHistory(Long id, ServiceHistory updatedServiceHistory){
        ServiceHistory existingServiceHistory = serviceHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record found with id: " + id));

        // Check if the service mileage is NOT more than the car's current mileage and NOT less than the last service
            if(updatedServiceHistory.getMileageAtService() > existingServiceHistory.getCar().getCurrentMileage()){
                throw new RuntimeException("Service mileage cannot be higher than Current Mileage");
            } else if(updatedServiceHistory.getMileageAtService() < existingServiceHistory.getMileageAtService()){
                throw new RuntimeException("New service mileage cannot be lower than the last latest service mileage");
            }

            // Check if 'Other' service is selected then make use of custom notes for it
            if(updatedServiceHistory.getServiceType() == ServiceType.OTHER && (updatedServiceHistory.getDescription() == null
                    || updatedServiceHistory.getDescription().trim().isEmpty()))
            {
                throw new RuntimeException("Description cannot be empty when service type is OTHER");
            }
            existingServiceHistory.setServiceDate(updatedServiceHistory.getServiceDate());
            existingServiceHistory.setDescription(updatedServiceHistory.getDescription());
            existingServiceHistory.setMileageAtService(updatedServiceHistory.getMileageAtService());
            existingServiceHistory.setServiceType(updatedServiceHistory.getServiceType());
            existingServiceHistory.setCost(updatedServiceHistory.getCost());
        return serviceHistoryRepository.save(existingServiceHistory);
    }

    // Filter different Service categories
    @Override
    public List<ServiceHistory> getServiceHistoryByCategory(ServiceCategory serviceCategory){
        return serviceHistoryRepository.findByServiceCategory(serviceCategory);
    }

    // Filter different Service types
    @Override
    public List<ServiceHistory> getServiceHistoryByServiceType(ServiceType serviceType){
        return serviceHistoryRepository.findByServiceType(serviceType);
    }

    // Filter different Services by dates
    @Override
    public List<ServiceHistory> getServiceHistoryByCarAndDate(Car car, LocalDate serviceDate){
        return serviceHistoryRepository.findByCarAndServiceDate(car, serviceDate);
    }

    // Filter all Service records for a car
    @Override
    public List<ServiceHistory> getServiceHistoryByCar(Car car){
        return serviceHistoryRepository.findByCar(car);
    }

}
