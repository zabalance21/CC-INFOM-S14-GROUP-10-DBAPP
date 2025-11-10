package Controller;

import Model.DAO.*;
import Model.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentController {
    private ClientDAO clientDAO;
    private InvoiceDAO invoiceDAO;
    private ContractDAO contractDAO;
    private ContractServiceDao contractServiceDAO;
    private PaymentDAO paymentDAO;

    public PaymentController(PaymentDAO paymentDAO, ClientDAO clientDAO, InvoiceDAO invoiceDAO, ContractDAO contractDAO, ContractServiceDao contractServiceDAO){
        this.paymentDAO = paymentDAO;
        this.clientDAO = clientDAO;
        this.invoiceDAO = invoiceDAO;
        this.contractDAO = contractDAO;
        this.contractServiceDAO = contractServiceDAO;
    }

    public void addPayment(Payment payment) {
        paymentDAO.addPayment(payment);
    }

    public boolean processPayment(String clientId, String invoiceId, BigDecimal paidAmount) {
        Client client = clientDAO.getClientByID(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return false;
        }

        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId, client.getClientId());
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
        Payment payment = new Payment(invoiceId, client.getClientId(), LocalDate.now(), paidAmount);
        paymentDAO.addPayment(payment);

        // Update related entities
        invoiceDAO.markPaid(invoiceId);
        Contract contract = invoiceDAO.getContractByInvoiceId(invoiceId);
        ContractService cs = contractServiceDAO.getContractServiceByContractId(contract.getContractID());

        if (contract != null) {
            contractDAO.closeContract(contract.getContractID());  // Soft delete or mark closed
            contractServiceDAO.deactivateContractServices(cs.getContractServiceID());
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

}
