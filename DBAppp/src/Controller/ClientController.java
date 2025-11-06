package Controller;

import Model.DAO.ClientDAO;
import Model.Entities.AccountManager;
import Model.Entities.Client;
import Model.Entities.ClientStatus;
import Model.Entities.ManagerStatus;

import java.util.List;

public class ClientController {
    private ClientDAO clientDAO;

    public ClientController(){
        this.clientDAO = new ClientDAO();
    }

    // Create a new client
    public void addClient(Client client){
        if(clientDAO.checkClientExists(client.getName())){
            System.out.println("Client already exists");
        }else{
            clientDAO.addClient(client);
            System.out.println("Client added successfully");
        }
    }

    // Retrieve a client by ID
    public Client getClientByID(String id) {
        return clientDAO.getClientByID(id);
    }

    public Client getClientByName(String name) {
        return clientDAO.getClientByName(name);
    }

    // Update an existing client
    public void updateExistingClient(Client client){
        clientDAO.updateClient(client);
    }

    // Delete a client by ID
    public void deleteClient(String id){
        clientDAO.deleteClient(id);
    }

    // View related records
    public void viewRelatedRecords(String id){
        clientDAO.viewRelatedRecords(id);
    }

    // Prints all clients
    public void showAllClients(){
        List<Client> clients = clientDAO.getAllClients();
        for (Client client : clients){
            System.out.println(client.toString());
        }
    }

    // Prints all active clients
    public void showAllActiveClients(){
        List<Client> clients = clientDAO.getAllClients();
        System.out.println("List of operational branches: ");
        for (Client client : clients){
            if(client.getStatus() == ClientStatus.ACTIVE){
                System.out.println(String.format("Client ID: %s | Client Name: %s\n", client.getClientId(), client.getName()));
            }
        }
    }

    // Returns all active clients
    public List<Client> getAllActiveClients(){
        List<Client> clients = clientDAO.getAllClients();
        for (Client client : clients){
            if(client.getStatus() == ClientStatus.ACTIVE){
                clients.add(client);
            }
        }
        return clients;
    }
}
