package View;

import Controller.BranchController;
import Controller.ManagerController;
import Model.Entities.AccountManager;
import Model.Entities.Client;
import Model.Entities.Contract;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ManagerView {
    private ManagerController managerController;
    private Scanner scanner;
    private BranchView branchView;

    public ManagerView(ManagerController managerController, Scanner scanner, BranchView branchView) {
        this.managerController = managerController;
        this.scanner = scanner;
        this.branchView = branchView;
    }

    public void showManagerMenu(){
        int choice = -1;
        do{
            System.out.println("\n===Account Manager Menu===\n");
            System.out.println("[1] Add Manager");
            System.out.println("[2] View Active Managers by ID");
            System.out.println("[3] View All Managers");
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
            if(!managerController.addManager(manager)){
                System.out.println("Manager already exists.");
            }else{
                System.out.println("Manager added successfully.");
            }
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private String whichBranch(){
        branchView.showAllOperationalBranches();
        String choice = InputHelper.getStringInput("Choose your branch ID: ");
        return choice;
    }
    private void viewManagerByID(){
        showActiveManagers();
        String clientID = InputHelper.getStringInput("Enter Manager ID: ");
        AccountManager manager = managerController.getManagerByID(clientID);

        if(manager != null){
            System.out.println(manager.toString());
        }else{
            System.out.println("Manager not found!");
        }
    }

    private void updateManager(){
        showActiveManagers();
        String clientID = InputHelper.getStringInput("Enter Manager ID: ");
        AccountManager manager = managerController.getManagerByID(clientID);
        if(manager != null){
            System.out.println("Enter new name (Current name: " + manager.getName() + "):");
            String newName = scanner.nextLine().trim();
            System.out.println("Enter new email (Current Email: " + manager.getContactInfo() + "):");
            String newEmail = scanner.nextLine().trim();

            manager.setName(newName.isEmpty() ? manager.getName() : newName);
            manager.setContactInfo(newEmail.isEmpty() ? manager.getContactInfo() : newEmail);

            managerController.updateExistingManager(manager);
        }else{
            System.out.println("Manager not found!");
        }
    }

    private void removeManager(){
        showActiveManagers();
        String managerID = InputHelper.getStringInput("Enter Manager ID: ");
        if(!managerController.removeManager(managerID)){
            System.out.println("Cannot resign — manager still has active contracts.");
        }else{
            System.out.println("Manager resigned successfully.");
        }
    }

    private void viewRelatedRecords(){
        showActiveManagers();
        String managerID = InputHelper.getStringInput("Enter Manager ID: ");
        Map<String, List<?>> relatedRecords = managerController.getRelatedRecords(managerID);
        List<Client> clients = (List<Client>) relatedRecords.get("clients");
        List<Contract> contracts = (List<Contract>) relatedRecords.get("contracts");

        System.out.println("\n=== Clients assigned to Manager " + managerID + " ===");
        if (clients.isEmpty()) {
            System.out.println("This manager has no clients yet.");
        } else {
            for (Client c : clients) {
                System.out.println("Client ID: " + c.getClientId() +
                        " | Name: " + c.getName() +
                        " | Email: " + c.getEmail() +
                        " | Phone: " + c.getPhone() +
                        " | Status: " + c.getStatus());
            }
        }

        System.out.println("\n=== Contracts handled by Manager " + managerID + " ===");
        if (contracts.isEmpty()) {
            System.out.println("This manager does not handle any contracts yet.");
        } else {
            for (Contract ct : contracts) {
                System.out.println("Contract ID: " + ct.getContractID() +
                        " | Client ID: " + ct.getClientID() +
                        " | Start Date: " + ct.getStartDate() +
                        " | End Date: " + ct.getEndDate() +
                        " | Status: " + ct.getContractStatus());
            }
        }
    }

    public void showActiveManagers(){
        List<AccountManager> managers = managerController.getAllActiveManagers();
        for(AccountManager m :managers){
            System.out.println(String.format("Manager ID: %s | Manager Name: %s\n", m.getManagerID(), m.getName()));
        }
    }
}
