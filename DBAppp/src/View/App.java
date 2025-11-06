package View;

import Controller.*;
import Model.DAO.BranchDAO;
import Model.DAO.ContractDAO;

import java.util.Scanner;

public class App {
    private Scanner sc;
    ClientController clientController = new ClientController();
    ContractController contractController = new ContractController();
    ManagerController managerController = new ManagerController(contractController);
    BranchController branchController = new BranchController( clientController, contractController);
    ServiceController serviceController = new ServiceController();

    public App(Scanner sc){
        this.sc = sc;
    }

    public void mainApp(){
        int choice = -1;
        String password = "Hello";
        do{
            System.out.println("Are you an admin or a client?");
            System.out.println("[1] Admin");
            System.out.println("[2] Client");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ", 0,2);
            switch(choice){
                case 1:
                   String checkPass = InputHelper.getStringInput("Enter the admin password: ");
                   if(checkPass.equals(password)){
                       System.out.println("Admin accepted");
                       adminAccess();
                   }
                case 2:

            }
        }while(choice != 0);
    }



    private void adminAccess(){
        int choice = -1;
        do{
            System.out.println("\n=== Admin Access ===\n");
            System.out.println("[1] Manage Client");
            System.out.println("[2] Manage Branches");
            System.out.println("[3] Manage Account Managers");
            System.out.println("[4] Manage Services");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1:
                    ClientView clientView = new ClientView(clientController, sc);
                    clientView.showClientMenu();
                    break;
                case 2:
                    BranchView branchView = new BranchView(branchController, sc);
                    branchView.showBranchMenu();
                    break;
                case 3:
                    ManagerView managerView = new ManagerView(managerController,sc,branchController);
                    managerView.showManagerMenu();
                    break;
                case 4:
                    ServiceView serviceView = new ServiceView(serviceController, sc);
                    serviceView.showMenu();
                    break;
                case 0: System.out.println("Exiting app."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

    private void clientAccess(){
        int choice = -1;
        do{
            System.out.println("\n=== IT SERVICES APP ===\n");
            System.out.println("[1] Create a new service contract");
            System.out.println("[2] Record Contract Payment"); // Will also generate the invoice
            System.out.println("[3] Contract Renewal");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,3);

            switch(choice){
                case 1:

                    break;
                case 2:

                    break;
                case 3:

                    break;
                case 0: System.out.println("Exiting app."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

}
