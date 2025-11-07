package Model.Entities;

public class ContractService {
    private String serviceID;
    private String contractID;
    private ClientStatus status;

    public ContractService(String serviceID, String contractID) {
        this.serviceID = serviceID;
        this.contractID = contractID;
        this.status = ClientStatus.ACTIVE;
    }

    public String getServiceID() {
        return serviceID;
    }
    public String getContractID() {
        return contractID;
    }

    public String toString(){

        return String.format("serviceID: %s, contractID: %s, status: %s", serviceID, contractID, status.toString());
    }
}
