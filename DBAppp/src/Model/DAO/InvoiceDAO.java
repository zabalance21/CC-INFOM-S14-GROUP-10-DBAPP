package Model.DAO;

import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.InvoiceStatus;
import Model.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        updateOverdueInvoices();
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice WHERE clientId = ? AND (status = 'UNPAID' OR status = 'OVERDUE')";

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
                    invoice.calculateLateFee(); // optional: keep data up to date
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }


    // Get single invoice by ID
    public Invoice getInvoiceById(String invoiceId, String clientId) {
        String sql = "SELECT * FROM Invoice WHERE invoiceId=? AND clientId = ? ";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            ps.setString(2, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getDate("invoiceDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status"));
                    inv.calculateLateFee(); // recalc dynamically
                    return inv;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
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
        String sql = "SELECT * FROM Invoice WHERE clientId = ? ORDER BY invoiceId";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, clientID); // set the client ID parameter
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


    // Update invoice (including recalculating late fees)
    public void updateInvoice(Invoice inv) {
        String sql = "UPDATE Invoice SET contractId=?, clientId=?, invoiceDate=?, dueDate=?, amount=?, lateFee=?, status=? WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            inv.calculateLateFee();

            ps.setString(1, inv.getContractId());
            ps.setString(2, inv.getClientId());
            ps.setDate(3, Date.valueOf(inv.getInvoiceDate()));
            ps.setDate(4, Date.valueOf(inv.getDueDate()));
            ps.setBigDecimal(5, inv.getAmount());
            ps.setBigDecimal(6, inv.getLateFee());
            ps.setString(7, inv.getStatus().name());
            ps.setString(8, inv.getInvoiceId());
            ps.executeUpdate();

            System.out.println("Invoice updated: " + inv.getInvoiceId());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteInvoice(String id) {
        String sql = "DELETE FROM Invoice WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            System.out.println("Invoice deleted: " + id);
        } catch (SQLException e) { e.printStackTrace(); }
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

    public void updateOverdueInvoices() {
        String sql = "UPDATE Invoice SET status = 'OVERDUE' WHERE dueDate < CURDATE() AND status = 'UNPAID'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int rowsUpdated = ps.executeUpdate();
            System.out.println(rowsUpdated + " invoice(s) marked as OVERDUE.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
