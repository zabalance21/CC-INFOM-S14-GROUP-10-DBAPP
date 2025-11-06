package View;

import Controller.*;
import Model.Entities.*;

import java.time.LocalDate;
import java.util.Scanner;

public class ClientAppView {
    private Scanner scanner;
    ClientController clientController;
    ServiceController serviceController;
    ManagerController managerController;
    BranchController branchController;
    ContractController contractController;
    InvoiceController invoiceController;

    public ClientAppView(Scanner scanner, ClientController clientController, ServiceController serviceController, ManagerController managerController, BranchController branchController, ContractController contractController,  InvoiceController invoiceController) {
        this.scanner = scanner;
        this.clientController = clientController;
        this.serviceController = serviceController;
        this.managerController = managerController;
        this.branchController = branchController;
        this.contractController = contractController;
        this.invoiceController = invoiceController;
    }

    public void createContractAndInvoice() {
        while (true) {
            String name = InputHelper.getStringInput("Enter Client Full Name (Ex. Ronin Zerna): ");
            Client client = clientController.getClientByName(name);

            if (client == null) {
                System.out.println("Client not found. Let's create a new one!");
                addClient();
            }

            // Proceed only if the client was successfully created or found
            if (client == null) {
                System.out.println("Client creation failed. Try again.");
                continue;
            }

            serviceController.printAvailableServices();
            String serviceId = InputHelper.getStringInput("Enter the Service ID of your chosen service: ");
            Service service = serviceController.getServiceById(serviceId);
            if (service == null) {
                System.out.println("Service not found. Please try again.");
                continue;
            }

            managerController.showAllActiveManagers();
            String managerId = InputHelper.getStringInput("Enter the Manager ID of your chosen account manager: ");
            AccountManager manager = managerController.getManagerByID(managerId);
            if (manager == null) {
                System.out.println("Manager not found. Please try again.");
                continue;
            }

            Branch branch = branchController.getBranchByID(manager.getBranchID());
            if (branch == null) {
                System.out.println("Branch not found for the selected manager.");
                continue;
            }

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusYears(1);
            LocalDate endDateInvoice = startDate.plusDays(30);

            Contract contract = new Contract(
                    client.getClientId(),
                    managerId,
                    branch.getBranchID(),
                    startDate,
                    endDate
            );

            Invoice invoice = new Invoice(contract.getContractID(), client.getClientId(), startDate, endDateInvoice, service.getRate());
            invoiceController.addInvoice(invoice);
            contractController.addContract(contract);
            System.out.println("Contract/Invoice successfully created for " + client.getName() + ".");
            System.out.println(contract.toString());
            System.out.println(invoice.toString());
        }
    }


    public void recordInvoice(){

    }


    private void addClient(){
        try {
            String name = InputHelper.getStringInput("Enter name: ");
            String email = InputHelper.getStringInput("Enter email: ");
            String phoneNumber = InputHelper.getStringInput("Enter phone number: ");
            String address = InputHelper.getStringInput("Enter address: ");

            Client client = new Client(name, email, phoneNumber, address);
            clientController.addClient(client);
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

}
