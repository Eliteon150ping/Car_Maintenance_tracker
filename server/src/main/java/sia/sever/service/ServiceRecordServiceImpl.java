package sia.sever.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sia.sever.entity.ServiceRecord;
import sia.sever.repository.ServiceRecordRepository;
import java.util.List;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    // We use the methods for the repository interface here to connect the database and service
    // through the repository since it acts as a bridge for deciding which methods to call from the
    // data given by the controller
    private final ServiceRecordRepository serviceRecordRepository;

    @Autowired
    public ServiceRecordServiceImpl(ServiceRecordRepository serviceRecordRepository) {
        this.serviceRecordRepository = serviceRecordRepository;
    }

    // Create a service record
    @Override
    public ServiceRecord createServiceRecord(ServiceRecord serviceRecord){
        return serviceRecordRepository.save(serviceRecord);
    }
    // Find the service record by id
    @Override
    public ServiceRecord getServiceRecordById(Long id){
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record found with id: " + id));
    }

    // Update the service record
    @Override
    public ServiceRecord updateServiceRecord(Long id, ServiceRecord updatedServiceRecord){
        ServiceRecord existingServiceRecord = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No record found with id: " + id));
        existingServiceRecord.setServiceDate(updatedServiceRecord.getServiceDate());
        existingServiceRecord.setEngineOilAndFilter(updatedServiceRecord.getEngineOilAndFilter());
        existingServiceRecord.setTyreRotation(updatedServiceRecord.getTyreRotation());
        existingServiceRecord.setAirFilter(updatedServiceRecord.getAirFilter());
        existingServiceRecord.setBrakePads(updatedServiceRecord.getBrakePads());
        existingServiceRecord.setWheelAlignment(updatedServiceRecord.getWheelAlignment());
        existingServiceRecord.setCoolantFlush(updatedServiceRecord.getCoolantFlush());
        existingServiceRecord.setSparkPlugs(updatedServiceRecord.getSparkPlugs());
        existingServiceRecord.setSerpentineBelt(updatedServiceRecord.getSerpentineBelt());
        existingServiceRecord.setTimingBelt(updatedServiceRecord.getTimingBelt());
        existingServiceRecord.setBattery(updatedServiceRecord.getBattery());
        existingServiceRecord.setFuelFilter(updatedServiceRecord.getFuelFilter());
        existingServiceRecord.setDifferentialOil(updatedServiceRecord.getDifferentialOil());
        return serviceRecordRepository.save(existingServiceRecord);
    }

    // Filter different Service categories
    @Override
    public List<ServiceRecord> getFilteredServiceRecords(Integer engineOilAndFilter, Integer tyreRotation,
                                                         Integer airFilter, Integer brakePads,
                                                         Integer wheelAlignment, Integer coolantFlush,
                                                         Integer sparkPlugs, Integer serpentineBelt,
                                                         Integer timingBelt, Integer battery,
                                                         Integer fuelFilter, Integer differentialOil)
    {
        if(engineOilAndFilter != null && tyreRotation != null && airFilter != null && brakePads != null
           && wheelAlignment != null && coolantFlush != null && sparkPlugs != null && serpentineBelt != null
        && timingBelt != null && battery != null && fuelFilter != null && differentialOil != null){

            return serviceRecordRepository
                    .getAllServiceRecords(engineOilAndFilter, tyreRotation, airFilter, brakePads,
                                          wheelAlignment, coolantFlush, sparkPlugs, serpentineBelt,
                                          timingBelt, battery, fuelFilter, differentialOil);

        } else if( engineOilAndFilter != null && airFilter != null && sparkPlugs != null &&
                   serpentineBelt !=null && timingBelt != null && fuelFilter != null &&
                   differentialOil !=null)
        {
            return serviceRecordRepository
                    .findByEngineAndPowertrain(engineOilAndFilter,airFilter,sparkPlugs, serpentineBelt,
                                               timingBelt, fuelFilter, differentialOil);

        } else if (wheelAlignment != null){
            return serviceRecordRepository.findBySuspension(wheelAlignment);
        }else if(brakePads != null){
            return serviceRecordRepository.findByBrakingSystem(brakePads);
        }else if(tyreRotation != null){
            return serviceRecordRepository.findByWheelsAndTyres(tyreRotation);
        }else if(coolantFlush != null){
            return serviceRecordRepository.findByCooling(coolantFlush);
        }else if(battery != null){
            return serviceRecordRepository.findByElectrical(battery);
        } else {
            return serviceRecordRepository.findAll();
        }
    }

}
