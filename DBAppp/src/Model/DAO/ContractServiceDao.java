package Model.DAO;

import Model.Entities.Contract;
import Model.Entities.ContractService;
import Model.Entities.Invoice;
import Model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractServiceDao {
    // CREATE
    public void addContractService(ContractService contractService) {
        String sql = "INSERT INTO Contract (contractId, serviceId) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractService.getContractID());
            stmt.setString(2, contractService.getServiceID());

            stmt.executeUpdate();
            System.out.println("ContractService successfully added!");

        } catch (SQLException e) {
            System.out.println("Error inserting ContractService record.");
            e.printStackTrace();
        }
    }

    public void deactivateContractServices(String contractId) {
        String sql = "UPDATE ContractService SET status = 'Inactive' WHERE contractId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("All ContractService records for " + contractId + " marked as INACTIVE.");
            } else {
                System.out.println("No ContractService records found for contract ID: " + contractId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error setting contract service inactive: " + e.getMessage());

        }

    }

    public void reactivateContractServices(String contractId) {
        String sql = "UPDATE ContractService SET status = 'Active' WHERE contractId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("All ContractService records for " + contractId + " marked as ACTIVE.");
            } else {
                System.out.println("No ContractService records found for contract ID: " + contractId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error setting contract service active: " + e.getMessage());

        }

    }

    public List<Contract> getContractsByServiceID(String serviceId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.contractId, c.clientId, c.managerId, c.branchId, c.startDate, c.endDate " +
                "FROM ContractService cs " +
                "JOIN Contract c ON cs.contractId = c.contractId " +
                "WHERE cs.serviceId = ? ORDER BY cs.contractId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contracts.add(new Contract(
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getString("managerId"),
                            rs.getString("branchId"),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("startDate").toLocalDate()
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }



    public List<Invoice> getInvoicesByServiceID(String serviceId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT DISTINCT i.invoiceId, i.contractId, i.amount, i.lateFee, i.status " +
                "FROM ContractService cs " +
                "JOIN Invoice i ON i.contractId = cs.contractId " +
                "WHERE cs.serviceId = ? ORDER BY i.invoiceId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            null, // clientId if needed, otherwise null
                            null, null, // invoiceDate and dueDate if needed
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

}
