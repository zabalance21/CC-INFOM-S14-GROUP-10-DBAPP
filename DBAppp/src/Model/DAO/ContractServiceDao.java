package Model.DAO;

import Model.Entities.*;
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
        String sql = "INSERT INTO ContractService (csId, contractId, serviceId) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contractService.getContractServiceID());
            stmt.setString(2, contractService.getContractID());
            stmt.setString(3, contractService.getServiceID());

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

    public void reactivateContractServices(String csId) {
        String sql = "UPDATE ContractService SET status = 'Active' WHERE contractId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, csId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("All ContractService records for " + csId + " marked as ACTIVE.");
            } else {
                System.out.println("No ContractService records found for contract ID: " + csId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error setting contract service active: " + e.getMessage());

        }

    }

    public List<ContractService> getAllInactiveContractServices() {
        List<ContractService> contractServiceList = new ArrayList<>();
        String sql = "SELECT * FROM ContractService WHERE status = 'Inactive'";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                ContractService cs = new ContractService(
                        rs.getString("csId"),
                        rs.getString("serviceId"),
                        rs.getString("contractId")
                );
                cs.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase())); // map status enum
                contractServiceList.add(cs);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return contractServiceList;
    }

    public ContractService getContractServiceById(String csId) {
        String sql = "SELECT * FROM ContractService WHERE csId = ?";
        ContractService contractService = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, csId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contractService = new ContractService(
                            rs.getString("csId"),
                            rs.getString("serviceId"),
                            rs.getString("contractId")
                    );
                    contractService.setStatus(ClientStatus.valueOf(rs.getString("status").toUpperCase()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contractService;
    }

    public List<ContractService> getContractServicesByContractId(String contractId) {
        List<ContractService> contractService = new ArrayList<>();
        String sql = "SELECT * FROM ContractService WHERE contractId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contractService.add(new ContractService(
                            rs.getString("csId"),
                            rs.getString("serviceId"),
                            rs.getString("contractId"),
                            ClientStatus.valueOf(rs.getString("status").toUpperCase())
                    ));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contractService;
    }

    public Contract getContractByContractServiceId(String csId) {
        String sql = "SELECT c.* FROM Contract c " +
                "JOIN ContractService cs ON c.contractId = cs.contractId " +
                "WHERE cs.csId = ?";
        Contract contract = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, csId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contract = new Contract(
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getString("managerId"),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate());
                    contract.setContractStatus(ContractStatus.valueOf(rs.getString("contract_status").toUpperCase()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contract;
    }

    public List<Contract> getContractsByServiceID(String serviceId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.contractId, c.clientId, c.managerId, c.startDate, c.endDate " +
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
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate()
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
