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
    ClientView cView;
    BranchView bView;
    PaymentController paymentController;
    ClientController clientController;
    ContractController contractController;
    ContractServiceController contractServiceController;

    public ClientAppView(Scanner scanner,
                         ManagerView mView,
                         ServiceView sView,
                         ClientView cView,
                         BranchView bView,
                         ClientController clientController,
                         ContractController contractController,
                         PaymentController paymentController, ContractServiceController contractServiceController) {
        this.scanner = scanner;
        this.mView = mView;
        this.sView = sView;
        this.bView = bView;
        this.clientController = clientController;
        this.contractController = contractController;
        this.paymentController = paymentController;
        this.contractServiceController = contractServiceController;
        this.cView = cView;
    }

    public void createContractAndInvoice() {
        System.out.println("=== CREATE CONTRACT AND INVOICE ===");

        while(true) {
            String check = InputHelper.getYesOrNo("Are you a new client?");
            if (!check.equalsIgnoreCase("Y")) {
                cView.showActiveClients();
                String clientName = InputHelper.getStringInput("Enter Client Name: ");
                sView.printAvailableServices();
                String serviceId = InputHelper.getStringInput("Enter the Service ID: ");
                mView.showActiveManagers();
                String managerId = InputHelper.getStringInput("Enter the Manager ID: ");


                boolean success = contractController.createContractAndInvoice(clientName, serviceId, managerId);

                if (success) {
                    System.out.println("Contract and invoice created successfully!");
                } else {
                    System.out.println("Failed to create contract and invoice. Please try again.");
                    continue;
                }
                String again = InputHelper.getYesOrNo("Create another contract");
                if (!again.equalsIgnoreCase("Y")) {
                    System.out.println("Returning to main menu...");
                    break;
                }
            }else{
                addClient();
                System.out.println("New client added successfully!");
                break;
            }
        }
    }


    public void recordInvoice() {
        while (true) {
            cView.showActiveClients();
            String clientId = InputHelper.getStringInput("Enter Client ID (Ex. CL-001): ");
            paymentController.showAllActiveClientInvoices(clientId);
            String invoiceId = InputHelper.getStringInput("Enter Invoice ID to pay (Exact Amount Only): ");
            BigDecimal paidAmount = InputHelper.getBigDecimalInput("Enter Paid Amount: ");

            boolean success = paymentController.processPayment(clientId, invoiceId, paidAmount);

            if (success) {
                System.out.println("Payment successfully recorded!");
            } else {
                System.out.println("Payment failed. Please check client/invoice info.");
                continue;
            }

            String again = InputHelper.getYesOrNo("Record another payment?");
            if (!again.equalsIgnoreCase("Y")) {
                System.out.println("Returning to main menu...");
                break;
            }
        }
    }


    public void contractRenewal() {
        while (true) {
            cView.showActiveClients();
            String clientId = InputHelper.getStringInput("Enter Client ID (Ex. CL-001): ");
            // Ask controller to handle the renewal process
            ContractRenewalResult result = contractServiceController.renewContract(clientId);

            if (result == null) {
                System.out.println("Contract renewal failed. Try again.");
            } else {
                System.out.println("\n===== CONTRACT RENEWAL SUCCESSFUL =====");
                System.out.println("Old Contract Details:");
                System.out.println("Start Date: " + result.oldStart);
                System.out.println("End Date: " + result.oldEnd);
                System.out.println("Status: " + result.oldStatus);

                System.out.println("\nNew Contract Details:");
                System.out.println("Start Date: " + result.newStart);
                System.out.println("End Date: " + result.newEnd);
                System.out.println("Status: " + result.newStatus);
                System.out.println("========================================\n");
            }

            String again = InputHelper.getYesOrNo("Renew another contract?");
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
