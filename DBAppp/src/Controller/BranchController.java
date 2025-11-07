package Controller;

import Model.DAO.AccountManagerDAO;
import Model.DAO.BranchDAO;
import Model.DAO.ClientDAO;
import Model.DAO.ContractDAO;
import Model.Entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchController {
    private BranchDAO branchDAO;
    private ClientDAO clientDAO;
    private AccountManagerDAO  accountManagerDAO;
    private ContractDAO contractDAO;


    public BranchController(BranchDAO branchDAO, ClientDAO clientDAO, AccountManagerDAO accountManagerDAO, ContractDAO contractDAO) {
        this.branchDAO = branchDAO;
        this.clientDAO = clientDAO;
        this.accountManagerDAO = accountManagerDAO;
        this.contractDAO =  contractDAO;
    }

    // Add a new branch
    public boolean addBranch(Branch branch){
        if(branchDAO.branchAddressExists(branch.getAddress())){
            return false;
        }
        branchDAO.addBranch(branch);
        return true;
    }

    // Retrieve a branch by ID
    public Branch getBranchByID(String id) {
        return branchDAO.getBranchByID(id);
    }

    // Update an existing branch
    public void updateExistingBranch(Branch branch){
        branchDAO.updateBranch(branch);
    }

    // Close a branch by ID
    public boolean closeBranch(String branchid){
        if (contractDAO.hasActiveContractsForBranch(branchid)) {
            return false;
        }
        branchDAO.removeBranch(branchid);
        return true;
    }

    // Returns all related records for a branch
    public Map<String, List<?>> getRelatedRecords(String branchId) {
        Map<String, List<?>> relatedRecords = new HashMap<>();
        List<Client> clients = clientDAO.getClientsByBranchID(branchId);
        List<AccountManager> managers = accountManagerDAO.getManagersByBranchID(branchId);

        relatedRecords.put("clients", clients);
        relatedRecords.put("managers", managers);

        return relatedRecords;
    }

    // Prints all Operational Branches
    public List<Branch> getAllOperationalBranches(){
        return branchDAO.getAllOperationalBranches();
    }

    // Prints all branches
    public void showAllBranches(){
        List<Branch> branches = branchDAO.getAllBranches();
        System.out.println("List of branches: ");
        for (Branch branch : branches){
            System.out.println(branch.toString());
        }
    }
}
