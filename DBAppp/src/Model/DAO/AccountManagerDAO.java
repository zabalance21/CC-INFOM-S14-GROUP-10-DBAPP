package Model.DAO;

import Model.Entities.AccountManager;
import Model.Entities.ManagerStatus;
import Model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountManagerDAO {
    // CREATE
    public void addManager(AccountManager manager){
        String sql = "INSERT INTO AccountManager (managerId, name, contactInfo, branchId) VALUES (?,?,?,?)";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, manager.getManagerID());
            stmt.setString(2, manager.getName());
            stmt.setString(3, manager.getContactInfo());
            stmt.setString(4, manager.getBranchID());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // READ - gets a single client by ID
    public AccountManager getManagerByID(String managerID){
        String sql = "SELECT * FROM AccountManager WHERE managerId = ?";
        AccountManager manager = null;
        try(Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, managerID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                manager = new AccountManager(rs.getString("managerId"),
                        rs.getString("name"),
                        rs.getString("contactInfo"),
                        rs.getString("managerId"));
                ManagerStatus.valueOf(rs.getString("employment_status").toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manager;
    }

    // READ - receives all manager
    public List<AccountManager> getAllManagers(){
        List<AccountManager > managers = new ArrayList<>();
        String sql = "SELECT * FROM AccountManager";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();) {

            while(rs.next()) {
                AccountManager manager = new AccountManager(
                        rs.getString("managerId"),
                        rs.getString("name"),
                        rs.getString("contactInfo"),
                        rs.getString("branchId"));
                manager.setStatus(ManagerStatus.valueOf(rs.getString("employment_status").toUpperCase()));
                managers.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managers;
    }

    // UPDATE
    public void updateManagers(AccountManager manager){
        String sql = "UPDATE AccountManager SET name  = ?, contactInfo = ? WHERE managerId = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, manager.getName());
            stmt.setString(2, manager.getContactInfo());
            stmt.setString(3, manager.getManagerID());
            stmt.executeUpdate();
            System.out.println("Branch info updated successfully.");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Remove
    public boolean removeManager(String managerId) {
        String sql = "UPDATE AccountManager SET employment_status = 'Resigned' WHERE managerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, managerId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Manager marked as Resigned successfully.");
                return true;
            } else {
                System.out.println("No client found with ID: " + managerId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // View Related Records part
    public void viewRelatedRecords(String managerId) {
        String clientSql = "SELECT DISTINCT c.clientId, c.name, c.email,c.phone, c.status " +
                "FROM Client c " +
                "JOIN Contract ct ON c.clientId = ct.clientId " +
                "WHERE ct.managerId = ?";

        String managerSql = "SELECT contractId, clientId, branchId, startDate, endDate, contract_status " +
                "FROM Contract WHERE managerId = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // ---- Clients Section ----
            System.out.println("\n=== Clients assigned to Manager " + managerId + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(clientSql)) {
                stmt.setString(1, managerId);
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
                    System.out.println("This manager has no clients yet.");
                }
            }

            // ---- Managers Section ----
            System.out.println("\n=== Contracts handled by Manager " + managerId + " ===");
            try (PreparedStatement stmt = conn.prepareStatement(managerSql)) {
                stmt.setString(1, managerId);
                ResultSet rs = stmt.executeQuery();

                boolean hasManagers = false;
                while (rs.next()) {
                    hasManagers = true;
                    System.out.println("Contract ID: " + rs.getString("contractId") +
                            " | Client ID: " + rs.getString("clientId") +
                            " | Branch ID: " + rs.getString("branchId") +
                            " | Start Date: " + rs.getString("startDate") +
                            " | End Date: " + rs.getString("endDate") +
                            " | Status: " + rs.getString("contract_status"));
                }
                if (!hasManagers) {
                    System.out.println("This manager does not handle any contracts yet.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
