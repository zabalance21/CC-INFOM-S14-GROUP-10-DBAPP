package Controller;

import Model.DAO.AccountManagerDAO;
import Model.DAO.ClientDAO;
import Model.DAO.ContractDAO;
import Model.Entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerController {
    private AccountManagerDAO managerDAO;
    private ContractDAO contractDAO;
    private ClientDAO clientDAO;

    public ManagerController( AccountManagerDAO managerDAO, ContractDAO contractDAO, ClientDAO clientDAO) {
        this.managerDAO = managerDAO;
        this.contractDAO = contractDAO;
        this.clientDAO = clientDAO;
    }

    // Add a new manager
    public boolean addManager(AccountManager manager){
        if(managerDAO.checkManagerExists(manager.getName())){
            return false;
        }
        managerDAO.addManager(manager);
        return true;
    }

    // Retrieve a manager by ID
    public AccountManager getManagerByID(String id) {
        return managerDAO.getManagerByID(id);
    }

    // Update an existing manager
    public void updateExistingManager(AccountManager manager){
        managerDAO.updateManagers(manager);
    }

    // Remove a manager by ID
    public boolean removeManager(String id){

        if (contractDAO.hasActiveContractsForManager(id)){
            return false;
        }
        managerDAO.removeManager(id);
        return true;
    }

    // View related records
    public void viewRelatedRecords(String id){
        managerDAO.viewRelatedRecords(id);
    }

    public Map<String, List<?>> getRelatedRecords(String managerID) {
        Map<String, List<?>> relatedRecords = new HashMap<>();
        List<Contract> contracts = contractDAO.getContractsByManagerID(managerID);
        List<Client> clients = clientDAO.getManagersByBranchID(managerID);
        relatedRecords.put("contracts", contracts);
        relatedRecords.put("clients", clients);
        return relatedRecords;
    }

    // Prints all managers (Active/Resigned)
    public void showAllManagers(){
        List<AccountManager> managers = managerDAO.getAllManagers();
        System.out.println("List of managers: ");
        for (AccountManager manager : managers){
            System.out.println(manager.toString());
        }
    }

    // Returns all active managers
    public List<AccountManager> getAllActiveManagers(){
        return managerDAO.getAllActiveManagers();
    }

}
