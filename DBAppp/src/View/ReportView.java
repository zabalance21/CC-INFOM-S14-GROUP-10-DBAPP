package View;

import Controller.ClientController;
import Controller.InvoiceController;
import Controller.PaymentController;
import Controller.ServiceController;
import Model.DAO.*;
import Model.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ReportView {
    private ClientController clientController;
    private PaymentController paymentController;
    private InvoiceController invoiceController;
    private ServiceController serviceController;
    public ReportView(ClientController clientController, PaymentController paymentController, InvoiceController invoiceController, ServiceController serviceController) {
        this.clientController = clientController;
        this.paymentController = paymentController;
        this.invoiceController = invoiceController;
        this.serviceController = serviceController;
    }

    public void reportMenu(){
        int choice = -1;
        do{
            System.out.println("\n===Report Menu===\n");
            System.out.println("[1] Montly Billing Report");
            System.out.println("[2] Outstanding Payments Report");
            System.out.println("[3] Services Revenue Report");
            System.out.println("[4] Client History Report");
            System.out.println("[0] Exit");
            choice = InputHelper.getIntInput("Enter your choice: ",0,6);

            switch(choice){
                case 1: monthBillingReport(); break;
                case 2: outstandingPaymentsReport(); break;
                case 3: servicesRevenuesReport(); break;
                case 4: clientHistoryReport(); break;
                case 0: System.out.println("Exiting client menu.");
                default: System.out.println("Invalid choice. Try again.");
            }
        }while(choice != 0);
    }

    private void monthBillingReport(){
        System.out.println("\n=== Monthly Collection Report (Per Client) ===");
        System.out.println("===================================================");

        Map<String, Map<String, BigDecimal>> report = paymentController.getCollectionsPerMonth();
        if (report.isEmpty()) {
            System.out.println("No payments recorded yet.");
            return;
        }
        for(Map.Entry<String, Map<String, BigDecimal>> entry : report.entrySet()){
            String clientName = entry.getKey();
            System.out.println("\nClient Name: " + clientName);
            Map<String, BigDecimal> monthlyTotal = entry.getValue();

            if(monthlyTotal.isEmpty()){
                System.out.println("No monthly total recorded yet.");
            }else{
                for(Map.Entry<String,BigDecimal> monthlyEntry : monthlyTotal.entrySet() ){
                    System.out.printf(" Month : %s | Total Collected: ₱%,.2f\n", monthlyEntry.getKey(), monthlyEntry.getValue());
                }
            }
        }
    }

    private void outstandingPaymentsReport() {
        System.out.println("\n=== Outstanding Payments Report (Per Client) ===");
        System.out.println("===================================================");

        Map<String, Map<String, OutstandingInvoiceReport>> outstandingReports = invoiceController.getOustandingReports();
        if (outstandingReports.isEmpty()) {
            System.out.println("No invoices recorded yet.");
            return;
        }

        for (Map.Entry<String, Map<String, OutstandingInvoiceReport>> entry : outstandingReports.entrySet()) {
            String clientName = entry.getKey();
            System.out.println("Client Name: " + clientName);

            Map<String, OutstandingInvoiceReport> monthlyReports = entry.getValue();
            if (monthlyReports.isEmpty()) {
                System.out.println("    No outstanding invoices.");
            } else {
                for (Map.Entry<String, OutstandingInvoiceReport> monthEntry : monthlyReports.entrySet()) {
                    OutstandingInvoiceReport report = monthEntry.getValue();
                    System.out.printf("   Month: %s | Active Invoices: %s | Total Invoice Amount: ₱%,.2f%n",
                            monthEntry.getKey(), report.getInvoiceIds(), report.getTotalAmount());
                }
            }
            System.out.println("---------------------------------------------------");
        }
    }

    private void servicesRevenuesReport() {
        System.out.println("\n=== Services Revenue Report (Per Service) ===");
        System.out.println("===================================================");

        List<Service> allServices = serviceController.getAllServices();

        Map<String, Map<String, BigDecimal>> quarterlyReport =
                paymentController.getRevenuePerQuarter();

        if (allServices.isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        // Define quarter labels and month ranges
        Map<Integer, String> quarterToMonths = Map.of(
                1, "Jan - Mar",
                2, "Apr - Jun",
                3, "Jul - Sep",
                4, "Oct - Dec"
        );

        // Determine all years present in payments
        Set<Integer> years = new TreeSet<>();
        for (Map<String, BigDecimal> qmap : quarterlyReport.values()) {
            for (String label : qmap.keySet()) {
                years.add(Integer.parseInt(label.split("-Q")[0]));
            }
        }
        if (years.isEmpty()) years.add(LocalDate.now().getYear());

        // Display per service
        for (Service service : allServices) {
            String serviceName = service.getName();
            System.out.println("\nService: " + serviceName);
            System.out.println("---------------------------------------------");

            Map<String, BigDecimal> serviceData =
                    quarterlyReport.getOrDefault(serviceName, new LinkedHashMap<>());

            for (int year : years) {
                System.out.println("Year: " + year);
                for (int q = 1; q <= 4; q++) {
                    String quarterLabel = year + "-Q" + q;
                    BigDecimal amount = serviceData.getOrDefault(quarterLabel, BigDecimal.ZERO);
                    String months = quarterToMonths.get(q);
                    System.out.printf("   Quarter: Q%d (%s) | Revenue: ₱%,.2f%n",
                            q, months, amount);
                }
                System.out.println();
            }
            System.out.println("---------------------------------------------");
        }
    }


    private void clientHistoryReport(){
        System.out.println("\n=== Client History Report (Per Year) ===");
        System.out.println("===================================================");
        List<Client> allClients = clientController.getAllClients();
        for (Client client : allClients) {
            String clientId = client.getClientId();
            System.out.println("Client Name: " + client.getName() + " (ID: " + clientId + ")");

            List<ClientHistory> clientHistories = clientController.getClientHistory(clientId);
            if(clientHistories.isEmpty()){
                System.out.println("No client history recorded yet.");
            }else{
                clientHistories.sort(Comparator.comparingInt(ClientHistory::getYear));

                for(ClientHistory ch : clientHistories){
                    System.out.println(" Year: " + ch.getYear());
                    System.out.println("    Contracts: " + (ch.getContractIds().isEmpty() ? "None" : ch.getContractIds()));
                    System.out.println("    Invoices: " + (ch.getInvoiceIds().isEmpty() ? "None" : ch.getInvoiceIds()));
                    System.out.println("    Payments: " + (ch.getPaymentIds().isEmpty() ? "None" : ch.getPaymentIds()));
                    System.out.println("---------------------------------------------------");
                }
            }
            System.out.println("===================================================");
        }
    }

}
