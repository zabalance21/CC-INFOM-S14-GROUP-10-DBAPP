package Model.DAO;

import Model.Entities.Branch;
import Model.Entities.BranchStatus;
import Model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {
    // CREATE
    public void addBranch(Branch branch){
        String sql = "INSERT INTO Branch (branchId, name, address, city, contactNumber) VALUES (?,?,?,?,?)";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, branch.getBranchID());
            stmt.setString(2, branch.getName());
            stmt.setString(3, branch.getAddress());
            stmt.setString(4, branch.getCity());
            stmt.setString(5, branch.getContactNumber());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean branchAddressExists(String address) {
        String sql = "SELECT COUNT(*) FROM Branch WHERE address = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // true if address already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // READ - gets a single client by ID
    public Branch getBranchByID(String branchID){
        String sql = "SELECT * FROM Branch WHERE branchId = ?";
        Branch branch = null;
        try(Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branchID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                branch = new Branch(rs.getString("branchId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("contactNumber"));
                BranchStatus.valueOf(rs.getString("status").toUpperCase());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branch;
    }

    // READ - receives all branches
    public List<Branch> getAllBranches(){
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT * FROM Branch";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();) {

            while(rs.next()) {
                Branch branch = new Branch(
                        rs.getString("branchId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("contactNumber"));
                branch.setStatus(BranchStatus.valueOf(rs.getString("status").toUpperCase()));
                branches.add(branch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    // UPDATE
    public void updateBranch(Branch branch){
        String sql = "UPDATE Branch SET name  = ?, address = ?, city = ?, contactNumber = ? , status = ? WHERE branchId = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getCity());
            stmt.setString(4, branch.getContactNumber());
            stmt.setString(5, branch.getStatus().name());
            stmt.setString(6, branch.getBranchID());
            stmt.executeUpdate();
            System.out.println("Branch info updated successfully.");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Remove
    public boolean removeBranch(String branchId) {
        String sql = "UPDATE Branch SET status = 'Closed' WHERE branchId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branchId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Branch marked as CLOSED.");
                return true;
            } else {
                System.out.println("No client found with ID: " + branchId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Branch> getAllOperationalBranches() {
        List<Branch> branches = new ArrayList<>();
        String sql = "SELECT branchId, name, address, city, contactNumber, status " +
                "FROM Branch WHERE status = 'Operational' ORDER BY branchId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                Branch branch = new Branch(
                        rs.getString("branchId"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("contactNumber"));
                branch.setStatus(BranchStatus.valueOf(rs.getString("status").toUpperCase()));
                branches.add(branch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branches;
    }
}
