package Controller;

import Model.DAO.ClientDAO;
import Model.Entities.Client;

import java.util.List;

public class ClientController {
    private ClientDAO clientDAO;

    public ClientController(){
        this.clientDAO = new ClientDAO();
    }

    // Create a new client
    public void addClient(Client client){
        clientDAO.addClient(client);
    }

    // Retrieve a client by ID
    public Client getClientByID(String id) {
        return clientDAO.getClientByID(id);
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
}
