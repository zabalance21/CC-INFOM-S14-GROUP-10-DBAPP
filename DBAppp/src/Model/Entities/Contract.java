package Model.Entities;

import java.time.LocalDate;

public class Contract {
    private String contractID;
    private String clientID;
    private String managerID;
    private String branchID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String terms;
    private ContractStatus contractStatus;
    private static int counter = 3;
    private static final int MAX_CONTRACTS = 1000;

    public Contract(String clientID, String managerID, String branchID,  LocalDate startDate, LocalDate endDate) {
        setContractID();
        this.clientID = clientID;
        this.managerID = managerID;
        this.branchID = branchID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = ContractStatus.ACTIVE;
    }

    // For the read function of the SQL
    public Contract(String contractID, String clientID, String managerID, String branchID,  LocalDate startDate, LocalDate endDate) {
        this.contractID = contractID;
        this.clientID = clientID;
        this.managerID = managerID;
        this.branchID = branchID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractStatus = ContractStatus.ACTIVE;
    }

    public void setContractID(){
        if(counter > MAX_CONTRACTS){
            throw new IllegalStateException("All possible contract IDs have been used.");
        }
        this.contractID = String.format("CT-%03d", counter);
        counter++;
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

    public String getBranchID() {
        return this.branchID;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public String getTerms() {
        return this.terms;
    }

    public ContractStatus getContractStatus() {
        return this.contractStatus;
    }
}

