package View;
import Controller.ServiceController;
import Model.Entities.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
public class ServiceView {
    private final ServiceController serviceController = new ServiceController();
    private final Scanner sc = new Scanner(System.in);

    public void showMenu() {
        int ch = -1;
        do {
            System.out.println("\n=== Service Menu ===");
            System.out.println("[1] Add Service");
            System.out.println("[2] View Service by ID");
            System.out.println("[3] View All Services");
            System.out.println("[4] Update Service");
            System.out.println("[5] Delete Service");
            System.out.println("[6] View Service with Related Contracts/Invoices");
            System.out.println("[0] Back");
            System.out.print("Choice: ");

            String line = sc.nextLine().trim();
            try { ch = line.isEmpty() ? -1 : Integer.parseInt(line); } catch (NumberFormatException e) { ch = -1; }

            switch (ch) {
                case 1:
                    add();
                    break;
                case 2:
                    viewById();
                    break;
                case 3:
                    listAll();
                    break;
                case 4:
                    update();
                    break;
                case 5:
                    softDelete();
                    break;
                case 6:
                    related();
                    break;
                case 0:
                    break;
                default:
                    if (ch != 0) System.out.println("Invalid.");
                    break;
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
            System.out.print("Category: ");
            String category = sc.nextLine();

            Service s = new Service(name, desc, rate, category);
            serviceController.addService(s);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewById() {
        System.out.print("Service ID (e.g., SV-001): ");
        Service s = serviceController.getServiceById(sc.nextLine().trim());
        System.out.println(s == null ? "Not found." : s);
    }

    private void listAll() {
        List<Service> all = serviceController.getAllServices();
        if (all.isEmpty()) { System.out.println("(none)"); return; }
        for (Service s : all) System.out.print(s);
    }

    private void update() {
        System.out.print("Service ID to update: ");
        String id = sc.nextLine().trim();
        Service s = serviceController.getServiceById(id);
        if (s == null) { System.out.println("Not found."); return; }

        System.out.println("Leave blank to keep existing.");
        System.out.print("New name (" + s.getName() + "): "); String name = sc.nextLine();
        System.out.print("New description: "); String desc = sc.nextLine();
        System.out.print("New rate (" + s.getRate() + "): "); String rate = sc.nextLine();
        System.out.print("New category (" + s.getCategory() + "): "); String cat = sc.nextLine();
        System.out.print("Availability [" + s.getAvailability() + "] (Available/Unavailable/Discontinued, blank=keep): ");
        String avail = sc.nextLine();

        if (!name.isBlank()) s.setName(name);
        if (!desc.isBlank()) s.setDescription(desc);
        if (!rate.isBlank()) s.setRate(new BigDecimal(rate));
        if (!cat.isBlank()) s.setCategory(cat);
        if (!avail.isBlank()) s.setAvailability(avail);

        serviceController.updateService(s);
    }

    private void softDelete() {
        System.out.print("Service ID to mark UNAVAILABLE: ");
        serviceController.deleteService(sc.nextLine().trim());
    }

    private void related() {
        System.out.print("Service ID (e.g., SV-001): ");
        serviceController.viewRelatedRecords(sc.nextLine().trim());
    }
}
