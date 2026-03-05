package sia.sever.enums;

public enum ServiceType {

    // Types of service maintenance for a car will be here...

    // Engine
    ENGINE_OIL_AND_FILTER(ServiceCategory.ENGINE),
    AIR_FILTER(ServiceCategory.ENGINE),
    SPARK_PLUGS(ServiceCategory.ENGINE),
    SERPENTINE_BELT(ServiceCategory.ENGINE),
    TIMING_BELT(ServiceCategory.ENGINE),

    // Cooling
    COOLANT_FLUSH(ServiceCategory.COOLING),

    // Electrical
    BATTERY(ServiceCategory.ELECTRICAL),

    // Drivetrain
    DIFFERENTIAL_OIL(ServiceCategory.DRIVETRAIN),

    // Wheels and Suspension
    TYRE_ROTATION(ServiceCategory.Wheels_And_Suspension),
    WHEEL_ALIGNMENT(ServiceCategory.Wheels_And_Suspension),

    // Braking
    BRAKE_PADS(ServiceCategory.BRAKING),

    // Fuel Delivery
    FUEL_FILTER(ServiceCategory.FUEL_DELIVERY),
    FUEL_INJECTOR_CLEANING(ServiceCategory.FUEL_DELIVERY);


    private ServiceCategory serviceCategory;
    ServiceType(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }
}
