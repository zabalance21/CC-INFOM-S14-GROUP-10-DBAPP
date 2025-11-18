package Controller;

import Model.DAO.InvoiceDAO;

import Model.Entities.OutstandingInvoiceReport;

import java.util.Map;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public Map<String, Map<String, OutstandingInvoiceReport>> getOustandingReports() {
        return invoiceDAO.getOutstandingReportsPerMonth();
    }
}
