package Model.Entities;

import Model.DAO.BranchDAO;

public class Branch {
    private String branchID;
    private String name;
    private String address;
    private String city;
    private String contactNumber;
    private BranchStatus status;


    public Branch(String name, String address, String city, String contactNumber){
        setbranchID();
        setName(name);
        setAddress(address);
        setRegion(city);
        setPhone(contactNumber);
        this.status = BranchStatus.OPERATIONAL;
    }

    // For the read function of the SQL
    public Branch(String branchID,  String name, String address, String region, String contactNumber) {
        this.branchID = branchID;
        setName(name);
        setAddress(address);
        setRegion(region);
        setPhone(contactNumber);
        this.status = BranchStatus.OPERATIONAL;
    }
    public void setbranchID() {
        BranchDAO branchDAO = new BranchDAO();
        this.branchID = branchDAO.getNextAvailableBranchId();

        if(this.branchID == null){
            throw new IllegalStateException("Failed to generate branch ID.");
        }
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid string format. String cannot be null or empty.");
        }

        if (!name.matches("^[A-Za-z\\s'-]+$")) {
            throw new IllegalArgumentException("Invalid string format.");
        }
        this.name =name;
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
    public void setRegion(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid string format. String cannot be null or empty.");
        }

        if (!name.matches("^[A-Za-z\\s'-]+$")) {
            throw new IllegalArgumentException("Invalid string format.");
        }
        this.city =name;
    }
    public void setPhone(String phone) {
        if(phone == null || !phone.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone format. Phone number must be 11 digits and starts with 09.");
        }
        this.contactNumber = phone;
    }

    public void setStatus(BranchStatus status) {
        this.status = status;
    }

    public String getBranchID() {
        return branchID;
    }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
    public String getCity(){
        return this.city;
    }
    public String getContactNumber() {
        return this.contactNumber;
    }
    public BranchStatus getStatus() {
        return this.status;
    }
    public String toString() {
        return String.format("Branch ID: %s | Branch Name: %s | Contact Number: %s | Address: %s | City: %s | Status : %s \n", this.branchID, this.name, this.contactNumber, this.address, this.city, this.status);
    }
}
