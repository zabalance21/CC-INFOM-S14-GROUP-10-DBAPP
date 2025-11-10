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
    private BigDecimal lateFee = BigDecimal.ZERO;
    private InvoiceStatus status;

    // Base constructor (no invoiceId)
    public Invoice(String contractId, String clientId,
                   LocalDate invoiceDate, LocalDate dueDate,
                   BigDecimal amount) {
        this.contractId = contractId;
        this.clientId = clientId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = InvoiceStatus.UNPAID;
    }

    // Full constructor (with invoiceId and status)
    public Invoice(String invoiceId, String contractId, String clientId,
                   LocalDate invoiceDate, LocalDate dueDate,
                   BigDecimal amount, BigDecimal lateFee, String status) {
        this.invoiceId = invoiceId;
        this.contractId = contractId;
        this.clientId = clientId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.lateFee = (lateFee == null) ? BigDecimal.ZERO : lateFee;
        this.status = InvoiceStatus.valueOf(status.toUpperCase());
    }


    public String getInvoiceId() { return invoiceId; }
    public String getContractId() { return contractId; }
    public String getClientId() { return clientId; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getLateFee() { return lateFee; }
    public InvoiceStatus getStatus() { return status; }

    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee == null ? BigDecimal.ZERO : lateFee; }

    public void calculateLateFee() {
        if (LocalDate.now().isAfter(dueDate)) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            BigDecimal dailyRate = new BigDecimal("0.01"); // 1% per day
            this.lateFee = amount.multiply(dailyRate).multiply(BigDecimal.valueOf(daysLate));
            this.status = InvoiceStatus.OVERDUE;
        } else {
            this.lateFee = BigDecimal.ZERO;
            if (this.status == InvoiceStatus.OVERDUE) {
                this.status = InvoiceStatus.UNPAID; // reset if paid later or date adjusted
            }
        }
    }


    @Override
    public String toString() {
        return String.format(
                "Invoice %s | Contract %s | Client %s | %s → %s | Amount %s | LateFee %s | %s%n",
                invoiceId, contractId, clientId, invoiceDate, dueDate, amount, lateFee, status
        );
    }
}
