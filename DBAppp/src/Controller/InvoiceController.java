package Controller;

import Model.DAO.ContractDAO;
import Model.DAO.InvoiceDAO;
import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.InvoiceStatus;

import java.util.ArrayList;
import java.util.List;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    // Add a new invoice
    public void addInvoice(Invoice invoice) {
        invoiceDAO.addInvoice(invoice);
    }


    public boolean clientHasActiveInvoice(String clientId) {
        return invoiceDAO.hasActiveInvoicesForClient(clientId);
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

    public Invoice getInvoicebyID(String invoiceId, String clientId){
        return invoiceDAO.getInvoiceById(invoiceId, clientId);
    }

    public void markInvoicePaid(String invoiceId) {
        invoiceDAO.markPaid(invoiceId);
    }

    public Contract getContractbyID(String invoiceId) {
        return invoiceDAO.getContractByInvoiceId(invoiceId);
    }
}
