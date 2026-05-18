package sia.sever.enums;

public enum ServiceCategory {

    ENGINE("Engine"),
    COOLING("Cooling"),
    ELECTRICAL("Electrical"),
    DRIVETRAIN("Drivetrain"),
    Wheels_And_Suspension("Wheels and Suspension"),
    BRAKING("Braking"),
    FUEL_DELIVERY("Fuel Delivery"),
    OTHER("Other");

    // Fields
    private final String displayName;

    // Constructor
    ServiceCategory(String displayName) {
        this.displayName = displayName;
    }

    // Getters
    public String getDisplayName(){
        return displayName;
    }
}
