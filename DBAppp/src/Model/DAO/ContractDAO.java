package Model.DAO;
import Model.Entities.Contract;
import Model.Entities.ContractStatus;
import Model.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
    // CREATE
    public void addContract(Contract contract) {
        String sql = "INSERT INTO Contract (contractId, clientId, managerId, branchId, startDate, endDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contract.getContractID());
            stmt.setString(2, contract.getClientID());
            stmt.setString(3, contract.getManagerID());
            stmt.setString(4, contract.getBranchID());
            stmt.setDate(5, java.sql.Date.valueOf(contract.getStartDate())); // if contract.getStartDate() returns LocalDate
            stmt.setDate(6, java.sql.Date.valueOf(contract.getEndDate()));   // same here

            stmt.executeUpdate();
            System.out.println("Contract successfully added!");

        } catch (SQLException e) {
            System.out.println("Error inserting contract record.");
            e.printStackTrace();
        }
    }


    // READ - gets a single client by ID
    public Contract getContractByID(String contractID) {
        String sql = "SELECT * FROM Contract WHERE contractId = ?";
        Contract contract = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                contract = new Contract(
                        rs.getString("contractId"),
                        rs.getString("clientId"),
                        rs.getString("managerId"),
                        rs.getString("branchId"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contract;
    }

    // READ - Get all contracts
    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM Contract";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contract contract = new Contract(
                        rs.getString("contractId"),
                        rs.getString("clientId"),
                        rs.getString("managerId"),
                        rs.getString("branchId"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate()
                );
                contract.setContractStatus(ContractStatus.valueOf(rs.getString("contract_status").toUpperCase()));
                contracts.add(contract);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    // UPDATE - Update an existing contract
    public void updateContract(Contract contract) {
        String sql = "UPDATE Contract SET clientId = ?, managerId = ?, branchId = ?, startDate = ?, endDate = ?, terms = ?, contract_status = ? WHERE contractId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contract.getClientID());
            stmt.setString(2, contract.getManagerID());
            stmt.setString(3, contract.getBranchID());
            stmt.setDate(4, Date.valueOf(contract.getStartDate()));
            stmt.setDate(5, Date.valueOf(contract.getEndDate()));
            stmt.setString(6, contract.getTerms());
            stmt.setString(7, contract.getContractStatus().name());
            stmt.setString(8, contract.getContractID());

            stmt.executeUpdate();
            System.out.println("Contract updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // SOFT DELETE - Mark contract as Closed
    public boolean closeContract(String contractId) {
        String sql = "UPDATE Contract SET contract_status = 'Closed' WHERE contractId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Contract marked as CLOSED.");
                return true;
            } else {
                System.out.println("No contract found with ID: " + contractId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // View Related Records part
    public void viewRelatedRecords(String branchId) {
        String clientSql = "SELECT c.clientId, c.name, c.email,c.phone, c.status " +
                "FROM Client c " +
                "JOIN Contract ct ON c.clientId = ct.clientId " +
                "WHERE ct.branchId = ?" +
                "GROUP BY c.clientId";

        String managerSql = "SELECT managerId, name, position, contactInfo, employment_status " +
                "FROM AccountManager WHERE branchId = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // ---- Clients Section ----
            System.out.println("\n=== Clients under Branch " + branchId + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(clientSql)) {
                stmt.setString(1, branchId);
                ResultSet rs = stmt.executeQuery();

                boolean hasClients = false;
                while (rs.next()) {
                    hasClients = true;
                    System.out.println("Client ID: " + rs.getString("clientId") +
                            " | Name: " + rs.getString("name") +
                            " | Email: " + rs.getString("email") +
                            " | Phone: " + rs.getString("phone") +
                            " | Status: " + rs.getString("status"));
                }
                if (!hasClients) {
                    System.out.println("No clients found for this branch.");
                }
            }

            // ---- Managers Section ----
            System.out.println("\n=== Account Managers under Branch " + branchId + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(managerSql)) {
                stmt.setString(1, branchId);
                ResultSet rs = stmt.executeQuery();

                boolean hasManagers = false;
                while (rs.next()) {
                    hasManagers = true;
                    System.out.println("Manager ID: " + rs.getString("managerId") +
                            " | Name: " + rs.getString("name") +
                            " | Position: " + rs.getString("position") +
                            " | Contact: " + rs.getString("contactInfo") +
                            " | Status: " + rs.getString("employment_status"));
                }
                if (!hasManagers) {
                    System.out.println("No managers found for this branch.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
