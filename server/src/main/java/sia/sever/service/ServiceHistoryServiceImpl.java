package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
//        existingServiceHistory.setServiceDate(updatedServiceHistory.getServiceDate());
//        existingServiceHistory.setEngineOilAndFilter(updatedServiceHistory.getEngineOilAndFilter());
//        existingServiceHistory.setTyreRotation(updatedServiceHistory.getTyreRotation());
//        existingServiceHistory.setAirFilter(updatedServiceHistory.getAirFilter());
//        existingServiceHistory.setBrakePads(updatedServiceHistory.getBrakePads());
//        existingServiceHistory.setWheelAlignment(updatedServiceHistory.getWheelAlignment());
//        existingServiceHistory.setCoolantFlush(updatedServiceHistory.getCoolantFlush());
//        existingServiceHistory.setSparkPlugs(updatedServiceHistory.getSparkPlugs());
//        existingServiceHistory.setSerpentineBelt(updatedServiceHistory.getSerpentineBelt());
//        existingServiceHistory.setTimingBelt(updatedServiceHistory.getTimingBelt());
//        existingServiceHistory.setBattery(updatedServiceHistory.getBattery());
//        existingServiceHistory.setFuelFilter(updatedServiceHistory.getFuelFilter());
//        existingServiceHistory.setDifferentialOil(updatedServiceHistory.getDifferentialOil());
        return serviceHistoryRepository.save(existingServiceHistory);
    }

    // Filter different Service categories
    @Override
    public List<ServiceHistory> getFilteredServiceHistory(Integer engineOilAndFilter, Integer tyreRotation,
                                                         Integer airFilter, Integer brakePads,
                                                         Integer wheelAlignment, Integer coolantFlush,
                                                         Integer sparkPlugs, Integer serpentineBelt,
                                                         Integer timingBelt, Integer battery,
                                                         Integer fuelFilter, Integer differentialOil)
    {
        if(engineOilAndFilter != null && tyreRotation != null && airFilter != null && brakePads != null
           && wheelAlignment != null && coolantFlush != null && sparkPlugs != null && serpentineBelt != null
        && timingBelt != null && battery != null && fuelFilter != null && differentialOil != null){

            return serviceHistoryRepository
                    .getAllServiceHistory(engineOilAndFilter, tyreRotation, airFilter, brakePads,
                                          wheelAlignment, coolantFlush, sparkPlugs, serpentineBelt,
                                          timingBelt, battery, fuelFilter, differentialOil);

        } else if( engineOilAndFilter != null && airFilter != null && sparkPlugs != null &&
                   serpentineBelt !=null && timingBelt != null && fuelFilter != null &&
                   differentialOil !=null)
        {
            return serviceHistoryRepository
                    .findByEngineAndPowertrain(engineOilAndFilter,airFilter,sparkPlugs, serpentineBelt,
                                               timingBelt, fuelFilter, differentialOil);

        } else if (wheelAlignment != null){
            return serviceHistoryRepository.findBySuspension(wheelAlignment);
        }else if(brakePads != null){
            return serviceHistoryRepository.findByBrakingSystem(brakePads);
        }else if(tyreRotation != null){
            return serviceHistoryRepository.findByWheelsAndTyres(tyreRotation);
        }else if(coolantFlush != null){
            return serviceHistoryRepository.findByCooling(coolantFlush);
        }else if(battery != null){
            return serviceHistoryRepository.findByElectrical(battery);
        } else {
            return serviceHistoryRepository.findAll();
        }
    }

}
