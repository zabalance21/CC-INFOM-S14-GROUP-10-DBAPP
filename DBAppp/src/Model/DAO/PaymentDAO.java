package Model.DAO;

import Model.Entities.Contract;
import Model.Entities.Payment;
import Model.util.DBConnection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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


}
