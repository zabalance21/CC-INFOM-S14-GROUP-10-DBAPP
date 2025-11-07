package Controller;

import Model.DAO.ContractDAO;
import Model.Entities.Contract;
import Model.Entities.ContractStatus;


import java.util.ArrayList;
import java.util.List;

public class ContractController {
    private ContractDAO contractDAO;
    ContractServiceController contractServiceController;


    public ContractController(ContractServiceController contractServiceController, ContractDAO contractDAO) {
        this.contractServiceController = contractServiceController;
        this.contractDAO = contractDAO;
    }
    // Add a new contract
    public void addContract(Contract contract){
        contractDAO.addContract(contract);
    }

    // Retrieve a contract by ID
    public Contract getContractByID(String id) {
        return contractDAO.getContractByID(id);
    }

    // Update an existing contract
    public void updateExistingContract(Contract contract){
        contractDAO.updateContract(contract);
    }

    // Soft Delete a contract by ID
    public void deleteContract(String id){
        contractDAO.closeContract(id);
    }

    // View related records
    public void viewRelatedRecords(String id){
        contractDAO.viewRelatedRecords(id);
    }

    // Prints all Closed Contracts
    public void showAllClosedContracts(String clientid){
        List<Contract> contracts = contractDAO.getContractsByClientId(clientid);
        System.out.println("List of active contracts: ");
        for (Contract contract : contracts){
            if(contract.getContractStatus() == ContractStatus.CLOSED){
                System.out.println(contract.toString());
            }
        }
    }

    public boolean checkManagersContracts(String id){
        return contractDAO.hasActiveContractsForManager(id);
    }

    public boolean checkBranchContracts(String branchId){
        return contractDAO.hasActiveContractsForBranch(branchId);
    }

    public boolean checkServiceContracts(String serviceId){
        return contractDAO.hasActiveContractsUsingService(serviceId);
    }

    public boolean checkClientContracts(String clientID){
        return contractDAO.hasActiveContractsForClient(clientID);
    }

}

