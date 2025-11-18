package Model.DAO;
import Model.Entities.*;
import Model.util.DBConnection;

import java.math.BigDecimal;
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
                        client.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
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
                        client.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
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

    public List<ClientHistory> getClientHistory(String clientId) {
        List<ClientHistory> clientHistories = new ArrayList<>();

        // Contracts - direct link to Client
        String contractSql = "SELECT contractId, contract_status, YEAR(startDate) AS year FROM Contract WHERE clientId = ?";

        // Invoices - join Contract to get clientId
        String invoiceSql = """
        SELECT i.invoiceId, i.status, i.amount, YEAR(i.invoiceDate) AS year
        FROM Invoice i
        JOIN Contract c ON i.contractId = c.contractId
        WHERE c.clientId = ?
    """;

        // Payments - join Invoice -> Contract to get clientId
        String paymentSql = """
        SELECT p.paymentId, p.payment_status, p.amount, YEAR(p.paymentDate) AS year
        FROM Payment p
        JOIN Invoice i ON p.invoiceId = i.invoiceId
        JOIN Contract c ON i.contractId = c.contractId
        WHERE c.clientId = ?
    """;

        try (Connection conn = DBConnection.getConnection()) {
            Map<Integer, List<String>> contractsPerYear = new HashMap<>();
            Map<Integer, List<String>> invoicesPerYear = new HashMap<>();
            Map<Integer, BigDecimal> invoiceTotalsPerYear = new HashMap<>();
            Map<Integer, List<String>> paymentsPerYear = new HashMap<>();
            Map<Integer, BigDecimal> paymentTotalsPerYear = new HashMap<>();

            // Contracts
            try (PreparedStatement ps = conn.prepareStatement(contractSql)) {
                ps.setString(1, clientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int year = rs.getInt("year");
                        String info = rs.getString("contractId") + " (" + rs.getString("contract_status") + ")";
                        contractsPerYear.computeIfAbsent(year, k -> new ArrayList<>()).add(info);
                    }
                }
            }

            // Invoices
            try (PreparedStatement ps = conn.prepareStatement(invoiceSql)) {
                ps.setString(1, clientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int year = rs.getInt("year");
                        String info = rs.getString("invoiceId") + " (" + rs.getString("status") + ")";
                        invoicesPerYear.computeIfAbsent(year, k -> new ArrayList<>()).add(info);

                        BigDecimal amount = rs.getBigDecimal("amount");
                        invoiceTotalsPerYear.put(year, invoiceTotalsPerYear.getOrDefault(year, BigDecimal.ZERO).add(amount));
                    }
                }
            }

            // Payments
            try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                ps.setString(1, clientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int year = rs.getInt("year");
                        String info = rs.getString("paymentId") + " (" + rs.getString("payment_status") + ")";
                        paymentsPerYear.computeIfAbsent(year, k -> new ArrayList<>()).add(info);

                        BigDecimal amount = rs.getBigDecimal("amount");
                        paymentTotalsPerYear.put(year, paymentTotalsPerYear.getOrDefault(year, BigDecimal.ZERO).add(amount));
                    }
                }
            }

            // Combine per year
            Set<Integer> allYears = new HashSet<>();
            allYears.addAll(contractsPerYear.keySet());
            allYears.addAll(invoicesPerYear.keySet());
            allYears.addAll(paymentsPerYear.keySet());

            for (int year : allYears) {
                clientHistories.add(new ClientHistory(
                        clientId,
                        year,
                        String.join(", ", contractsPerYear.getOrDefault(year, List.of())),
                        String.join(", ", invoicesPerYear.getOrDefault(year, List.of())) +
                                " | Total: ₱" + invoiceTotalsPerYear.getOrDefault(year, BigDecimal.ZERO),
                        String.join(", ", paymentsPerYear.getOrDefault(year, List.of())) +
                                " | Total: ₱" + paymentTotalsPerYear.getOrDefault(year, BigDecimal.ZERO)
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientHistories;
    }

    public int getActiveClientsCount(){
        String sql = "SELECT COUNT(*) as count FROM Client WHERE status = 'Active'";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            if(rs.next()){
                return rs.getInt("count");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return 0;
    }
}