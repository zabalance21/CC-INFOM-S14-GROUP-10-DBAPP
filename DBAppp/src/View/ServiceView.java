package View;

import Controller.ServiceController;
import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ServiceView {
    private ServiceController serviceController;
    private Scanner sc;

    public ServiceView(ServiceController serviceController, Scanner sc) {
        this.serviceController = serviceController;
        this.sc = sc;
    }

    public void showMenu() {
        int ch = -1;
        do {
            System.out.println("\n=== Service Menu ===");
            System.out.println("[1] Add Service");
            System.out.println("[2] View Service by ID");
            System.out.println("[3] View All Services");
            System.out.println("[4] Update Service");
            System.out.println("[5] Delete Service");
            System.out.println("[6] View Related Contracts/Invoices");
            System.out.println("[0] Back");
            System.out.print("Choice: ");

            String line = sc.nextLine().trim();
            try { ch = line.isEmpty() ? -1 : Integer.parseInt(line); } catch (NumberFormatException e) { ch = -1; }

            switch (ch) {
                case 1 -> add();
                case 2 -> viewById();
                case 3 -> listAll();
                case 4 -> update();
                case 5 -> softDelete();
                case 6 -> related();
                case 0 -> {}
                default -> System.out.println("Invalid.");
            }
        } while (ch != 0);
    }

    private void add() {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Description: ");
            String desc = sc.nextLine();
            System.out.print("Rate: ");
            BigDecimal rate = new BigDecimal(sc.nextLine());

            Service s = new Service(name, desc, rate);
            if(!serviceController.addService(s)){
                System.out.println("Service already exists.");
            }else{
                System.out.println("Service added successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewById() {
        printAvailableServices();
        System.out.print("Service ID (e.g., SV-001): ");
        Service s = serviceController.getServiceById(sc.nextLine().trim());
        System.out.println(s == null ? "Service Not found." : s);
    }

    private void listAll() {
        List<Service> all = serviceController.getAllServices();
        if (all.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (Service s : all) System.out.print(s);
    }

    private void update() {
        printAvailableServices();
        System.out.print("Service ID to update: ");
        String id = sc.nextLine().trim();
        Service s = serviceController.getServiceById(id);
        if (s == null) {
            System.out.println("Not found.");
            return;
        }

        System.out.println("Leave blank to keep existing values.");
        System.out.print("New name (" + s.getName() + "): ");
        String name = sc.nextLine();
        System.out.print("New description: ");
        String desc = sc.nextLine();
        System.out.print("New rate (" + s.getRate() + "): ");
        String rate = sc.nextLine();
        System.out.print("Availability [" + s.getAvailability() + "] (Available/Unavailable/Discontinued, blank=keep): ");
        String avail = sc.nextLine();

        if (!name.isBlank()) s.setName(name);
        if (!desc.isBlank()) s.setDescription(desc);
        if (!rate.isBlank()) s.setRate(new BigDecimal(rate));
        if (!avail.isBlank()) s.setAvailability(avail);

        serviceController.updateService(s);
    }

    private void softDelete() {
        printAvailableServices();
        System.out.print("Service ID to mark UNAVAILABLE: ");
        if(!serviceController.deleteService(sc.nextLine().trim())){
            System.out.println("Cannot mark service 'Unavailable', it is still used in active contracts.");
        }else{
            System.out.println("Service marked as unavailable.");
        }
    }

    private void related() {
        printAvailableServices();
        String serviceId = InputHelper.getStringInput("Service ID (e.g., SV-001): ");
        Map<String, List<?>> relatedRecords = serviceController.getRelatedRecords(serviceId);
        List<Contract> contracts = (List<Contract>) relatedRecords.get("contracts");
        List<Invoice> invoices = (List<Invoice>) relatedRecords.get("invoices");

        System.out.println("=== Related Records for Service " + serviceId + " ===");

        System.out.println("Contracts that include this Service:");
        if (contracts.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (Contract c : contracts) {
                System.out.println("  - Contract " + c.getContractID());
            }
        }

        System.out.println("Invoices that include this Service (via Contract):");
        if (invoices.isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (Invoice i : invoices) {
                System.out.printf("  - Invoice %s | Contract %s | Amount %s | LateFee %s | %s%n",
                        i.getInvoiceId(),
                        i.getContractId(),
                        i.getAmount(),
                        i.getLateFee(),
                        i.getStatus());
            }
        }
    }

    public void printAvailableServices(){
        List<Service> services = serviceController.getAvailableServices();
        for (Service s : services){
            System.out.println(String.format("Service ID: %s | Service Name: %s\n", s.getServiceId(), s.getName()));
        }
    }
}
