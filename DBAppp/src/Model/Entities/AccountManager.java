package Model.Entities;

import Model.DAO.AccountManagerDAO;

public class AccountManager {
    private String managerID;
    private String name;
    private String contactInfo;
    private String branchID;
    private ManagerStatus status;


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
        AccountManagerDAO accountManagerDAO = new AccountManagerDAO();
        this.managerID = accountManagerDAO.getNextAvailableManagerId();

        if(this.managerID == null){
            throw new IllegalStateException("Failed to generate manager ID.");
        }
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