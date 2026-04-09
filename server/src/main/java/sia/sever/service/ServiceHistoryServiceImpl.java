package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.dto.car.CarSummaryDTO;
import sia.sever.dto.serviceRecord.ServiceRecordRequestDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.exception.InvalidClassException;
import sia.sever.exception.InvalidMileageException;
import sia.sever.exception.ResourceNotFoundException;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // Mapper for DTO and service to return a service record object for a car to the frontend
    public ServiceRecordResponseDTO mapToServiceRecordResponseDTO(ServiceHistory serviceHistory){

        int remainingKm = calculateRemainingKm(serviceHistory);
        int remainingDays = calculateRemainingDays(serviceHistory);

        Car extractCarInfo = serviceHistory.getCar();
        CarSummaryDTO car = new CarSummaryDTO(extractCarInfo.getBrand(), extractCarInfo.getModel(),
                                              extractCarInfo.getYear(), extractCarInfo.getCurrentMileage());

        return new ServiceRecordResponseDTO(serviceHistory.getId(), serviceHistory.getServiceDate(),
                                            serviceHistory.getMileageAtService(),
                                            serviceHistory.getNextDueMileage(), serviceHistory.getNextDueDate()
                                            , serviceHistory.getServiceType(), serviceHistory.getCost(),
                                            serviceHistory.getDescription(), remainingKm, remainingDays, car);
    }

    // Mapper to convert ServiceRecordRequestDTO into an entity
    public ServiceHistory mapToEntity(ServiceRecordRequestDTO serviceRecordRequestDTO){
        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(serviceRecordRequestDTO.getServiceDate());
        serviceRecord.setMileageAtService(serviceRecordRequestDTO.getMileageAtService());
        serviceRecord.setServiceType(serviceRecordRequestDTO.getServiceType());
        serviceRecord.setCost(serviceRecordRequestDTO.getCost());
        serviceRecord.setDescription(serviceRecordRequestDTO.getDescription());
        return serviceRecord;
    }


    // Create a service record
    @Override
    public ServiceRecordResponseDTO createServiceHistory(ServiceRecordRequestDTO serviceHistory, Long carId){

        ServiceHistory convertToEntity = mapToEntity(serviceHistory);

        // First retrieve an existing car through its id
        Car car = getCarOrThrow(carId);
        convertToEntity.setCar(car);
        ServiceHistory lastLatestServiceMileage = serviceHistoryRepository.findFirstByCarOrderByMileageAtServiceDesc(car);

        // Check if the service mileage is NOT more than the car's current mileage and NOT less than the last service
        validateMileage(convertToEntity);
        if(lastLatestServiceMileage != null) {
            if (convertToEntity.getMileageAtService() < lastLatestServiceMileage.getMileageAtService()) {
                throw new InvalidMileageException("New service mileage cannot be lower than the last latest service mileage");
            }
        }

        // Check if 'Other' service is selected then make use of custom notes for it
        validateOtherServiceDescription(serviceHistory);

        // Check if user did a service, give the next change interval/date
        convertToEntity.setNextDueMileage(calculateNextServiceMileage(convertToEntity));
        convertToEntity.setNextDueDate(calculateNextServiceDate(convertToEntity));

        ServiceHistory savedServiceHistory = serviceHistoryRepository.save(convertToEntity);
        return mapToServiceRecordResponseDTO(savedServiceHistory);
    }

    // Get all service records
    @Override
    public List<ServiceRecordResponseDTO> getAllServiceRecords(){
        List<ServiceHistory> findAllRecords = serviceHistoryRepository.findAll();
        return findAllRecords.stream()
                             .map(this::mapToServiceRecordResponseDTO)
                             .collect(Collectors.toList());
    }

    // Find the service record by id
    @Override
    public ServiceRecordResponseDTO getServiceHistoryById(Long id){
        ServiceHistory findById = serviceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found with id: " + id));
        return mapToServiceRecordResponseDTO(findById);
    }

    // Update the service record
    @Override
    public ServiceRecordResponseDTO updateServiceHistory(Long id, ServiceRecordRequestDTO updatedServiceHistory, Long carId){

        getCarOrThrow(carId);
        ServiceHistory existingServiceHistory = serviceHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No record found with id: " + id));

            // Check if 'Other' service is selected then make use of custom notes for it
            validateOtherServiceDescription(updatedServiceHistory);

            existingServiceHistory.setDescription(updatedServiceHistory.getDescription());
            existingServiceHistory.setCost(updatedServiceHistory.getCost());

            ServiceHistory newUpdatedServiceHistory = serviceHistoryRepository.save(existingServiceHistory);
            return mapToServiceRecordResponseDTO(newUpdatedServiceHistory);
    }

    // Filter different Service categories
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory){
        List<ServiceType> matchingServiceTypes = new ArrayList<>();
        for(ServiceType serviceTypes : ServiceType.values()){
            if(serviceTypes.getServiceCategory() == serviceCategory){
                matchingServiceTypes.add(serviceTypes);
            }
        }
        List<ServiceHistory> filterServiceCategories = serviceHistoryRepository.findByServiceTypeIn(matchingServiceTypes);
        return filterServiceCategories.stream()
                                      .map(this::mapToServiceRecordResponseDTO)
                                      .collect(Collectors.toList());
    }

    // Filter different Service types
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType){
        List<ServiceHistory> findByServiceType = serviceHistoryRepository.findByServiceType(serviceType);
        return findByServiceType.stream()
                                .map(this::mapToServiceRecordResponseDTO)
                                .collect(Collectors.toList());
    }

    // Filter different Services by dates
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate){
        List<ServiceHistory> findByCarAndServiceDate = serviceHistoryRepository.findByCarAndServiceDate(getCarOrThrow(carId), serviceDate);
        return findByCarAndServiceDate.stream()
                                      .map(this::mapToServiceRecordResponseDTO)
                                      .collect(Collectors.toList());
    }

    // Filter all Service records for a car
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId){
        List<ServiceHistory> findByCar = serviceHistoryRepository.findByCar(getCarOrThrow(carId));
        return findByCar.stream()
                        .map(this::mapToServiceRecordResponseDTO)
                        .collect(Collectors.toList());
    }

    // Methods to help reduce duplicate code:

    // Find a car's id before proceeding with anything else
    private Car getCarOrThrow(Long carId){
        return carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("No car found with id: " + carId));
    }

    // Check if the service mileage is NOT more than the car's current mileage
    private void validateMileage(ServiceHistory serviceHistory){
        if(serviceHistory.getMileageAtService() > serviceHistory.getCar().getCurrentMileage()){
            throw new InvalidMileageException("Service mileage cannot be higher than Current Mileage");
        }
    }

    // Check if 'Other' service is selected then make use of custom notes for it
    private void validateOtherServiceDescription(ServiceRecordRequestDTO serviceHistory){
        if(serviceHistory.getServiceType() == ServiceType.OTHER && (serviceHistory.getDescription() == null
                || serviceHistory.getDescription().trim().isEmpty()))
        {
            throw new InvalidClassException("Description cannot be empty when service type is OTHER");
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
        return serviceHistory.getNextDueMileage() - serviceHistory.getCar().getCurrentMileage();
    }

    // Check if user did not do a service, give the days remaining
    private int calculateRemainingDays(ServiceHistory serviceHistory){
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), serviceHistory.getNextDueDate());
    }
}
