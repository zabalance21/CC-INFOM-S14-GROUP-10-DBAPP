package View;

import Controller.ClientController;
import Model.Entities.Client;

import java.util.Scanner;

public class ClientView {
    private ClientController clientController;
    private Scanner scanner = new Scanner(System.in);

    public ClientView(){
        clientController = new ClientController();
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
            System.out.println("Enter your choice: ");
            choice = Integer.parseInt(scanner.nextLine());

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
            System.out.println("Enter name: ");
            String name = scanner.nextLine();
            System.out.println("Enter email: ");
            String email = scanner.nextLine();
            System.out.println("Enter phone number: ");
            String phoneNumber = scanner.nextLine();
            System.out.println("Enter address: ");
            String address = scanner.nextLine();

            Client client = new Client(name, email, phoneNumber, address);
            clientController.addClient(client);
        }catch(IllegalStateException e){
            System.out.println(e.getMessage());
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    private void viewClientByID(){
        clientController.showAllClients();
        System.out.println("Enter Client ID: ");
        String clientID = scanner.nextLine();
        Client client = clientController.getClientByID(clientID);

        if(client != null){
            System.out.println(client.toString());
        }else{
            System.out.println("Client not found!");
        }
    }

    private void updateClient(){
        clientController.showAllClients();
        System.out.println("Enter Client ID: ");
        String clientID = scanner.nextLine();
        Client client = clientController.getClientByID(clientID);
        System.out.println("Leave fields blank if you want to retain those information.");
        if(client != null){
            System.out.println("Enter new name (Current name: " + client.getName() + "):");
            String newName = scanner.nextLine();
            System.out.println("Enter new email (Current email: " + client.getEmail() + "):");
            String newEmail = scanner.nextLine();
            System.out.println("Enter new phone number (Current Phone Number: " + client.getPhone() + "):");
            String newPhoneNumber = scanner.nextLine();
            System.out.println("Enter new address (Current address: " + client.getAddress() + "):");
            String newAddress = scanner.nextLine();

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
        clientController.showAllClients();
        System.out.println("Enter Client ID to delete: ");
        String clientID = scanner.nextLine();
        clientController.deleteClient(clientID);
    }

    private void viewRelatedRecords(){
        clientController.showAllClients();
        System.out.println("Enter Client ID: ");
        String clientID = scanner.nextLine();
        clientController.viewRelatedRecords(clientID);
    }
}
