package Controller;

import Model.DAO.AccountManagerDAO;
import Model.Entities.*;

import java.util.List;

public class ManagerController {
    private final AccountManagerDAO managerDAO = new AccountManagerDAO();
    private ContractController contractController;

    public ManagerController(ContractController contractController) {
        this.contractController = contractController;
    }

    // Add a new manager
    public void addManager(AccountManager manager){
        managerDAO.addManager(manager);
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
        List<Contract> activeContracts = contractController.getAllActiveContracts();

        if (!activeContracts.isEmpty()) {
            return false;
        }
        managerDAO.removeManager(id);
        return true;
    }

    // View related records
    public void viewRelatedRecords(String id){
        managerDAO.viewRelatedRecords(id);
    }

    // Prints all Active Managers
    public void showAllActiveManagers(){
        List<AccountManager> managers = managerDAO.getAllManagers();
        System.out.println("List of active managers: ");
        for (AccountManager manager : managers){
            if(manager.getStatus() == ManagerStatus.ACTIVE){
                System.out.println(String.format("Manager ID: %s | Manager Name: %s\n", manager.getManagerID(), manager.getName()));
            }
        }
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
        List<AccountManager> managers = managerDAO.getAllManagers();
        for (AccountManager manager : managers){
            if(manager.getStatus() == ManagerStatus.ACTIVE){
                System.out.println(String.format("Manager ID: %s | Manager Name: %s\n", manager.getManagerID(), manager.getName()));
            }
        }
        return managers;
    }

}
