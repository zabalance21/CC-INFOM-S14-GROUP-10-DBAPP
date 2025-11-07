package Model.Entities;

public class AccountManager {
    private String managerID;
    private String name;
    private String contactInfo;
    private String branchID;
    private ManagerStatus status;
    private static int counter = 3;
    private static final int MAX_MANAGERS = 1000;

    public AccountManager(String name, String contactInfo, String branchID) {
        setManagerID();
        setName(name);
        setContactInfo(contactInfo);
        this.branchID = branchID;
        this.status = ManagerStatus.ACTIVE;
    }
    public AccountManager(String managerID, String name,String contactInfo, String branchID) {
        this.managerID = managerID;
        setName(name);
        setContactInfo(contactInfo);
        this.branchID = branchID;
        this.status = ManagerStatus.ACTIVE;
    }

    public void setManagerID() {
        if(counter > MAX_MANAGERS) {
            throw new IllegalStateException("All possible manager IDs have been used.");
        }
        this.managerID = String.format("AM-%03d", counter);;
        counter = counter + 1;
    }

    public void setName(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid string format. String cannot be null or empty.");
        }

        if (!name.matches("^[A-Za-z\\s'-]+$")) {
            throw new IllegalArgumentException("Invalid string format.");
        }
        this.name = name;
    }

    public void setContactInfo(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.contactInfo = email;
    }

    public void setStatus(ManagerStatus status) {
        this.status = status;
    }

    public String getManagerID() {
        return this.managerID;
    }
    public String getName() {
        return this.name;
    }
    public String getContactInfo() {
        return this.contactInfo;
    }
    public String getBranchID() {
        return this.branchID;
    }
    public ManagerStatus getStatus() {
        return this.status;
    }
    public String toString(){
        return String.format("Manager ID: %s | Manager Name: %s | Mobile Contact Info: %s | Assigned Branch ID: %s | Status: %s\n", this.managerID, this.name, this.contactInfo, this.branchID, this.status);
    }
}