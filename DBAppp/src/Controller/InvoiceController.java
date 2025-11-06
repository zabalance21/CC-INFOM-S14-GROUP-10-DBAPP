package Controller;

import Model.DAO.ContractDAO;
import Model.DAO.InvoiceDAO;
import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.InvoiceStatus;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    // Add a new invoice
    public void addInvoice(Invoice invoice) {
        invoiceDAO.addInvoice(invoice);
    }

}
