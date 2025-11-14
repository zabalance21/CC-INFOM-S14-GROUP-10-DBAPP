package Model.Entities;

public class ClientHistory {
    private String clientId;
    private String contractIds;
    private String invoiceIds;
    private String paymentIds;
    private int year;

    public ClientHistory(String clientId,int year, String contractIds, String invoiceIds, String paymentIds) {
        this.clientId = clientId;
        this.contractIds = contractIds;
        this.invoiceIds = invoiceIds;
        this.paymentIds = paymentIds;
        this.year = year;
    }

    public String getClientId() {return this.clientId;}
    public String getContractIds() {return this.contractIds;}
    public String getInvoiceIds() {return this.invoiceIds;}
    public String getPaymentIds() {return this.paymentIds;}
    public int getYear() {return this.year;}
}
