package Controller;

import Model.DAO.*;
import Model.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PaymentController {
    private ClientDAO clientDAO;
    private InvoiceDAO invoiceDAO;
    private ContractDAO contractDAO;
    private PaymentDAO paymentDAO;

    public PaymentController(PaymentDAO paymentDAO, ClientDAO clientDAO, InvoiceDAO invoiceDAO, ContractDAO contractDAO){
        this.paymentDAO = paymentDAO;
        this.clientDAO = clientDAO;
        this.invoiceDAO = invoiceDAO;
        this.contractDAO = contractDAO;
    }

    public boolean processPayment(String clientId, String invoiceId, BigDecimal paidAmount) {
        Client client = clientDAO.getClientByID(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return false;
        }

        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) {
            System.out.println("Invoice not found for this client.");
            return false;
        }

        // Validate payment
        if (paidAmount.compareTo(invoice.getAmount()) != 0) {
            System.out.println("Paid amount must match invoice amount exactly.");
            return false;
        }

        // Record payment
        Payment payment = new Payment(invoiceId, LocalDate.now(), paidAmount);
        paymentDAO.addPayment(payment);

        // Update related entities
        invoiceDAO.markPaid(invoiceId);
        Contract contract = invoiceDAO.getContractByInvoiceId(invoiceId);

        if (contract != null) {
            contractDAO.closeContract(contract.getContractID());  // Soft delete or mark closed
        }

        System.out.println("Payment Reference: " + payment.getReceiptNumber());
        return true;
    }

    public void showAllActiveClientInvoices(String clientId) {
        List<Invoice> activeInvoices = invoiceDAO.getActiveInvoicesForClient(clientId);

        if (activeInvoices.isEmpty()) {
            System.out.println("No active invoices found for client: " + clientId);
        } else {
            System.out.println("Active invoices for client " + clientId + ":");
            for (Invoice inv : activeInvoices) {
                System.out.println(" - " + inv.getInvoiceId() +
                        " | Due: " + inv.getDueDate() +
                        " | Status: " + inv.getStatus() +
                        " | Amount: " + inv.getAmount());
            }
        }
    }

    public Map<String, Map<String, BigDecimal>> getCollectionsPerMonth() {
        return paymentDAO.getMonthlyCollectionsPerClient();
    }

    public Map<String, Map<String, BigDecimal>> getRevenuePerQuarter(){
        return paymentDAO.getQuarterlyRevenuePerService();
    }

    public BigDecimal getMonthlyRevenue(){
        return paymentDAO.getMonthlyRevenue();
    }
    public BigDecimal getMonthlyRevenue(int month, int year){
        return paymentDAO.getMonthlyRevenue(month, year);
    }

}
