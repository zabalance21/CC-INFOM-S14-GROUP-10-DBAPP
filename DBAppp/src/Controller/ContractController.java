package Controller;

import Model.DAO.ContractDAO;
import Model.Entities.Contract;
import Model.Entities.ContractStatus;


import java.util.List;

public class ContractController {
    private ContractDAO contractDAO = new  ContractDAO();
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

    // Prints all Active Contracts
    public void showAllActiveContracts(){
        List<Contract> contracts = contractDAO.getAllContracts();
        System.out.println("List of active contracts: ");
        for (Contract contract : contracts){
            if(contract.getContractStatus() == ContractStatus.ACTIVE){
                System.out.println(String.format("Contract ID: %s\n", contract.getContractID()));
            }
        }
    }

    // Prints all contracts (Active/Closed)
    public void showAllBranches(){
        List<Contract> contracts = contractDAO.getAllContracts();
        System.out.println("List of all contracts: ");
        for (Contract contract : contracts){
            System.out.println(contract.toString());
        }
    }

    //Returns all active contracts
    public List<Contract> getAllActiveContracts(){
        List<Contract> contracts = contractDAO.getAllContracts();
        for (Contract contract : contracts){
            if(contract.getContractStatus() == ContractStatus.ACTIVE){
                contracts.add(contract);
            }
        }
        return contracts;
    }

}

