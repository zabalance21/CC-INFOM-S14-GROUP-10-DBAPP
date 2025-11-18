package Model.DAO;

import Model.Entities.Service;
import Model.Entities.ServiceAvailability;
import Model.util.DBConnection;

import java.sql.*;
import java.util.*;

public class ServiceDAO {

    private String nextServiceId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT serviceId FROM Service WHERE serviceId LIKE 'SV-%' ORDER BY serviceId DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = Integer.parseInt(rs.getString(1).substring(3)) + 1;
            return String.format("SV-%03d", next);
        }
    }

    public void addService(Service s) {
        String sql = "INSERT INTO Service(serviceId, name, description, rate, availability) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (s.getServiceId() == null || s.getServiceId().isBlank()) {
                s.setServiceId(nextServiceId(conn));
            }

            ps.setString(1, s.getServiceId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDescription());
            ps.setBigDecimal(4, s.getRate());
            ps.setString(5, s.getAvailability());
            ps.executeUpdate();

            System.out.println("Service added: " + s.getServiceId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Checks if service exists
    public boolean checkServiceExists(String serviceName) {
        String sql = "SELECT COUNT(*) FROM Client WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serviceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Service getServiceById(String id) {
        String sql = "SELECT * FROM Service WHERE serviceId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Service(
                            rs.getString("serviceId"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("rate"),
                            rs.getString("availability")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Service> getAllServices() {
        List<Service> out = new ArrayList<>();
        String sql = "SELECT * FROM Service ORDER BY serviceId";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Service(
                        rs.getString("serviceId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("rate"),
                        rs.getString("availability")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public List<Service> getAvailableServicesOnly() {
        List<Service> out = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE availability='Available' ORDER BY serviceId";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Service(
                        rs.getString("serviceId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("rate"),
                        rs.getString("availability")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public void updateService(Service s) {
        String sql = "UPDATE Service SET name=?, description=?, rate=?, availability=? WHERE serviceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getDescription());
            ps.setBigDecimal(3, s.getRate());
            ps.setString(4, s.getAvailability());
            ps.setString(5, s.getServiceId());

            ps.executeUpdate();
            System.out.println("Service updated: " + s.getServiceId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteService(String serviceId) {
        String sql = "UPDATE Service SET availability = ? WHERE serviceId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ServiceAvailability.UNAVAILABLE.db());
            stmt.setString(2, serviceId);
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("Service " + serviceId + " marked as UNAVAILABLE.");
            else
                System.out.println("No service found with ID: " + serviceId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getInactiveServiceCount(){
        String sql = "SELECT COUNT(*) as count FROM Service WHERE availability IN ('Unavailable', 'Discontinued')";

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

    public int getAvailableServiceCount(){
        String sql = "SELECT COUNT(*) as count FROM Service WHERE availability = 'Available'";

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
