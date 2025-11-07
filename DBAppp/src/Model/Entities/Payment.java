package Model.Entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private String paymentId;
    private String invoiceId;
    private String clientId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String receiptNumber;
    private static int paymentIdCounter = 3;
    private static int receptIdCounter = 2;
    private static int MAX_RECIEPTS = 1000;

    public Payment(String invoiceId, String clientId, LocalDate paymentDate, BigDecimal amount) {
        setPaymentId();
        this.invoiceId = invoiceId;
        this.clientId = clientId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    private void setPaymentId(){
        if(paymentIdCounter > MAX_RECIEPTS){
            throw new IllegalStateException("All possible payment IDs have been used.");
        }
        this.paymentId = String.format("PM-%03d", paymentIdCounter++);
    }

    private void setReceiptNumber(){
        if(receptIdCounter > MAX_RECIEPTS){
            throw new IllegalStateException("All possible receipt IDs have been used.");
        }
        this.receiptNumber = String.format("REF-%03d", receptIdCounter++);
    }

    public String getPaymentId() {return this.paymentId;}
    public String getReceiptNumber() {return this.receiptNumber;}
    public String getClientId() {return this.clientId;}
    public LocalDate getPaymentDate() {return this.paymentDate;}
    public BigDecimal getAmount() {return this.amount;}
    public String getInvoiceId() {return this.invoiceId;}
}
