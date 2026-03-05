package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.repository.ServiceHistoryRepository;
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
        return serviceHistoryRepository.save(serviceHistory);
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
            if(updatedServiceHistory.getMileageAtService() > existingServiceHistory.getCar().getCurrentMileage()){
                throw new RuntimeException("Service mileage cannot be higher than Current Mileage");
            } else if(updatedServiceHistory.getMileageAtService() < existingServiceHistory.getMileageAtService()){
                throw new RuntimeException("New service mileage cannot be lower than the last latest Mileage");
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
    public List<ServiceHistory> getFilteredServiceHistory(){}


}
