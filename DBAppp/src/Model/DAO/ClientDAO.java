package Model.DAO;
import Model.Entities.Client;
import Model.Entities.ClientStatus;
import Model.util.DBConnection;
import java.sql.*;
import java.util.*;


public class ClientDAO {  // FOR SQL CLIENT TABLE QUERIES

    // CREATE
    public void addClient(Client client){
        String sql = "INSERT INTO Client (clientID, name, email, phone, address, status) VALUES (?,?,?,?,?,?)";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, client.getClientId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
            stmt.setString(6, client.getStatus().name());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // READ - gets a single client by ID
    public Client getClientByID(String clientID){
        String sql = "SELECT * FROM Client WHERE clientID = ?";
        Client client = null;
        try(Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clientID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                client = new Client(rs.getString("clientID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"));
                        client.setStatus(ClientStatus.valueOf(rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }

    // READ - receives all clientele
    public List<Client> getAllClients(){
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();) {

            while(rs.next()) {
                Client client = new Client(
                        rs.getString("clientID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"));
                client.setStatus(ClientStatus.valueOf(rs.getString("status")));
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // UPDATE
    public void updateClient(Client client){
        String sql = "UPDATE Client SET name  = ?, email = ?, phone = ?, address = ? , status = ? WHERE clientID = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getAddress());
            stmt.setString(5, client.getStatus().name());
            stmt.setString(6, client.getClientId());
            stmt.executeUpdate();
            System.out.println("Client updated successfully.");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // DELETE
    public void deleteClient(String clientID){
        String sql = "UPDATE Client SET status = ? WHERE clientID = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ClientStatus.INACTIVE.name());
            stmt.setString(2, clientID);
            stmt.executeUpdate();
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Client marked as INACTIVE.");
            } else {
                System.out.println("No client found with ID: " + clientID);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // View Related Records part
    public void viewRelatedRecords(String clientID){
        String contractSql = "SELECT * FROM Contract WHERE clientID = ?";
        String invoiceSql = "SELECT * FROM Invoice WHERE clientID = ?";
        try(Connection conn = DBConnection.getConnection()){
            // Contracts
            try(PreparedStatement stmt = conn.prepareStatement(contractSql)){
                stmt.setString(1, clientID);
                ResultSet rs = stmt.executeQuery();
                System.out.println("Contracts for client  "+ clientID + ":");
                while(rs.next()){
                    System.out.println("Contract ID: " + rs.getString("contractID") +
                                        ", Start: " + rs.getDate("startDate") +
                                        ", End: " + rs.getDate("endDate"));
                }
            }

            // Invoices
            try(PreparedStatement stmt = conn.prepareStatement(invoiceSql)){
                stmt.setString(1, clientID);
                ResultSet rs = stmt.executeQuery();
                System.out.println("Invoices for client  "+ clientID + ":");
                while(rs.next()) {
                    System.out.println("Invoice ID: " + rs.getString("invoiceID") +
                                       ", Amount: " + rs.getBigDecimal("amount") +
                                        ", Status: " + rs.getString("status"));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
