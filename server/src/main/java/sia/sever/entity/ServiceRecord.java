package sia.sever.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ServiceRecord")
public class ServiceRecord {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Date serviceDate;

    @Column(nullable = false)
    private int engineOilAndFilter;

    @Column(nullable = false)
    private int tyreRotation;

    @Column(nullable = false)
    private int airFilter;

    @Column(nullable = false)
    private int brakePads;

    @Column(nullable = false)
    private int wheelAlignment;

    @Column(nullable = false)
    private int coolantFlush;

    @Column(nullable = true)
    private int sparkPlugs;

    @Column(nullable = false)
    private int serpentineBelt;        // Same as fan belt

    @Column(nullable = true)
    private int timingBelt;

    @Column(nullable = false)
    private int battery;

    @Column(nullable = false)
    private int fuelFilter;

    @Column(nullable = true)
    private int differentialOil;

    // Constructor
    public ServiceRecord(){}
    public ServiceRecord(Date serviceDate, int engineOilAndFilter, int tyreRotation,
                         int airFilter, int brakePads, int wheelAlignment, int coolantFlush,
                         int sparkPlugs, int serpentineBelt, int timingBelt, int battery,
                         int fuelFilter, int differentialOil)
    {
        this.serviceDate = serviceDate;
        this.engineOilAndFilter = engineOilAndFilter;
        this.tyreRotation = tyreRotation;
        this.airFilter = airFilter;
        this.brakePads = brakePads;
        this.wheelAlignment = wheelAlignment;
        this.coolantFlush = coolantFlush;
        this.sparkPlugs = sparkPlugs;
        this.serpentineBelt = serpentineBelt;
        this.timingBelt = timingBelt;
        this.battery = battery;
        this.fuelFilter = fuelFilter;
        this.differentialOil = differentialOil;
    }

    // Getters
    public Long getId(){
        return id;
    }

    public Date getServiceDate(){
        return serviceDate;
    }

    public int getEngineOilAndFilter(){
        return engineOilAndFilter;
    }

    public int getTyreRotation(){
        return tyreRotation;
    }

    public int getAirFilter(){
        return airFilter;
    }

    public int getBrakePads(){
        return brakePads;
    }

    public int getWheelAlignment(){
        return wheelAlignment;
    }

    public int getCoolantFlush(){
        return coolantFlush;
    }

    public int getSparkPlugs(){
        return sparkPlugs;
    }

    public int getSerpentineBelt(){
        return serpentineBelt;
    }

    public int getTimingBelt(){
        return timingBelt;
    }

    public int getBattery(){
        return battery;
    }

    public int getFuelFilter(){
        return fuelFilter;
    }

    public int getDifferentialOil(){
        return differentialOil;
    }

    // Setters
    public void setServiceDate(Date serviceDate){
        this.serviceDate = serviceDate;
    }

    public void setEngineOilAndFilter(int engineOilAndFilter){
        this.engineOilAndFilter = engineOilAndFilter;
    }

    public void setTyreRotation(int tyreRotation){
        this.tyreRotation = tyreRotation;
    }

    public void setAirFilter(int airFilter){
        this.airFilter = airFilter;
    }

    public void setBrakePads(int brakePads){
        this.brakePads = brakePads;
    }

    public void setWheelAlignment(int wheelAlignment){
        this.wheelAlignment = wheelAlignment;
    }

    public void setCoolantFlush(int coolantFlush){
        this.coolantFlush = coolantFlush;
    }

    public void setSparkPlugs(int sparkPlugs){
        this.sparkPlugs = sparkPlugs;
    }

    public void setSerpentineBelt(int serpentineBelt){
        this.serpentineBelt = serpentineBelt;
    }

    public void setTimingBelt(int timingBelt){
        this.timingBelt = timingBelt;
    }

    public void setBattery(int battery){
        this.battery = battery;
    }

    public void setFuelFilter(int fuelFilter){
        this.fuelFilter = fuelFilter;
    }

    public void setDifferentialOil(int differentialOil){
        this.differentialOil = differentialOil;
    }

}
