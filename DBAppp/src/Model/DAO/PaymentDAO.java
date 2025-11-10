package Model.DAO;

import Model.Entities.Contract;
import Model.Entities.Payment;
import Model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentDAO {

    // CREATE
    public void addPayment(Payment payment) {
        String sql = "INSERT INTO Payment (paymentId, invoiceId, clientId, paymentDate, amount, referenceNumber) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getInvoiceId());
            stmt.setString(3, payment.getClientId());
            stmt.setDate(4,  java.sql.Date.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(5,payment.getAmount());
            stmt.setString(6, payment.getReceiptNumber());

            stmt.executeUpdate();
            System.out.println("Payment successfully added!");

        } catch (SQLException e) {
            System.out.println("Error inserting payment record.");
            e.printStackTrace();
        }
    }
}
