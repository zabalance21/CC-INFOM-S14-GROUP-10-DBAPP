package Controller;

import Model.DAO.InvoiceDAO;
import Model.Entities.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InvoiceController {
    private final InvoiceDAO dao = new InvoiceDAO();

    public void addInvoice(Invoice i) { dao.addInvoice(i); }
    public Invoice getInvoiceById(String id) { return dao.getInvoiceById(id); }
    public List<Invoice> getAllInvoices() { return dao.getAllInvoices(); }
    public void updateInvoice(Invoice i) { dao.updateInvoice(i); }
    public void deleteInvoice(String id) { dao.deleteInvoice(id); }

    public void printActiveContractsWithServices(LocalDate today) { dao.printActiveContractsWithServices(today); }
    public BigDecimal calculateContractAmount(String contractId) { return dao.calculateContractAmount(contractId); }
    public Invoice createInvoiceFromContract(String contractId, LocalDate inv, LocalDate due, BigDecimal lateFee) {
        return dao.createInvoiceFromContract(contractId, inv, due, lateFee);
    }
    public void markUnpaid(String invoiceId) { dao.markUnpaid(invoiceId); }
}