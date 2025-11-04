package Model.Entities;

public enum ServiceAvailability {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    DISCONTINUED("Discontinued");

    private final String dbValue;
    ServiceAvailability(String dbValue) { this.dbValue = dbValue; }
    public String db() { return dbValue; }
}