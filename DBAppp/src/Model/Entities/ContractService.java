package Model.Entities;

public class ContractService {
    private String serviceID;
    private String contractID;

    public ContractService(String serviceID, String contractID) {
        this.serviceID = serviceID;
        this.contractID = contractID;
    }

    public String getServiceID() {
        return serviceID;
    }
    public String getContractID() {
        return  contractID;
    }
}
