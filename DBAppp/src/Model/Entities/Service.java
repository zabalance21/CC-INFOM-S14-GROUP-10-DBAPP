package Model.Entities;

import java.math.BigDecimal;

public class Service {
    private String serviceId;
    private String name;
    private String description;
    private BigDecimal rate;
    private String availability; // "Available" | "Unavailable" | "Discontinued"

    // Constructor for creating a new Service (before ID is assigned)
    public Service(String name, String description, BigDecimal rate) {
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.availability = "Available";
    }

    // Constructor for reading from DB
    public Service(String serviceId, String name, String description, BigDecimal rate, String availability) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.availability = availability;
    }

    // Getters
    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getRate() { return rate; }
    public String getAvailability() { return availability; }

    // Setters
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public String toString() {
        return String.format(
                "Service ID: %s | Name: %s | Rate: %s | Availability: %s | Desc: %s%n",
                serviceId, name, rate, availability, description
        );
    }
}
