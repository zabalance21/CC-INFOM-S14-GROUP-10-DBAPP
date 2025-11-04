package Model.Entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Invoice {
    private String invoiceId;
    private String contractId;
    private String clientId;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal lateFee;
    private String status;

    // Base ctor (no invoiceId)
    public Invoice(String contractId, String clientId,
                   LocalDate invoiceDate, LocalDate dueDate,
                   BigDecimal amount, BigDecimal lateFee, String status) {
        this.contractId = contractId;
        this.clientId = clientId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.lateFee = lateFee == null ? BigDecimal.ZERO : lateFee;
        this.status = status;
    }

    // Full ctor (with invoiceId)
    public Invoice(String invoiceId, String contractId, String clientId,
                   LocalDate invoiceDate, LocalDate dueDate,
                   BigDecimal amount, BigDecimal lateFee, String status) {
        this(contractId, clientId, invoiceDate, dueDate, amount, lateFee, status);
        this.invoiceId = invoiceId;
    }

    public String getInvoiceId() { return invoiceId; }
    public String getContractId() { return contractId; }
    public String getClientId() { return clientId; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getLateFee() { return lateFee; }
    public String getStatus() { return status; }

    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public void setStatus(String status) { this.status = status; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee == null ? BigDecimal.ZERO : lateFee; }

    @Override
    public String toString() {
        return String.format(
                "Invoice %s | Contract %s | Client %s | %s → %s | Amount %s | LateFee %s | %s%n",
                invoiceId, contractId, clientId, invoiceDate, dueDate, amount, lateFee, status
        );
    }
}