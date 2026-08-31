package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sia.sever.dto.car.CarSummaryDTO;
import sia.sever.dto.car.UpdateCarDTO;
import sia.sever.dto.serviceRecord.CreateServiceRecordDTO;
import sia.sever.dto.serviceRecord.ServiceRecordResponseDTO;
import sia.sever.dto.serviceRecord.UpdateServiceRecordDTO;
import sia.sever.entity.Car;
import sia.sever.entity.ServiceHistory;
import sia.sever.entity.User;
import sia.sever.enums.ServiceCategory;
import sia.sever.enums.ServiceType;
import sia.sever.exception.*;
import sia.sever.repository.CarRepository;
import sia.sever.repository.ServiceHistoryRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sia.sever.repository.UserRepository;

@Service
public class ServiceHistoryServiceImpl implements ServiceHistoryService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final ServiceHistoryRepository serviceHistoryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    private static final int UPCOMING_DAYS_THRESHOLD = 40;
    private static final int UPCOMING_KM_THRESHOLD = 1500;

    @Autowired
    public ServiceHistoryServiceImpl(ServiceHistoryRepository serviceHistoryRepository,
                                     CarRepository carRepository, UserRepository userRepository) {
        this.serviceHistoryRepository = serviceHistoryRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    // Mapper for DTO and service to return a service record object for a car to the frontend
    private ServiceRecordResponseDTO mapToServiceRecordResponseDTO(ServiceHistory serviceHistory) {

        Integer remainingKm = null;
        Integer remainingDays = null;

        // Validate If 'OTHER' is selected then do not return values for remaining km/days
        if (serviceHistory.getServiceType() != ServiceType.OTHER) {

            remainingKm = calculateRemainingKm(serviceHistory);
            remainingDays = calculateRemainingDays(serviceHistory);
        }

        Car extractCarInfo = serviceHistory.getCar();
        CarSummaryDTO car = new CarSummaryDTO(extractCarInfo.getId(), extractCarInfo.getBrand(), extractCarInfo.getModel(),
                extractCarInfo.getYear(), extractCarInfo.getCurrentMileage());

        return new ServiceRecordResponseDTO(serviceHistory.getId(), serviceHistory.getServiceDate(),
                serviceHistory.getMileageAtService(),
                serviceHistory.getNextDueMileage(), serviceHistory.getNextDueDate()
                , serviceHistory.getServiceType(), serviceHistory.getCost(),
                serviceHistory.getDescription(), remainingKm, remainingDays, car);
    }

    // Mapper to convert ServiceRecordRequestDTO into an entity
    private ServiceHistory mapToEntity(CreateServiceRecordDTO createServiceRecordDTO) {
        ServiceHistory serviceRecord = new ServiceHistory();
        serviceRecord.setServiceDate(createServiceRecordDTO.getServiceDate());
        serviceRecord.setMileageAtService(createServiceRecordDTO.getMileageAtService());
        serviceRecord.setServiceType(createServiceRecordDTO.getServiceType());
        serviceRecord.setCost(createServiceRecordDTO.getCost());
        serviceRecord.setDescription(createServiceRecordDTO.getDescription());
        serviceRecord.setServiceDate(createServiceRecordDTO.getServiceDate());
        return serviceRecord;
    }

    // Mapper to apply update DTO changes to an existing managed entity
    private void updateEntityFromDTO(UpdateServiceRecordDTO updateServiceRecordDTO, ServiceHistory existingServiceRecord){

        if(updateServiceRecordDTO.getDescription() != null){
            existingServiceRecord.setDescription(updateServiceRecordDTO.getDescription());
        }
        if(updateServiceRecordDTO.getCost() != null){
            existingServiceRecord.setCost(updateServiceRecordDTO.getCost());
        }
    }


    // Create a service record
    @Override
    public ServiceRecordResponseDTO createServiceHistory(CreateServiceRecordDTO serviceHistory, Long carId) {

        ServiceHistory convertToEntity = mapToEntity(serviceHistory);

        // First retrieve an existing car through its id
        User user = getAuthenticatedUser();
        Car car = getUserCar(carId, user);
        convertToEntity.setCar(car);
        ServiceHistory lastLatestServiceMileage = serviceHistoryRepository.findFirstByCarOrderByMileageAtServiceDesc(car);
        ServiceHistory lastLatestServiceDate = serviceHistoryRepository.findFirstByCarOrderByServiceDateDesc(car);

        // Check if the service mileage is NOT more than the car's current mileage and NOT less than the last service
        validateMileage(convertToEntity);
        if (lastLatestServiceMileage != null) {
            if ((convertToEntity.getMileageAtService() < lastLatestServiceMileage.getMileageAtService())) {
                throw new InvalidMileageException("New service mileage cannot be lower than the last latest service mileage");
            }
        }
        // Check if the service date is NOT before the car's year model
        if (convertToEntity.getServiceDate().getYear() < car.getYear()) {
            throw new InvalidDateException("Service Date cannot be before the car's year model: " + car.getYear());
        }

        // Check if the service date is NOT before the service's last service date
        if (lastLatestServiceDate != null) {
            if (convertToEntity.getServiceDate().isBefore(lastLatestServiceDate.getServiceDate())) {
                throw new InvalidDateException("New service date cannot be before the " + lastLatestServiceDate.getServiceDate());
            }
        }

        // Prevent duplicate service records of the same type on the same date with the same mileage at service
        boolean isExistingRecord = serviceHistoryRepository.existsByCarAndServiceTypeAndServiceDateAndMileageAtService(car,
                serviceHistory.getServiceType(), serviceHistory.getServiceDate(), serviceHistory.getMileageAtService());
        if (isExistingRecord) {
            throw new InvalidClassException("Cannot add duplicate service record for the same service type with same mileage and date");
        }

        // Check if 'Other' service is selected then make use of custom notes for it
        validateOtherServiceDescription(serviceHistory.getServiceType(), serviceHistory.getDescription());

        // Check if user did a service, give the next change interval/date
        convertToEntity.setNextDueMileage(calculateNextServiceMileage(convertToEntity));
        convertToEntity.setNextDueDate(calculateNextServiceDate(convertToEntity));

        ServiceHistory savedServiceHistory = serviceHistoryRepository.save(convertToEntity);
        return mapToServiceRecordResponseDTO(savedServiceHistory);
    }

    // Get all service records
    @Override
    public List<ServiceRecordResponseDTO> getAllServiceRecords() {
        User user = getAuthenticatedUser();
        List<ServiceHistory> findAllRecords = serviceHistoryRepository.findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user);
        return findAllRecords.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Find the service record by id
    @Override
    public ServiceRecordResponseDTO getServiceHistoryById(Long id) {
        User user = getAuthenticatedUser();
        ServiceHistory findById = serviceHistoryRepository.findByIdAndCarUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("No record found with id: " + id));
        return mapToServiceRecordResponseDTO(findById);
    }

    // Update the service record
    @Override
    public ServiceRecordResponseDTO updateServiceHistory(Long id, UpdateServiceRecordDTO updatedServiceHistory, Long carId) {

        User user = getAuthenticatedUser();
        Car car = getUserCar(carId, user);
        ServiceHistory existingServiceHistory = serviceHistoryRepository.findByIdAndCar(id, car)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found for this car"));

        // Check if 'Other' service is selected then make use of custom notes for it
        validateOtherServiceDescription(existingServiceHistory.getServiceType(), updatedServiceHistory.getDescription());

        updateEntityFromDTO(updatedServiceHistory, existingServiceHistory);

        ServiceHistory newUpdatedServiceHistory = serviceHistoryRepository.save(existingServiceHistory);
        return mapToServiceRecordResponseDTO(newUpdatedServiceHistory);
    }

    // Filter upcoming services by remaining km or days
    @Override
    public List<ServiceRecordResponseDTO> getUpcomingServiceRecords() {
        User user = getAuthenticatedUser();
        List<ServiceHistory> getAllUpcomingServiceRecords = serviceHistoryRepository.findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user);
        Collection<ServiceHistory> latestRecords = getAllUpcomingServiceRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> Arrays.asList(record.getCar().getId(), record.getServiceType()),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(ServiceHistory::getServiceDate)
                                        .thenComparing(ServiceHistory::getMileageAtService)),
                                Optional::get
                        )
                )).values();

        return latestRecords.stream()
                .filter(serviceHistory -> {

                    if (serviceHistory.getServiceType() == ServiceType.OTHER) {
                        return false;
                    }

                    int remainingKm = calculateRemainingKm(serviceHistory);
                    int remainingDays = calculateRemainingDays(serviceHistory);

                    return (remainingKm > 0 && remainingKm <= UPCOMING_KM_THRESHOLD) ||
                            (remainingDays > 0 && remainingDays <= UPCOMING_DAYS_THRESHOLD);
                })
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter overdue services by remaining km or days
    @Override
    public List<ServiceRecordResponseDTO> getOverDueServiceRecords() {
        User user = getAuthenticatedUser();
        List<ServiceHistory> getAllOverdueServiceRecords = serviceHistoryRepository.findAllByCarUserOrderByServiceDateDescMileageAtServiceDesc(user);
        Collection<ServiceHistory> latestRecords = getAllOverdueServiceRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> Arrays.asList(record.getCar().getId(), record.getServiceType()),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(ServiceHistory::getServiceDate)
                                        .thenComparing(ServiceHistory::getMileageAtService)),
                                Optional::get
                        )
                )).values();

        return latestRecords.stream()
                .filter(serviceHistory -> {

                    if (serviceHistory.getServiceType() == ServiceType.OTHER) {
                        return false;
                    }

                    int remainingKm = calculateRemainingKm(serviceHistory);
                    int remainingDays = calculateRemainingDays(serviceHistory);

                    return (remainingKm < 0) || (remainingDays < 0);
                })
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter different Service categories
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory) {
        User user = getAuthenticatedUser();
        List<ServiceType> filterServiceCategories = filterServiceTypesByCategory(serviceCategory);

        List<ServiceHistory> filteredServiceCategories = serviceHistoryRepository.findByCarUserAndServiceTypeIn(user, filterServiceCategories);
        return filteredServiceCategories.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter different Service types
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType) {
        User user = getAuthenticatedUser();
        List<ServiceHistory> findByServiceType = serviceHistoryRepository.findByCarUserAndServiceType(user, serviceType);
        return findByServiceType.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter different Service categories for a car
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory(Long carId, ServiceCategory serviceCategory) {
        User user = getAuthenticatedUser();
        List<ServiceType> filterServiceCategories = filterServiceTypesByCategory(serviceCategory);

        List<ServiceHistory> filteredServiceCategories = serviceHistoryRepository.findByCarAndServiceTypeIn(getUserCar(carId, user), filterServiceCategories);
        return filteredServiceCategories.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter different Service types for a car
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType(Long carId, ServiceType serviceType) {
        User user = getAuthenticatedUser();
        List<ServiceHistory> findByServiceType = serviceHistoryRepository.findByCarAndServiceType(getUserCar(carId, user), serviceType);
        return findByServiceType.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter different Services by dates
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate) {
        User user = getAuthenticatedUser();
        List<ServiceHistory> findByCarAndServiceDate = serviceHistoryRepository.findByCarAndServiceDate(getUserCar(carId, user), serviceDate);
        return findByCarAndServiceDate.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }

    // Filter all Service records for a car
    @Override
    public List<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId) {
        User user = getAuthenticatedUser();
        List<ServiceHistory> findByCar = serviceHistoryRepository.findByCarOrderByServiceDateDescMileageAtServiceDesc(getUserCar(carId, user));
        return findByCar.stream()
                .map(this::mapToServiceRecordResponseDTO)
                .collect(Collectors.toList());
    }


    // Pagination versions(keep original also for best of both worlds/for frontend logic)

    // Filter different Services by dates
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndDate(Long carId, LocalDate serviceDate, int page, int size) {
        User user = getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> findByCarAndServiceDate = serviceHistoryRepository.findByCarAndServiceDate(getUserCar(carId, user), serviceDate, pageable);
        return findByCarAndServiceDate.map(this::mapToServiceRecordResponseDTO);
    }

    // Filter different Service types
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByServiceType(ServiceType serviceType, int page, int size) {
        User user = getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> findByServiceType = serviceHistoryRepository.findByCarUserAndServiceType(user, serviceType, pageable);
        return findByServiceType.map(this::mapToServiceRecordResponseDTO);
    }

    // Filter different Service categories
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByCategory(ServiceCategory serviceCategory, int page, int size) {
        User user = getAuthenticatedUser();
        List<ServiceType> filterServiceCategories = filterServiceTypesByCategory(serviceCategory);
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> filteredServiceCategories = serviceHistoryRepository.findByCarUserAndServiceTypeIn(user, filterServiceCategories, pageable);
        return filteredServiceCategories.map(this::mapToServiceRecordResponseDTO);
    }

    // Filter all Service records for a car
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByCar(Long carId, int page, int size) {
        User user = getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> findByCar = serviceHistoryRepository.findByCar(getUserCar(carId, user), pageable);
        return findByCar.map(this::mapToServiceRecordResponseDTO);

    }

    // Filter different Service categories for a car
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndCategory(Long carId, ServiceCategory serviceCategory, int page, int size) {
        User user = getAuthenticatedUser();
        List<ServiceType> filterServiceCategories = filterServiceTypesByCategory(serviceCategory);
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> filteredServiceCategories = serviceHistoryRepository.findByCarAndServiceTypeIn(getUserCar(carId, user), filterServiceCategories, pageable);
        return filteredServiceCategories.map(this::mapToServiceRecordResponseDTO);
    }

    // Filter different Service types for a car
    @Override
    public Page<ServiceRecordResponseDTO> getServiceHistoryByCarAndServiceType(Long carId, ServiceType serviceType, int page, int size) {
        User user = getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceHistory> findByServiceType = serviceHistoryRepository.findByCarAndServiceType(getUserCar(carId, user), serviceType, pageable);
        return findByServiceType.map(this::mapToServiceRecordResponseDTO);
    }

    // Methods to help reduce duplicate code:

    // Check if the service mileage is NOT more than the car's current mileage
    private void validateMileage(ServiceHistory serviceHistory) {
        if (serviceHistory.getMileageAtService() > serviceHistory.getCar().getCurrentMileage()) {
            throw new InvalidMileageException("Service mileage cannot be higher than Current Mileage");
        }
    }

    // Check if 'Other' service is selected then make use of custom notes for it
    private void validateOtherServiceDescription(ServiceType serviceType, String description) {
        if (serviceType == ServiceType.OTHER && (description == null || description.trim().isEmpty())) {
            throw new InvalidClassException("Description cannot be empty when service type is OTHER");
        }
    }

    // Check if user did a service, give the next change interval
    private int calculateNextServiceMileage(ServiceHistory serviceHistory) {
        return serviceHistory.getMileageAtService() + serviceHistory.getServiceType().getIntervalKm();
    }

    // Check if user did a service, give the next change date
    private LocalDate calculateNextServiceDate(ServiceHistory serviceHistory) {
        return serviceHistory.getServiceDate().plusMonths(serviceHistory.getServiceType().getIntervalMonths());
    }

    // Check if user did not do a service, give the km remaining
    private int calculateRemainingKm(ServiceHistory serviceHistory) {
        return serviceHistory.getNextDueMileage() - serviceHistory.getCar().getCurrentMileage();
    }

    // Check if user did not do a service, give the days remaining
    private int calculateRemainingDays(ServiceHistory serviceHistory) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), serviceHistory.getNextDueDate());
    }

    // Get authenticated user helper method
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User not authorized");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    // Get user's car helper method
    private Car getUserCar(Long id, User user) {
        return carRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + id));
    }

    // Filter service types into service categories
    private List<ServiceType> filterServiceTypesByCategory(ServiceCategory serviceCategory) {
        List<ServiceType> matchingServiceTypes = new ArrayList<>();
        for (ServiceType serviceTypes : ServiceType.values()) {
            if (serviceTypes.getServiceCategory() == serviceCategory) {
                matchingServiceTypes.add(serviceTypes);
            }
        }
        return matchingServiceTypes;
    }
}
