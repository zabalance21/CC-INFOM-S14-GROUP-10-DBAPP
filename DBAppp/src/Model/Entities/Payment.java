package Model.Entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import Model.DAO.PaymentDAO;

public class Payment {
    private String paymentId;
    private String invoiceId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String receiptNumber;
    private static int receptIdCounter = 2;
    private static int MAX_RECIEPTS = 1000;

    public Payment(String invoiceId, LocalDate paymentDate, BigDecimal amount) {
        setPaymentId();
        this.invoiceId = invoiceId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        setReceiptNumber();
    }

    //Constructor overload for setting payments already in the DB, refer to PaymentDAO.java method getRecentPayments
    public Payment(String paymentId, String invoiceId, LocalDate paymentDate, BigDecimal amount, String receiptNumber) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.receiptNumber = receiptNumber;
    }

    private void setPaymentId(){
        PaymentDAO paymentDAO = new PaymentDAO();
        this.paymentId = paymentDAO.getNextAvailablepaymentId();

        if(this.paymentId == null){
            throw new IllegalStateException("Failed to generate payment ID.");
        }
    }

    private void setReceiptNumber(){
        if(receptIdCounter > MAX_RECIEPTS){
            throw new IllegalStateException("All possible receipt IDs have been used.");
        }
        this.receiptNumber = String.format("REF-%03d", receptIdCounter);
        receptIdCounter++;
    }

    public String getPaymentId() {return this.paymentId;}
    public String getReceiptNumber() {return this.receiptNumber;}
    public LocalDate getPaymentDate() {return this.paymentDate;}
    public BigDecimal getAmount() {return this.amount;}
    public String getInvoiceId() {return this.invoiceId;}
}
