package Controller;

import Model.DAO.ClientDAO;
import Model.DAO.ContractDAO;
import Model.DAO.InvoiceDAO;
import Model.Entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientController {
    private ClientDAO clientDAO;
    private ContractDAO contractDAO;
    private InvoiceDAO invoiceDAO;
    public ClientController( ContractDAO contractDAO, InvoiceDAO invoiceDAO, ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
        this.invoiceDAO = invoiceDAO;
        this.contractDAO = contractDAO;

    }

    // Create a new client
    public boolean addClient(Client client){
        if(clientDAO.checkClientExists(client.getName())){
            return false;
        }
        clientDAO.addClient(client);
        return true;
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
    public boolean deleteClient(String id){
        if(invoiceDAO.hasActiveInvoicesForClient(id)|| contractDAO.hasActiveContractsForClient(id)){
            return false;
        }
        clientDAO.deleteClient(id);
        return true;
    }

    public List<Client> getAllClients(){
        return clientDAO.getAllClients();
    }

    public Map<String, List<?>> getRelatedRecords(String clientID) {
        Map<String, List<?>> relatedRecords = new HashMap<>();
        List<Contract> contracts = contractDAO.getContractsByClientId(clientID);
        List<Invoice> invoices = invoiceDAO.getInvoicesByClientID(clientID);
        relatedRecords.put("contracts", contracts);
        relatedRecords.put("invoices", invoices);
        return relatedRecords;
    }


    // Prints all clients
    public void showAllClients(){
        List<Client> clients = clientDAO.getAllClients();
        for (Client client : clients){
            System.out.println(client.toString());
        }
    }

    // Returns all active clients
    public List<Client> getAllActiveClients(){
        List<Client> clients = clientDAO.getAllClients();
        List<Client> filteredClients = new ArrayList<>();
        for (Client client : clients){
            if(client.getStatus() == ClientStatus.ACTIVE){
                filteredClients.add(client);
            }
        }
        return filteredClients;
    }

    public List<ClientHistory> getClientHistory(String clientID) {
        return clientDAO.getClientHistory(clientID);
    }

    public Client getClientByInvoice(Invoice invoice){
        // Defensive: if invoice or its id is null, we can't find a client
        if (invoice == null || invoice.getInvoiceId() == null) return null;

        List<Client> clients = getAllClients();
        for (Client client : clients) {
            List<Invoice> clientInvoices = invoiceDAO.getInvoicesByClientID(client.getClientId());
            if (clientInvoices == null) continue;
            for (Invoice inv : clientInvoices) {
                if (inv != null && invoice.getInvoiceId().equals(inv.getInvoiceId())) {
                    return client;
                }
            }
        }
        return null;
    }
}
