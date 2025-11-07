package Model.DAO;
import Model.Entities.*;
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

    public List<Client> getClientsByBranchID(String branchId) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.clientId, c.name, c.email, c.phone, c.status " +
                "FROM Client c " +
                "JOIN Contract ct ON c.clientId = ct.clientId " +
                "WHERE ct.branchId = ? " +
                "GROUP BY c.clientId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client(
                            rs.getString("clientId"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("status"));
                    client.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
                    clients.add(client);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    public List<Client> getManagersByBranchID(String managerId) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.clientId, c.name, c.email, c.phone, c.status " +
                "FROM Client c " +
                "JOIN Contract ct ON c.clientId = ct.clientId " +
                "WHERE ct.managerId = ? " +
                "GROUP BY c.clientId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client(
                            rs.getString("clientId"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("status"));
                    client.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
                    clients.add(client);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

}