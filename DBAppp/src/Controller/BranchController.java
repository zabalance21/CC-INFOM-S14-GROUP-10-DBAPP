package Controller;

import Model.DAO.BranchDAO;
import Model.Entities.Branch;
import Model.Entities.BranchStatus;
import Model.Entities.Client;
import Model.Entities.Contract;

import java.util.List;

public class BranchController {
    private BranchDAO branchDAO = new BranchDAO();
    private ClientController clientController;
    private ContractController contractController;

    public BranchController(ClientController clientController, ContractController contractController) {
        this.clientController = clientController;
        this.contractController = contractController;
    }

    // Add a new branch
    public void addBranch(Branch branch){
        if(branchDAO.branchAddressExists(branch.getAddress())){
            System.out.println("Branch already exists.");
            return;
        }
        branchDAO.addBranch(branch);
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
    public boolean closeBranch(String id){
        List<Client> activeClients = clientController.getAllActiveClients();
        List<Contract> activeContracts = contractController.getAllActiveContracts();
        if (!activeClients.isEmpty() || !activeContracts.isEmpty()) {
            return false;
        }
        branchDAO.removeBranch(id);
        return true;
    }

    // View related records
    public void viewRelatedRecords(String id){
        branchDAO.viewRelatedRecords(id);
    }

    // Prints all Operational Branches
    public void showAllOperationalBranches(){
        List<Branch> branches = branchDAO.getAllBranches();
        System.out.println("List of operational branches: ");
            for (Branch branch : branches){
                if(branch.getStatus() == BranchStatus.OPERATIONAL){
                    System.out.println(String.format("Branch ID: %s | Branch Name: %s\n", branch.getBranchID(), branch.getName()));
                }
        }
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
