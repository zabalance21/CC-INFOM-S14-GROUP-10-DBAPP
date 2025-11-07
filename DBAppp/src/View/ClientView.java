package View;

import Controller.ClientController;
import Model.Entities.Client;
import Model.Entities.ClientStatus;
import Model.Entities.Contract;
import Model.Entities.Invoice;

import java.lang.classfile.attribute.InnerClassesAttribute;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientView {
    private ClientController clientController;
    private Scanner scanner;

    public ClientView(ClientController clientController, Scanner scanner) {
        this.clientController = clientController;
        this.scanner = scanner;
    }

    public void showClientMenu(){
        int choice = -1;
        do{
            System.out.println("\n===Client Menu===\n");
            System.out.println("[1] Add Client");
            System.out.println("[2] View Client by ID");
            System.out.println("[3] View All Clients");
            System.out.println("[4] Update Client");
            System.out.println("[5] Delete Client");
            System.out.println("[6] View Related Records (Contracts/Invoices)");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1: addClient(); break;
                case 2: viewClientByID(); break;
                case 3: clientController.showAllClients(); break;
                case 4: updateClient(); break;
                case 5: deleteClient(); break;
                case 6: viewRelatedRecords(); break;
                case 0: System.out.println("Exiting client menu.");
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

    private void addClient(){
        try {
            String name = InputHelper.getStringInput("Enter name: ");
            String email = InputHelper.getStringInput("Enter email: ");
            String phoneNumber = InputHelper.getStringInput("Enter phone number (Ex. 09452132936): ");
            String address = InputHelper.getStringInput("Enter address: ");

            Client client = new Client(name, email, phoneNumber, address);
            if(!clientController.addClient(client)){
                System.out.println("Client already exists");
            }else{
                System.out.println("Client added successfully");
            }
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void viewClientByID(){
        showActiveClients();
        String clientID = InputHelper.getStringInput("Enter Client ID: ");
        Client client = clientController.getClientByID(clientID);

        if(client != null){
            System.out.println(client.toString());
        }else{
            System.out.println("Client not found!");
        }
    }

    private void updateClient(){
        showActiveClients();
        String clientID = InputHelper.getStringInput("Enter Client ID: ");
        Client client = clientController.getClientByID(clientID);
        System.out.println("Leave blank if no desired update.");
        if(client != null){
            System.out.println("Enter new name (Current name: " + client.getName() + "):");
            String newName = scanner.nextLine().trim();
            System.out.println("Enter new email (Current email: " + client.getEmail() + "):");
            String newEmail = scanner.nextLine().trim();
            System.out.println("Enter new phone number (Current Phone Number: " + client.getPhone() + "):");
            String newPhoneNumber = scanner.nextLine().trim();
            System.out.println("Enter new address (Current address: " + client.getAddress() + "):");
            String newAddress = scanner.nextLine().trim();

            client.setName(newName.isEmpty() ? client.getName() : newName);
            client.setEmail(newEmail.isEmpty() ? client.getEmail() : newEmail);
            client.setPhone(newPhoneNumber.isEmpty() ? client.getPhone() : newPhoneNumber);
            client.setAddress(newAddress.isEmpty() ? client.getAddress() : newAddress);

            clientController.updateExistingClient(client);
        }else{
            System.out.println("Client not found!");
        }
    }

    private void deleteClient(){
        showActiveClients();
        String clientID = InputHelper.getStringInput("Enter Client ID: ");
        if(!clientController.deleteClient(clientID)){
            System.out.println("Client still has active contracts/invoices.");
        }else{
            System.out.println("Client marked INACTIVE successfully.");
        }
    }

    private void viewRelatedRecords() {
        showActiveClients();
        String clientID = InputHelper.getStringInput("Enter Client ID: ");
        Map<String, List<?>> relatedRecords = clientController.getRelatedRecords(clientID);

        List<Contract> contracts = (List<Contract>) relatedRecords.get("contracts");
        List<Invoice> invoices = (List<Invoice>) relatedRecords.get("invoices");

        System.out.println("\n=== CONTRACTS for Client " + clientID + " ===");
        if (contracts.isEmpty()) {
            System.out.println("No contracts found.");
        } else {
            for (Contract c : contracts) {
                System.out.println(c.getContractID() + " | " + c.getStartDate() + " → " + c.getEndDate());
            }
        }

        System.out.println("\n=== INVOICES for Client " + clientID + " ===");
        if (invoices.isEmpty()) {
            System.out.println("No invoices found.");
        } else {
            for (Invoice i : invoices) {
                System.out.println(i.getInvoiceId() + " | ₱" + i.getAmount() + " | " + i.getStatus());
            }
        }
    }

    private void showActiveClients(){
        List<Client> clients =  clientController.getAllActiveClients();
        System.out.println("List of active clients: ");
        for (Client client : clients){
            if(client.getStatus() == ClientStatus.ACTIVE){
                System.out.println(String.format("Client ID: %s | Client Name: %s\n", client.getClientId(), client.getName()));
            }
        }
    }

}
