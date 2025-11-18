package Model.Entities;

public class ContractService {
    private String contractServiceID;
    private String serviceID;
    private String contractID;
    private ClientStatus status;
    private static int counter = 4;
    private static final int MAX_CS = 1000;

    public ContractService(String serviceID, String contractID) {
        setContractID();
        this.serviceID = serviceID;
        this.contractID = contractID;
        this.status = ClientStatus.ACTIVE;
    }

    public ContractService(String contractServiceID, String serviceID, String contractID) {
        this.contractServiceID = contractServiceID;
        this.serviceID = serviceID;
        this.contractID = contractID;
    }

    public void setContractID(){
        if(counter > MAX_CS){
            throw new IllegalStateException("All possible contract service IDs have been used.");
        }
        this.contractServiceID = String.format("CS-%03d", counter);
        counter++;
    }

    public void setStatus(ClientStatus status){
        this.status = status;
    }

    public String getServiceID() {
        return serviceID;
    }
    public String getContractID() {
        return contractID;
    }

    public ClientStatus getStatus(){
        return status;
    }
    public String getContractServiceID() { return contractServiceID; }

    public String toString(){
        return String.format("ContractService ID: %s | ServiceID: %s | ContractID: %s, Status: %s",contractServiceID, serviceID, contractID, status.toString());
    }
}
