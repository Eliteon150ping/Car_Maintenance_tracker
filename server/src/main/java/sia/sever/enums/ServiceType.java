package sia.sever.enums;

public enum ServiceType {

    // Types of service maintenance for a car will be here...

    // Engine
    ENGINE_OIL_AND_FILTER(10000,6,ServiceCategory.ENGINE),
    AIR_FILTER(15000,12,ServiceCategory.ENGINE),
    SPARK_PLUGS(80000,0,ServiceCategory.ENGINE),
    SERPENTINE_BELT(80000,0,ServiceCategory.ENGINE),
    TIMING_BELT(90000,5,ServiceCategory.ENGINE),

    // Cooling
    COOLANT_FLUSH(50000,2,ServiceCategory.COOLING),

    // Electrical
    BATTERY(0,48,ServiceCategory.ELECTRICAL),

    // Drivetrain
    DIFFERENTIAL_OIL(50000,0,ServiceCategory.DRIVETRAIN),
    TRANSMISSION_FLUID(60000,0,ServiceCategory.DRIVETRAIN),

    // Wheels and Suspension
    TYRE_ROTATION(10000,0,ServiceCategory.Wheels_And_Suspension),
    WHEEL_ALIGNMENT(15000,12,ServiceCategory.Wheels_And_Suspension),

    // Braking
    BRAKE_PADS(30000,0,ServiceCategory.BRAKING),

    // Fuel Delivery
    FUEL_FILTER(30000,0,ServiceCategory.FUEL_DELIVERY),
    FUEL_INJECTOR_CLEANING(30000,0,ServiceCategory.FUEL_DELIVERY),

    // Other
    OTHER(0,0,ServiceCategory.OTHER);

    // Enum fields
    private final ServiceCategory serviceCategory;
    private final int intervalKm;
    private final int intervalMonths;

    // Constructor
    ServiceType(int intervalKm, int intervalMonths, ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
        this.intervalKm = intervalKm;
        this.intervalMonths = intervalMonths;
    }

    // Getters
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
