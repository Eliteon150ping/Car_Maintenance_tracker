package sia.sever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sia.sever.entity.ServiceHistory;
import java.util.List;

public interface ServiceHistoryRepository extends JpaRepository<ServiceHistory, Long> {

    /* Methods like this are defined automatically by JPA:
       save()
       findById(Long id)
       findAll()
       deleteById(Long id)
       delete()
       existsById(Long id)                                                                              */

    // So if you want custom methods for filtering, make them here:
    List<ServiceHistory> getAllServiceHistory(int engineOilAndFilter, int tyreRotation,
                                             int airFilter, int brakePads,
                                             int wheelAlignment, int coolantFlush,
                                             int sparkPlugs, int serpentineBelt,
                                             int timingBelt, int battery,
                                             int fuelFilter, int differentialOil);

    List<ServiceHistory> findByEngineAndPowertrain(int engineOilAndFilter, int airFilter,
                                                  int sparkPlugs, int serpentineBelt,
                                                  int timingBelt, int fuelFilter,int differentialOil);

    List<ServiceHistory> findBySuspension(int wheelAlignment);
    List<ServiceHistory> findByBrakingSystem(int brakePads);
    List<ServiceHistory> findByWheelsAndTyres(int tyreRotation);
    List<ServiceHistory> findByCooling(int coolantFlush);
    List<ServiceHistory> findByElectrical(int battery);
}
