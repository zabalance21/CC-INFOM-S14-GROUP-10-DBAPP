package Model.DAO;
import Model.Entities.Client;
import Model.Entities.ClientStatus;
import Model.util.DBConnection;
import java.sql.*;
import java.util.*;


public class ClientDAO {  // FOR SQL CLIENT TABLE QUERIES

    // CREATE
    public void addClient(Client client){
        String sql = "INSERT INTO Client (clientID, name, email, phone, address) VALUES (?,?,?,?,?)";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, client.getClientId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getEmail());
            stmt.setString(4, client.getPhone());
            stmt.setString(5, client.getAddress());
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
                        ClientStatus.valueOf(rs.getString("status").toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }

    public Client getClientByName(String clientName) {
        String sql = "SELECT * FROM Client WHERE LOWER(name) = LOWER(?)";
        Client client = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, clientName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                client = new Client(
                        rs.getString("clientID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"));
                        ClientStatus.valueOf(rs.getString("status").toUpperCase());
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
                client.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
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
    public boolean deleteClient(String clientId) {
        String sql = "UPDATE Client SET status = 'Inactive' WHERE clientId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, clientId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Client marked as INACTIVE.");
                return true;
            } else {
                System.out.println("No client found with ID: " + clientId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Checks if client exists
    public boolean checkClientExists(String clientName) {
        String sql = "SELECT COUNT(*) FROM Client WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // View Related Records part
    public void viewRelatedRecords(String clientID) {
        String contractSql = "SELECT c.contractId, c.startDate, c.endDate, b.name AS branchName, am.name AS managerName " +
                "FROM Contract c " +
                "JOIN Branch b ON c.branchId = b.branchId " +
                "JOIN AccountManager am ON c.managerId = am.managerId " +
                "WHERE c.clientId = ?";

        String invoiceSql = "SELECT invoiceId, invoiceDate, dueDate, amount, status " +
                "FROM Invoice WHERE clientId = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // ---- Contracts Section ----
            System.out.println("\n=== CONTRACTS for Client " + clientID + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(contractSql)) {
                stmt.setString(1, clientID);
                ResultSet rs = stmt.executeQuery();

                boolean hasContracts = false;
                while (rs.next()) {
                    hasContracts = true;
                    System.out.println("Contract ID: " + rs.getString("contractId") +
                            " | Manager: " + rs.getString("managerName") +
                            " | Branch: " + rs.getString("branchName") +
                            " | Start: " + rs.getDate("startDate") +
                            " | End: " + rs.getDate("endDate"));
                }
                if (!hasContracts) {
                    System.out.println("No contracts found for this client.");
                }
            }

            // ---- Invoices Section ----
            System.out.println("\n=== INVOICES for Client " + clientID + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(invoiceSql)) {
                stmt.setString(1, clientID);
                ResultSet rs = stmt.executeQuery();

                boolean hasInvoices = false;
                while (rs.next()) {
                    hasInvoices = true;
                    System.out.println("Invoice ID: " + rs.getString("invoiceId") +
                            " | Date: " + rs.getDate("invoiceDate") +
                            " | Due: " + rs.getDate("dueDate") +
                            " | Amount: " + rs.getBigDecimal("amount") +
                            " | Status: " + rs.getString("status"));
                }
                if (!hasInvoices) {
                    System.out.println("No invoices found for this client.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}