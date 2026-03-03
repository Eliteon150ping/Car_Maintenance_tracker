package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.ServiceRecord;
import java.util.List;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    /* Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

    // So if you want custom methods for filtering, make them here:
    List<ServiceRecord> getAllServiceRecords(int engineOilAndFilter, int tyreRotation,
                                             int airFilter, int brakePads,
                                             int wheelAlignment, int coolantFlush,
                                             int sparkPlugs, int serpentineBelt,
                                             int timingBelt, int battery,
                                             int fuelFilter, int differentialOil);

    List<ServiceRecord> findByEngineAndPowertrain(int engineOilAndFilter, int airFilter,
                                                  int sparkPlugs, int serpentineBelt,
                                                  int timingBelt, int fuelFilter,int differentialOil);

    List<ServiceRecord> findBySuspension(int wheelAlignment);
    List<ServiceRecord> findByBrakingSystem(int brakePads);
    List<ServiceRecord> findByWheelsAndTyres(int tyreRotation);
    List<ServiceRecord> findByCooling(int coolantFlush);
    List<ServiceRecord> findByElectrical(int battery);
}
