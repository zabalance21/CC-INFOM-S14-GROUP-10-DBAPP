package Model.Entities;

import java.math.BigDecimal;

public class OutstandingInvoiceReport {
    private String invoiceIds; // e.g., "INV-001, INV-002"
    private BigDecimal totalAmount;

    public OutstandingInvoiceReport(String invoiceIds, BigDecimal totalAmount) {
        this.invoiceIds = invoiceIds;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceIds() { return invoiceIds; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}

