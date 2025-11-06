package View;

import Controller.BranchController;
import Model.Entities.Branch;
import Model.Entities.Client;

import java.util.Scanner;

public class BranchView {
    private BranchController branchController;
    private Scanner scanner;
    public BranchView(BranchController branchController,Scanner scanner) {
        this.branchController = branchController;
        this.scanner = scanner;
    }
    public void showBranchMenu(){
        int choice = -1;
        do{
            System.out.println("\n===Branch Menu===\n");
            System.out.println("[1] Add Branch");
            System.out.println("[2] View Branch by ID");
            System.out.println("[3] View All Branches");
            System.out.println("[4] Update Branch");
            System.out.println("[5] Close Branch");
            System.out.println("[6] View Related Records (Clients/Account Managers)");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1: addBranch(); break;
                case 2: viewBranchByID(); break;
                case 3: branchController.showAllBranches(); break;
                case 4: updateBranch(); break;
                case 5: deleteBranch(); break;
                case 6: viewRelatedRecords(); break;
                case 0: System.out.println("Exiting Branch menu.");
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

    private void addBranch(){
        try {
            String name = InputHelper.getStringInput("Enter branch name (Ex. Manila Branch): ");
            String address = InputHelper.getStringInput("Enter address: ");
            String region = InputHelper.getStringInput("Enter the city where you are located: ");
            String contactNumber = InputHelper.getStringInput("Enter mobile contact number (Ex. 09299122333): ");

            Branch branch = new Branch(name, address, region, contactNumber);
            branchController.addBranch(branch);
            System.out.println("Branch added successfully.");
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void viewBranchByID(){
        branchController.showAllOperationalBranches();
        String branchID = InputHelper.getStringInput("Enter Branch ID: ");
        Branch branch = branchController.getBranchByID(branchID);

        if(branch != null){
            System.out.println(branch.toString());
        }else{
            System.out.println("Client not found!");
        }
    }

    private void updateBranch(){
        branchController.showAllOperationalBranches();
        String branchID = InputHelper.getStringInput("Enter Branch ID: ");
        Branch branch = branchController.getBranchByID(branchID);
        System.out.println("Enter a blank space if you are not going to update that specific field.");
        if(branch != null){
            System.out.println("Enter new name (Current name: " + branch.getName() + "):");
            String newName = scanner.nextLine();
            System.out.println("Enter new address (Current address: " + branch.getAddress() + "):");
            String newAddress = scanner.nextLine();
            System.out.println("Enter new contact number (Current Contact Number: " + branch.getContactNumber() + "):");
            String newContactNumber = scanner.nextLine();
            System.out.println("Enter new city (Current city: " + branch.getCity() + "):");
            String newRegion = scanner.nextLine();

            branch.setName(newName.isEmpty() ? branch.getName() : newName);
            branch.setAddress(newAddress.isEmpty() ? branch.getAddress() : newAddress);
            branch.setPhone(newContactNumber.isEmpty() ? branch.getContactNumber() : newContactNumber);
            branch.setRegion(newRegion.isEmpty() ? branch.getCity() : newRegion);

            branchController.updateExistingBranch(branch);
        }else{
            System.out.println("Branch not found!");
        }
    }

    private void deleteBranch(){
        branchController.showAllOperationalBranches();
        String branchID = InputHelper.getStringInput("Enter Branch ID: ");
        if(branchController.closeBranch(branchID)){
            System.out.println("Branch closed successfully.");
        }else{
            System.out.println("Cannot close branch: there are still active contracts.");
        }
    }

    private void viewRelatedRecords(){
        branchController.showAllOperationalBranches();
        String branchID = InputHelper.getStringInput("Enter Branch ID: ");
        branchController.viewRelatedRecords(branchID);
    }
}
