package View;

import Controller.BranchController;
import Controller.ManagerController;
import Model.Entities.AccountManager;
import Model.Entities.Client;

import java.util.Scanner;

public class ManagerView {
    private ManagerController managerController;
    private Scanner scanner;
    private BranchController branchController;

    public ManagerView(ManagerController managerController, Scanner scanner, BranchController branchController) {
        this.managerController = managerController;
        this.scanner = scanner;
        this.branchController = branchController;
    }

    public void showManagerMenu(){
        int choice = -1;
        do{
            System.out.println("\n===Account Manager Menu===\n");
            System.out.println("[1] Add Manager");
            System.out.println("[2] View Active Managers by ID");
            System.out.println("[3] View All Clients");
            System.out.println("[4] Update Manager");
            System.out.println("[5] Delete Manager");
            System.out.println("[6] View Related Records (Clients/Contracts)");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1: addManager(); break;
                case 2: viewManagerByID(); break;
                case 3: managerController.showAllManagers(); break;
                case 4: updateManager(); break;
                case 5: removeManager(); break;
                case 6: viewRelatedRecords(); break;
                case 0: System.out.println("Exiting client menu."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

    private void addManager(){
        try {
            String name = InputHelper.getStringInput("Enter full name (Ex. Ronin Zerna): ");
            String phoneNumber = InputHelper.getStringInput("Enter email: ");
            String branchId = whichBranch();
            AccountManager manager = new AccountManager(name, phoneNumber, branchId);
            managerController.addManager(manager);
            System.out.println("Manager added successfully.");
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private String whichBranch(){
        branchController.showAllOperationalBranches();
        String choice = InputHelper.getStringInput("Choose your branch ID: ");
        return choice;
    }
    private void viewManagerByID(){
        managerController.showAllActiveManagers();
        String clientID = InputHelper.getStringInput("Enter Manager ID: ");
        AccountManager manager = managerController.getManagerByID(clientID);

        if(manager != null){
            System.out.println(manager.toString());
        }else{
            System.out.println("Manager not found!");
        }
    }

    private void updateManager(){
        managerController.showAllActiveManagers();
        String clientID = InputHelper.getStringInput("Enter Manager ID: ");
        AccountManager manager = managerController.getManagerByID(clientID);
        if(manager != null){
            System.out.println("Enter new name (Current name: " + manager.getName() + "):");
            String newName = scanner.nextLine();
            System.out.println("Enter new email (Current Email: " + manager.getContactInfo() + "):");
            String newEmail = scanner.nextLine();

            manager.setName(newName.isEmpty() ? manager.getName() : newName);
            manager.setContactInfo(newEmail.isEmpty() ? manager.getContactInfo() : newEmail);

            managerController.updateExistingManager(manager);
        }else{
            System.out.println("Manager not found!");
        }
    }

    private void removeManager(){
        managerController.showAllActiveManagers();
        String managerID = InputHelper.getStringInput("Enter Manager ID: ");
        if(managerController.removeManager(managerID)){
            System.out.println("Manager resigned successfully.");
        }else{
            System.out.println("Manager cannot resign: there are still active contracts.");
        }
    }

    private void viewRelatedRecords(){
        managerController.showAllActiveManagers();
        String managerID = InputHelper.getStringInput("Enter Manager ID: ");
        managerController.viewRelatedRecords(managerID);
    }
}
