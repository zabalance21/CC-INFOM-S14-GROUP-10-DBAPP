package Model.DAO;

import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.InvoiceStatus;
import Model.Entities.OutstandingInvoiceReport;
import Model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDAO {

    private String nextInvoiceId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT invoiceId FROM Invoice WHERE invoiceId LIKE 'INV-%' ORDER BY invoiceId DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = Integer.parseInt(rs.getString(1).substring(4)) + 1;
            return String.format("INV-%03d", next);
        }
    }

    // Add invoice
    public void addInvoice(Invoice inv) {
        String sql = "INSERT INTO Invoice(invoiceId, contractId, clientId, invoiceDate, dueDate, amount, lateFee, status) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (inv.getInvoiceId() == null || inv.getInvoiceId().isBlank()) {
                inv.setInvoiceId(nextInvoiceId(conn));
            }

            // Calculate late fee before storing
            inv.calculateLateFee();

            ps.setString(1, inv.getInvoiceId());
            ps.setString(2, inv.getContractId());
            ps.setString(3, inv.getClientId());
            ps.setDate(4, Date.valueOf(inv.getInvoiceDate()));
            ps.setDate(5, Date.valueOf(inv.getDueDate()));
            ps.setBigDecimal(6, inv.getAmount());
            ps.setBigDecimal(7, inv.getLateFee());
            ps.setString(8, inv.getStatus().name());
            ps.executeUpdate();

            System.out.println("Invoice added: " + inv.getInvoiceId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if a client still has active invoices (UNPAID or OVERDUE)
    public boolean hasActiveInvoicesForClient(String clientId) {
        String sql = "SELECT COUNT(*) FROM Invoice WHERE clientId = ? AND (status = 'UNPAID' OR status = 'OVERDUE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true if there’s at least one active invoice
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Invoice> getActiveInvoicesForClient(String clientId) {
        updateOverdueInvoices(clientId);
        List<Invoice> invoices = new ArrayList<>();

        // SELECT query with JOIN to get clientId from Contract
        String sql = "SELECT i.*, c.clientId " +
                "FROM Invoice i " +
                "JOIN Contract c ON i.contractId = c.contractId " +
                "WHERE c.clientId = ? " +
                "AND (i.status = 'UNPAID' OR i.status = 'OVERDUE')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getDate("invoiceDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status")
                    );
                    invoice.calculateLateFee();
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }


    // Get single invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        // JOIN with Contract to get clientId
        String sql = "SELECT i.*, c.clientId " +
                "FROM Invoice i " +
                "JOIN Contract c ON i.contractId = c.contractId " +
                "WHERE i.invoiceId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            rs.getString("clientId"),  // From Contract via JOIN
                            rs.getDate("invoiceDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status")
                    );
                    invoice.calculateLateFee();
                    return invoice;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Contract getContractByInvoiceId(String invoiceId) {
        String sql = """
        SELECT c.* 
        FROM Contract c
        JOIN Invoice i ON c.contractId = i.contractId
        WHERE i.invoiceId = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming your Contract class looks something like this:
                    return new Contract(
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getString("managerId"),
                            rs.getString("branchId"),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // if not found
    }


    public List<Invoice> getInvoicesByClientID(String clientID) {
        List<Invoice> out = new ArrayList<>();
        // JOIN with Contract to get invoices by clientId
        String sql = "SELECT i.*, c.clientId " +
                "FROM Invoice i " +
                "JOIN Contract c ON i.contractId = c.contractId " +
                "WHERE c.clientId = ? " +
                "ORDER BY i.invoiceId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clientID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getDate("invoiceDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status"));
                    inv.calculateLateFee();
                    out.add(inv);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return out;
    }
    // --- helper for status ---
    public void markUnpaid(String invoiceId) { updateStatus(invoiceId, InvoiceStatus.UNPAID); }
    public void markPaid(String invoiceId) { updateStatus(invoiceId, InvoiceStatus.PAID); }

    private void updateStatus(String invoiceId, InvoiceStatus status) {
        String sql = "UPDATE Invoice SET status=? WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, invoiceId);
            ps.executeUpdate();
            System.out.println("Invoice " + invoiceId + " marked as " + status.name());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateOverdueInvoices(String clientId) {
        // UPDATE query with JOIN
        String sqlUpdate = "UPDATE Invoice i " +
                "JOIN Contract c ON i.contractId = c.contractId " +
                "SET i.status = 'OVERDUE' " +
                "WHERE i.dueDate < CURDATE() " +
                "AND i.status = 'UNPAID' " +
                "AND c.clientId = ?";

        // SELECT COUNT query with JOIN
        String sqlCount = "SELECT COUNT(*) " +
                "FROM Invoice i " +
                "JOIN Contract c ON i.contractId = c.contractId " +
                "WHERE c.clientId = ? " +
                "AND i.status = 'OVERDUE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement psCount = conn.prepareStatement(sqlCount)) {

            // update overdue invoices
            psUpdate.setString(1, clientId);
            psUpdate.executeUpdate(); // ignore rowsUpdated for printing

            // count total overdue invoices for client
            psCount.setString(1, clientId);
            ResultSet rs = psCount.executeQuery();
            if (rs.next()) {
                int overdueCount = rs.getInt(1);
                if (overdueCount > 0) {
                    System.out.println(overdueCount + " invoice(s) for client " + clientId + " are OVERDUE.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, OutstandingInvoiceReport>> getOutstandingReportsPerMonth() {
        String sql = "SELECT c.name AS clientName, " +
                "YEAR(i.invoiceDate) AS year, " +
                "MONTH(i.invoiceDate) AS month, " +
                "GROUP_CONCAT(i.invoiceId SEPARATOR ', ') AS invoiceIds, " +
                "SUM(i.amount) AS totalAmount " +
                "FROM Client c " +
                "LEFT JOIN Invoice i ON c.clientId = i.clientId AND i.status IN ('UNPAID','OVERDUE') " +
                "GROUP BY c.name, YEAR(i.invoiceDate), MONTH(i.invoiceDate) " +
                "ORDER BY c.name, YEAR(i.invoiceDate), MONTH(i.invoiceDate)";

        Map<String, Map<String, OutstandingInvoiceReport>> outstandingReport = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String clientName = rs.getString("clientName");
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                BigDecimal totalAmount = rs.getBigDecimal("totalAmount");
                String invoiceIds = rs.getString("invoiceIds");
                String monthYear = (month > 0) ? String.format("%02d/%d", month, year) : "N/A";

                outstandingReport.putIfAbsent(clientName, new LinkedHashMap<>());

                if (invoiceIds != null && totalAmount != null) {
                    OutstandingInvoiceReport report = new OutstandingInvoiceReport(invoiceIds, totalAmount);
                    outstandingReport.get(clientName).put(monthYear, report);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outstandingReport;
    }



}
