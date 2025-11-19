package Model.Entities;

import Model.DAO.ContractServiceDao;

public class ContractService {
    private String contractServiceID;
    private String serviceID;
    private String contractID;
    private ClientStatus status;

    public ContractService(String serviceID, String contractID) {
        setContractServiceID();
        this.serviceID = serviceID;
        this.contractID = contractID;
        this.status = ClientStatus.ACTIVE;
    }

    public ContractService(String contractServiceID, String serviceID, String contractID) {
        this.contractServiceID = contractServiceID;
        this.serviceID = serviceID;
        this.contractID = contractID;
    }

    public ContractService(String contractServiceID, String serviceID, String contractID, ClientStatus status) {
        this.contractServiceID = contractServiceID;
        this.serviceID = serviceID;
        this.contractID = contractID;
        this.status = status;
    }

    public void setContractServiceID(){
        ContractServiceDao contractServiceDao = new ContractServiceDao();
        this.contractServiceID = contractServiceDao.getNextAvailableContractServiceId();

        if(this.contractServiceID == null){
            throw new IllegalStateException("Failed to generate contract service ID.");
        }
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
