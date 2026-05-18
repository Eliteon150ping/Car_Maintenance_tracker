package sia.sever.enums;

public enum ServiceType {

    // Types of service maintenance for a car will be here...

    // Engine
    ENGINE_OIL_AND_FILTER("Engine Oil and Filter",10000,6,ServiceCategory.ENGINE),
    AIR_FILTER("Air Filter",15000,12,ServiceCategory.ENGINE),
    SPARK_PLUGS("Spark Plugs",80000,0,ServiceCategory.ENGINE),
    SERPENTINE_BELT("Serpentine Belt",80000,0,ServiceCategory.ENGINE),
    TIMING_BELT("Timing Belt",90000,5,ServiceCategory.ENGINE),

    // Cooling
    COOLANT_FLUSH("Coolant Flush",50000,2,ServiceCategory.COOLING),

    // Electrical
    BATTERY("Battery",0,48,ServiceCategory.ELECTRICAL),

    // Drivetrain
    DIFFERENTIAL_OIL("Differential Oil",50000,0,ServiceCategory.DRIVETRAIN),
    TRANSMISSION_FLUID("Transmission Fluid",60000,0,ServiceCategory.DRIVETRAIN),

    // Wheels and Suspension
    TYRE_ROTATION("Tyre Rotation",10000,0,ServiceCategory.Wheels_And_Suspension),
    WHEEL_ALIGNMENT("Wheel Alignment",15000,12,ServiceCategory.Wheels_And_Suspension),

    // Braking
    BRAKE_PADS("Brake Pads",30000,0,ServiceCategory.BRAKING),

    // Fuel Delivery
    FUEL_FILTER("Fuel Filter",30000,0,ServiceCategory.FUEL_DELIVERY),
    FUEL_INJECTOR_CLEANING("Fuel Injector Cleaning",30000,0,ServiceCategory.FUEL_DELIVERY),

    // Other
    OTHER("Other",0,0,ServiceCategory.OTHER);

    // Enum fields
    private final String displayName;
    private final ServiceCategory serviceCategory;
    private final int intervalKm;
    private final int intervalMonths;

    // Constructor
    ServiceType(String displayName, int intervalKm, int intervalMonths, ServiceCategory serviceCategory) {
        this.displayName = displayName;
        this.serviceCategory = serviceCategory;
        this.intervalKm = intervalKm;
        this.intervalMonths = intervalMonths;
    }

    // Getters
    public String getDisplayName(){
        return displayName;
    }

    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public int getIntervalKm() {
        return intervalKm;
    }

    public int getIntervalMonths() {
        return intervalMonths;
    }
}
