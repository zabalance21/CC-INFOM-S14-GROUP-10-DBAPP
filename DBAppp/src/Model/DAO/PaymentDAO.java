package Model.DAO;

import Model.Entities.Payment;
import Model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentDAO {

    // CREATE
    public void addPayment(Payment payment) {
        String sql = "INSERT INTO Payment (paymentId, invoiceId, paymentDate, amount, referenceNumber) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getInvoiceId());
            stmt.setDate(3,  java.sql.Date.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(4,payment.getAmount());
            stmt.setString(5, payment.getReceiptNumber());

            stmt.executeUpdate();
            System.out.println("Payment successfully added!");

        } catch (SQLException e) {
            System.out.println("Error inserting payment record.");
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, BigDecimal>> getMonthlyCollectionsPerClient() {
        String sql =
                "SELECT c.name AS clientName, " +
                        "       YEAR(p.paymentDate) AS year, " +
                        "       MONTH(p.paymentDate) AS month, " +
                        "       SUM(p.amount) AS totalAmount " +
                        "FROM Client c " +
                        "LEFT JOIN Contract ct ON c.clientId = ct.clientId " +
                        "LEFT JOIN Invoice i ON ct.contractId = i.contractId " +
                        "LEFT JOIN Payment p ON i.invoiceId = p.invoiceId " +
                        "GROUP BY c.name, YEAR(p.paymentDate), MONTH(p.paymentDate) " +
                        "ORDER BY c.name, YEAR(p.paymentDate), MONTH(p.paymentDate)";

        Map<String, Map<String, BigDecimal>> report = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String clientName = rs.getString("clientName");
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                BigDecimal totalAmount = rs.getBigDecimal("totalAmount");

                report.putIfAbsent(clientName, new LinkedHashMap<>());

                if (totalAmount != null) {
                    String monthYear = String.format("%02d/%d", month, year);
                    report.get(clientName).put(monthYear, totalAmount);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }


    public Map<String, Map<String, BigDecimal>> getQuarterlyRevenuePerService() {
        String sql = """
        SELECT s.name AS serviceName,
               YEAR(p.paymentDate) AS year,
               CEIL(MONTH(p.paymentDate)/3.0) AS quarter,
               SUM(p.amount) AS totalAmount
        FROM Service s
        LEFT JOIN ContractService cs ON s.serviceId = cs.serviceId
        LEFT JOIN Invoice i ON cs.contractId = i.contractId
        LEFT JOIN Payment p ON p.invoiceId = i.invoiceId
        GROUP BY s.name, YEAR(p.paymentDate), CEIL(MONTH(p.paymentDate)/3.0)
        ORDER BY s.name, YEAR(p.paymentDate), quarter
    """;

        Map<String, Map<String, BigDecimal>> quarterlyReport = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String serviceName = rs.getString("serviceName");
                int year = rs.getInt("year");
                int quarter = rs.getInt("quarter");
                BigDecimal totalAmount = rs.getBigDecimal("totalAmount");

                // Skip null years (no payments yet)
                if (year == 0) continue;

                String quarterLabel = year + "-Q" + quarter;

                quarterlyReport.putIfAbsent(serviceName, new LinkedHashMap<>());
                quarterlyReport.get(serviceName).put(quarterLabel,
                        totalAmount != null ? totalAmount : BigDecimal.ZERO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quarterlyReport;
    }

    public List<Payment> getAllPayments(){
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                Payment payment = new Payment(
                    rs.getString("paymentID"),
                    rs.getString("invoiceId"),
                    rs.getDate("paymentDate").toLocalDate(),
                    rs.getBigDecimal("amount"),
                    rs.getString("referencedNumber")
                );
                payments.add(payment);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return payments;
    }

    public List<Payment> getRecentPayments(int days){
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE paymentDate >= DATE_SUB(CURDATE(), INTERVAL ? DAY) ORDER BY paymentDate DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, days);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    Payment payment = new Payment(
                        rs.getString("paymentID"),
                        rs.getString("invoiceId"),
                        rs.getDate("paymentDate").toLocalDate(),
                        rs.getBigDecimal("amount"),
                        rs.getString("referenceNumber")
                    );
                    payments.add(payment);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return payments;
    }

    public BigDecimal getMonthlyRevenue(){
        String sql = "SELECT SUM(amount) as total FROM Payment WHERE MONTH(paymentDate) = MONTH(CURDATE()) AND YEAR(paymentDate) = YEAR(CURDATE())";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            
            if(rs.next()){
                return rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getMonthlyRevenue(int month, int year){
        String sql = "SELECT SUM(amount) as total FROM Payment WHERE MONTH(paymentDate) = ? AND YEAR(paymentDate) = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, month);
            stmt.setInt(2, year);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    BigDecimal revenue = rs.getBigDecimal("total");
                    return revenue != null ? revenue : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

        public String getNextAvailablepaymentId() {
        String sql = "SELECT MAX(paymentId) FROM Payment WHERE paymentId LIKE 'PM-%'";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId != null) {
                    // Extract number from "PM-003" 
                    int lastNumber = Integer.parseInt(maxId.substring(3));
                    int nextNumber = lastNumber + 1;
                    
                    //CHECK MAX LIMIT
                    if (nextNumber > 1000) {
                        throw new IllegalStateException("Maximum payment limit reached (999 contracts services). Cannot proceed with payment");
                    }
                    
                    return String.format("PM-%03d", nextNumber);
                }
            }
            // No contract service exist yet, start from PM-001
            return "PM-001";
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
