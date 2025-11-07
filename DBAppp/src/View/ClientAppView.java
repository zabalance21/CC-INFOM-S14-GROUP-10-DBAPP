package View;

import Controller.*;
import Model.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class ClientAppView {
    private Scanner scanner;
    ManagerView mView;
    ServiceView sView;
    PaymentController paymentController;
    ServiceController serviceController;
    ContractServiceController contractServiceController;
    ClientController clientController;
    ManagerController managerController;
    BranchController branchController;
    ContractController contractController;
    InvoiceController invoiceController;

    public ClientAppView(Scanner scanner,
                         ManagerView mView,
                         ServiceView sView,
                         ServiceController serviceController,
                         ClientController clientController,
                         ManagerController managerController,
                         BranchController branchController,
                         ContractController contractController,
                         InvoiceController invoiceController,
                         ContractServiceController contractServiceController, PaymentController paymentController) {
        this.scanner = scanner;
        this.mView = mView;
        this.sView = sView;
        this.serviceController = serviceController;
        this.clientController = clientController;
        this.managerController = managerController;
        this.branchController = branchController;
        this.contractController = contractController;
        this.invoiceController = invoiceController;
        this.contractServiceController = contractServiceController;
        this.paymentController = paymentController;
    }

    public void createContractAndInvoice() {
        while (true) {
            String name = InputHelper.getStringInput("Enter Client Full Name (Ex. Ronin Zerna): ");
            Client client = clientController.getClientByName(name);

            if (client == null) {
                System.out.println("Client not found. Let's create a new one!");
                addClient();
                client = clientController.getClientByName(name); // re-fetch after creation
            }

            // Proceed only if the client was successfully created or found
            if (client == null) {
                System.out.println("Client creation failed. Try again.");
                continue;
            }

            sView.printAvailableServices();
            String serviceId = InputHelper.getStringInput("Enter the Service ID of your chosen service: ");
            Service service = serviceController.getServiceById(serviceId);
            if (service == null) {
                System.out.println("Service not found. Please try again.");
                continue;
            }

            mView.showActiveManagers();
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

            ContractService contractService = new ContractService(serviceId, contract.getContractID());
            Invoice invoice = new Invoice(contract.getContractID(), client.getClientId(), startDate, endDateInvoice, service.getRate());

            invoiceController.addInvoice(invoice);
            contractController.addContract(contract);
            contractServiceController.addContractService(contractService);

            System.out.println("Contract/Invoice successfully created for " + client.getName() + ".");
            System.out.println(contract.toString());
            System.out.println(invoice.toString());
            System.out.println(contractService.toString());

            //Ask if user wants to create another
            String again = InputHelper.getYesOrNo("\nDo you want to create another contract? (Y/N): ");
            if (!again.equalsIgnoreCase("Y")) {
                System.out.println("Returning to main menu...");
                break;
            }
        }
    }


    public void recordInvoice() {
        while (true) {
            String name = InputHelper.getStringInput("Enter Client Full Name (Ex. Ronin Zerna): ");
            Client client = clientController.getClientByName(name);

            if (client == null) {
                System.out.println("Client not found. Please try again.");
                continue;
            }

            invoiceController.showAllActiveClientInvoices(client.getClientId());

            String invoiceId = InputHelper.getStringInput("Enter Invoice ID to pay (Exact Amount Only): ");
            Invoice chosenInvoice = invoiceController.getInvoicebyID(invoiceId, client.getClientId());
            if (chosenInvoice == null) {
                System.out.println("Invoice not found for this client. Please try again.");
                continue;
            }

            Contract contract = invoiceController.getContractbyID(invoiceId);

            BigDecimal paidAmount = InputHelper.getBigDecimalInput("Enter Paid Amount: ");
            if (paidAmount.compareTo(chosenInvoice.getAmount()) != 0) {
                System.out.println("Paid amount must be exactly equal to the invoice amount.");
                continue;
            }

            Payment payment = new Payment(invoiceId, client.getClientId(), LocalDate.now(), paidAmount);
            paymentController.addPayment(payment);
            invoiceController.markInvoicePaid(invoiceId);
            contractController.deleteContract(contract.getContractID());
            contractServiceController.setContractServiceInvalid(contract.getContractID());

            System.out.println("Payment successfully recorded for " + client.getName() +
                    ". Reference Number: " + payment.getReceiptNumber() + ".");

            String again = InputHelper.getYesOrNo("Record another payment? (Y/N): ");
            if (!again.equalsIgnoreCase("Y")) {
                System.out.println("Returning to main menu...");
                break;
            }
        }
    }

    public void contractRenewal() {
        while (true) {
            String name = InputHelper.getStringInput("Enter Client Full Name (Ex. Ronin Zerna): ");
            Client client = clientController.getClientByName(name);

            if (client == null) {
                System.out.println("Client not found. Please try again.");
                continue;
            }

            contractController.showAllClosedContracts(client.getClientId());
            String contractId = InputHelper.getStringInput("Enter the Contract ID of the contract you would like to renew: ");
            Contract contract = contractController.getContractByID(contractId);

            if (contract == null) {
                System.out.println("Contract not found for this client. Please try again.");
                continue;
            }

            // Store old details before renewal
            LocalDate oldStart = contract.getStartDate();
            LocalDate oldEnd = contract.getEndDate();
            ContractStatus oldStatus = contract.getContractStatus();

            // Perform renewal
            contract.setStartDate(LocalDate.now());
            contract.setEndDate(LocalDate.now().plusYears(1));
            contract.setContractStatus(ContractStatus.ACTIVE);
            contractServiceController.setContractServiceValid(contract.getContractID());

            // Print old and new details
            System.out.println("\n===== CONTRACT RENEWAL SUCCESSFUL =====");
            System.out.println("Old Contract Details:");
            System.out.println("Start Date: " + oldStart);
            System.out.println("End Date: " + oldEnd);
            System.out.println("Status: " + oldStatus);

            System.out.println("\nNew Contract Details:");
            System.out.println("Start Date: " + contract.getStartDate());
            System.out.println("End Date: " + contract.getEndDate());
            System.out.println("Status: " + contract.getContractStatus());
            System.out.println("========================================\n");

            String again = InputHelper.getYesOrNo("Renew another contract? (Y/N): ");
            if (!again.equalsIgnoreCase("Y")) {
                System.out.println("Returning to main menu...");
                break;
            }
        }
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
