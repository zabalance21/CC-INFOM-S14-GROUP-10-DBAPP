package Controller;

import Model.DAO.ContractDAO;
import Model.DAO.InvoiceDAO;
import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.InvoiceStatus;
import Model.Entities.OutstandingInvoiceReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public Map<String, Map<String, OutstandingInvoiceReport>>  getOustandingReports() {
        return invoiceDAO.getOutstandingReportsPerMonth();
    }
}
