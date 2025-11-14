package View;

import Controller.*;
import Model.DAO.*;

import java.util.Scanner;

public class App {
    private Scanner sc;
    ContractDAO  contractDAO = new ContractDAO();
    ClientDAO clientDAO = new ClientDAO();
    BranchDAO branchDAO = new BranchDAO();
    InvoiceDAO invoiceDAO = new InvoiceDAO();
    ServiceDAO serviceDAO = new ServiceDAO();
    PaymentDAO paymentDAO = new PaymentDAO();
    AccountManagerDAO  accountManagerDAO = new AccountManagerDAO();
    ContractServiceDao  contractServiceDao = new ContractServiceDao();

    ContractServiceController contractServiceController =   new ContractServiceController(contractServiceDao, clientDAO, serviceDAO, contractDAO, invoiceDAO);
    InvoiceController invoiceController = new InvoiceController();
    PaymentController paymentController = new PaymentController(paymentDAO, clientDAO,invoiceDAO, contractDAO, contractServiceDao);
    ContractController contractController = new ContractController(contractDAO, clientDAO, serviceDAO, branchDAO,
            contractServiceDao, invoiceDAO, accountManagerDAO);
    ManagerController managerController = new ManagerController(accountManagerDAO,contractDAO,clientDAO);
    ClientController clientController = new ClientController(contractDAO,invoiceDAO,clientDAO);
    BranchController branchController = new BranchController(branchDAO, clientDAO, accountManagerDAO, contractDAO);
    ServiceController serviceController = new ServiceController(contractDAO, serviceDAO, contractServiceDao);

    BranchView branchView;
    ManagerView managerView;
    ClientView clientView;
    ServiceView serviceView;
    ClientAppView clientAppView;
    ReportView reportView;

    public App(Scanner sc){
        this.sc = sc;
        this.branchView = new BranchView(branchController, sc);
        this.clientView = new ClientView(clientController, sc);
        this.managerView = new ManagerView(managerController,sc, branchView);
        this.serviceView = new ServiceView(serviceController, sc);
        this.clientAppView = new ClientAppView(sc, managerView, serviceView,clientView, branchView, clientController,
                contractController, paymentController, contractServiceController);
        this.reportView = new ReportView(clientController,paymentController,invoiceController,serviceController);

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
                   if(checkPass.equalsIgnoreCase(password)){
                       System.out.println("Admin accepted");
                       adminAccess();
                       break;
                   }else{
                       System.out.println("Wrong password");
                       break;
                   }
                case 2:
                    clientAccess();
                    break;
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
            System.out.println("[5] Generate Reports");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1:
                    clientView.showClientMenu();
                    break;
                case 2:
                    branchView.showBranchMenu();
                    break;
                case 3:
                    managerView.showManagerMenu();
                    break;
                case 4:
                    serviceView.showMenu();
                    break;
                case 5:
                    reportView.reportMenu();
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
            System.out.println("[1] Create a new service contract"); // Will also generate the invoice
            System.out.println("[2] Record Contract Payment");
            System.out.println("[3] Contract Renewal");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,3);

            switch(choice){
                case 1:
                    clientAppView.createContractAndInvoice();
                    break;
                case 2:
                    clientAppView.recordInvoice();
                    break;
                case 3:
                    clientAppView.contractRenewal();
                    break;
                case 0: System.out.println("Exiting app."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

}
