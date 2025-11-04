package Model.Entities;
import java.math.*;

public class Service {
    private String serviceId;
    private String name;
    private String description;
    private BigDecimal rate;
    private String category;
    private String availability; // "Available" | "Unavailable" | "Discontinued"

    public Service(String name, String description, BigDecimal rate, String category) {
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.category = category;
        this.availability = "Available"; // DB default; safe local default
    }

    public Service(String serviceId, String name, String description, BigDecimal rate, String category) {
        this(serviceId, name, description, rate, category, "Available");
    }

    public Service(String serviceId, String name, String description, BigDecimal rate, String category, String availability) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.category = category;
        this.availability = availability;
    }

    public String getServiceId() { return serviceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getRate() { return rate; }
    public String getCategory() { return category; }
    public String getAvailability() { return availability; }

    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailability(String availability) { this.availability = availability; }
    public String toString() {
        return String.format(
                "Service ID: %s | Name: %s | Rate: %s | Category: %s | Availability: %s | Desc: %s%n", serviceId, name, rate, category, availability, description
        );
    }
}