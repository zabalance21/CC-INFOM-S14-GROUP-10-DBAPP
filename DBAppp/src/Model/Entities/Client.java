package Model.Entities;

import java.util.ArrayList;
import java.util.Random;

public class Client {
    private String clientId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private static final Random random = new Random();
    private static int counter = 3;
    private static final int MAX_CLIENTS = 1000;
    private final ArrayList<String> generatedIDs = new ArrayList<>();

    public Client(String name, String email, String phone, String address) {
        setClientId();
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    // For reading from DB (use existing ID)
    public Client(String clientId, String name, String email, String phone, String address) {
        this.clientId = clientId;
        setName(name);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
    }

    public void setClientId() {
        if(counter > MAX_CLIENTS){
            throw new IllegalStateException("All possible client IDs have been used.");
        }
        this.clientId = String.format("CL-%03d", counter);
        counter++;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name format. Name cannot be null or empty.");
        }

        if (!name.matches("^[A-Za-z\\s'-]+$")) {
            throw new IllegalArgumentException("Invalid name format.");
        }
        this.name =name;
    }

    public void setPhone(String phone) {
        if(phone == null || !phone.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone format. Phone number must be 11 digits and starts with 09.");
        }
        this.phone = phone;
    }

    public void setAddress(String address){
        if(address == null || address.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid address format. Address cannot be null or empty.");
        }
        if(address.length() > 28){
            throw new IllegalArgumentException("Invalid address length. Up to 28 characters only.");
        }
        if (!address.matches("^[A-Za-z0-9\\s,.-]+$")) {
            throw new IllegalArgumentException("Address contains invalid characters");
        }
        this.address = address;
    }

    public String getClientId() {
        return this.clientId;
    }
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getAddress() {
        return this.address;
    }
    public String toString() {
        return String.format("Client ID: %s | Client Name: %s | Email: %s | Phone: %s | Address: %s\n", this.clientId, this.name, this.email, this.phone, this.address);
    }
}
