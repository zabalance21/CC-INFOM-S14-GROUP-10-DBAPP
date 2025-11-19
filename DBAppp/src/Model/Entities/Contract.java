package Model.Entities;

import java.time.LocalDate;

import Model.DAO.ContractDAO;

public class Contract {
    private String contractID;
    private String clientID;
    private String managerID;
    private LocalDate startDate;
    private LocalDate endDate;
    private ContractStatus contractStatus;

    public Contract(String clientID, String managerID, LocalDate startDate, LocalDate endDate) {
        setContractID();
        this.clientID = clientID;
        this.managerID = managerID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = ContractStatus.ACTIVE;
    }

    // For the read function of the SQL
    public Contract(String contractID, String clientID, String managerID, LocalDate startDate, LocalDate endDate) {
        this.contractID = contractID;
        this.clientID = clientID;
        this.managerID = managerID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setContractID(){
        ContractDAO contractDAO = new ContractDAO();
        this.contractID = contractDAO.getNextAvailableContractId();

        if(this.contractID == null){
            throw new IllegalStateException("Failed to generate contract ID.");
        }
    }

    public void setContractStatus(ContractStatus contractStatus){
        this.contractStatus = contractStatus;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }
    public String getContractID() {
        return this.contractID;
    }

    public String getClientID() {
        return this.clientID;
    }

    public String getManagerID() {
        return this.managerID;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public ContractStatus getContractStatus() {
        return this.contractStatus;
    }

    public String toString(){
        return String.format("Contract ID: %s | Status: %s\n", this.contractID, this.contractStatus);
    }
}

