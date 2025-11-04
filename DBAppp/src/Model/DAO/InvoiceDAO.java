package Model.DAO;

import Model.Entities.Invoice;
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

    //Add

    public void addInvoice(Invoice inv) {
        String sql = "INSERT INTO Invoice(invoiceId, contractId, clientId, invoiceDate, dueDate, amount, lateFee, status) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (inv.getInvoiceId() == null || inv.getInvoiceId().isBlank()) {
                inv.setInvoiceId(nextInvoiceId(conn));
            }
            ps.setString(1, inv.getInvoiceId());
            ps.setString(2, inv.getContractId());
            ps.setString(3, inv.getClientId());
            ps.setDate(4, java.sql.Date.valueOf(inv.getInvoiceDate())); // avoid Date ambiguity
            ps.setDate(5, java.sql.Date.valueOf(inv.getDueDate()));
            ps.setBigDecimal(6, inv.getAmount());
            ps.setBigDecimal(7, inv.getLateFee());
            ps.setString(8, inv.getStatus());
            ps.executeUpdate();
            System.out.println("Invoice added: " + inv.getInvoiceId());
        } catch (SQLException e) { e.printStackTrace(); }
    }


    public Invoice getInvoiceById(String id) {
        String sql = "SELECT * FROM Invoice WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Invoice(
                            rs.getString("invoiceId"),
                            rs.getString("contractId"),
                            rs.getString("clientId"),
                            rs.getDate("invoiceDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("lateFee"),
                            rs.getString("status"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> out = new ArrayList<>();
        String sql = "SELECT * FROM Invoice ORDER BY invoiceId";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new Invoice(
                        rs.getString("invoiceId"),
                        rs.getString("contractId"),
                        rs.getString("clientId"),
                        rs.getDate("invoiceDate").toLocalDate(),
                        rs.getDate("dueDate").toLocalDate(),
                        rs.getBigDecimal("amount"),
                        rs.getBigDecimal("lateFee"),
                        rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    public void updateInvoice(Invoice inv) {
        String sql = "UPDATE Invoice SET contractId=?, clientId=?, invoiceDate=?, dueDate=?, amount=?, lateFee=?, status=? WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inv.getContractId());
            ps.setString(2, inv.getClientId());
            ps.setDate(3, java.sql.Date.valueOf(inv.getInvoiceDate()));
            ps.setDate(4, java.sql.Date.valueOf(inv.getDueDate()));
            ps.setBigDecimal(5, inv.getAmount());
            ps.setBigDecimal(6, inv.getLateFee());
            ps.setString(7, inv.getStatus());
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

    //Retrieve ACTIVE contracts + services for 'today'
    public void printActiveContractsWithServices(LocalDate today) {
        String qContracts = """
            SELECT contractId, clientId, startDate, endDate, terms
            FROM Contract
            WHERE startDate <= ? AND endDate >= ? AND contract_status='Active'
            ORDER BY contractId
            """;
        String qServices = """
            SELECT s.serviceId, s.name, s.rate
            FROM ContractService cs
            JOIN Service s ON s.serviceId = cs.serviceId
            WHERE cs.contractId = ?
            ORDER BY s.serviceId
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pc = conn.prepareStatement(qContracts)) {
            pc.setDate(1, java.sql.Date.valueOf(today));
            pc.setDate(2, java.sql.Date.valueOf(today));
            try (ResultSet rc = pc.executeQuery()) {
                while (rc.next()) {
                    String cid = rc.getString("contractId");
                    System.out.printf("Contract %s | Client %s | %s..%s | Terms: %s%n",
                            cid, rc.getString("clientId"), rc.getDate("startDate"), rc.getDate("endDate"), rc.getString("terms"));
                    try (PreparedStatement ps = conn.prepareStatement(qServices)) {
                        ps.setString(1, cid);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                System.out.printf("  - %s | %s | %s%n",
                                        rs.getString("serviceId"), rs.getString("name"), rs.getBigDecimal("rate"));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //Calculate billing amount = sum of service rates for a contract
    public BigDecimal calculateContractAmount(String contractId) {
        String sql = """
            SELECT COALESCE(SUM(s.rate),0) AS total
            FROM ContractService cs
            JOIN Service s ON s.serviceId = cs.serviceId
            WHERE cs.contractId = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    //Create Invoice (status = 'Unpaid') with lateFee (atomic)
    public Invoice createInvoiceFromContract(String contractId,
                                             LocalDate invoiceDate,
                                             LocalDate dueDate,
                                             BigDecimal lateFee) {
        String qClient = "SELECT clientId FROM Contract WHERE contractId=?";
        String ins = "INSERT INTO Invoice(invoiceId, contractId, clientId, invoiceDate, dueDate, amount, lateFee, status) "
                + "VALUES (?,?,?,?,?,?,?, 'Unpaid')";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String clientId = null;
            try (PreparedStatement ps = conn.prepareStatement(qClient)) {
                ps.setString(1, contractId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) clientId = rs.getString("clientId");
                }
            }
            if (clientId == null) { conn.rollback(); throw new SQLException("Contract not found: " + contractId); }

            BigDecimal amount = calculateContractAmountTx(conn, contractId);
            String invoiceId = nextInvoiceId(conn);

            try (PreparedStatement pi = conn.prepareStatement(ins)) {
                pi.setString(1, invoiceId);
                pi.setString(2, contractId);
                pi.setString(3, clientId);
                pi.setDate(4, java.sql.Date.valueOf(invoiceDate));
                pi.setDate(5, java.sql.Date.valueOf(dueDate));
                pi.setBigDecimal(6, amount);
                pi.setBigDecimal(7, lateFee == null ? BigDecimal.ZERO : lateFee);
                pi.executeUpdate();
            }
            conn.commit();
            return new Invoice(invoiceId, contractId, clientId, invoiceDate, dueDate, amount,
                    lateFee == null ? BigDecimal.ZERO : lateFee, "Unpaid");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Mark invoice Unpaid
    public void markUnpaid(String invoiceId) { updateStatus(invoiceId, "Unpaid"); }

    private void updateStatus(String invoiceId, String status) {
        String sql = "UPDATE Invoice SET status=? WHERE invoiceId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, invoiceId);
            ps.executeUpdate();
            System.out.println("Invoice " + invoiceId + " marked " + status);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private BigDecimal calculateContractAmountTx(Connection conn, String contractId) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(s.rate),0) AS total
            FROM ContractService cs
            JOIN Service s ON s.serviceId = cs.serviceId
            WHERE cs.contractId = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }
}
