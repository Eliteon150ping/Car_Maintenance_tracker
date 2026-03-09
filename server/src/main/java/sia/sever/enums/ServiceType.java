package sia.sever.enums;

public enum ServiceType {

    // Types of service maintenance for a car will be here...

    // Engine
    ENGINE_OIL_AND_FILTER(10000,ServiceCategory.ENGINE),
    AIR_FILTER(15000,ServiceCategory.ENGINE),
    SPARK_PLUGS(80000,ServiceCategory.ENGINE),
    SERPENTINE_BELT(80000,ServiceCategory.ENGINE),
    TIMING_BELT(90000,ServiceCategory.ENGINE),

    // Cooling
    COOLANT_FLUSH(50000,ServiceCategory.COOLING),

    // Electrical
    BATTERY(4,ServiceCategory.ELECTRICAL),

    // Drivetrain
    DIFFERENTIAL_OIL(50000,ServiceCategory.DRIVETRAIN),
    TRANSMISSION_FLUID(60000,ServiceCategory.DRIVETRAIN),

    // Wheels and Suspension
    TYRE_ROTATION(10000 ,ServiceCategory.Wheels_And_Suspension),
    WHEEL_ALIGNMENT(15000,ServiceCategory.Wheels_And_Suspension),

    // Braking
    BRAKE_PADS(30000,ServiceCategory.BRAKING),

    // Fuel Delivery
    FUEL_FILTER(30000,ServiceCategory.FUEL_DELIVERY),
    FUEL_INJECTOR_CLEANING(30000,ServiceCategory.FUEL_DELIVERY),

    // Other
    OTHER(0,ServiceCategory.OTHER);

    private final ServiceCategory serviceCategory;
    private final int intervalKm;

    ServiceType(int intervalKm, ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
        this.intervalKm = intervalKm;
    }

    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public int getIntervalKm() {
        return intervalKm;
    }
}
