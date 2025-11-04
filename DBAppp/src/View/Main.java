package View;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ClientView clientView = new ClientView();
        ServiceView serviceView = new ServiceView();
        int pick = -1;
        do {
            System.out.println("\n=== Main Menu ===");
            System.out.println("[1] Client Menu");
            System.out.println("[2] Service Menu");
            System.out.println("[3] Payment Menu");
            System.out.println("[4] Contract Renewal Menu");
            System.out.println("[0] Exit");
            System.out.print("Enter your choice: ");

            String in = sc.nextLine().trim();
            try { pick = Integer.parseInt(in); } catch (NumberFormatException e) { pick = -1; }

            switch (pick) {
                case 1:
                    clientView.showClientMenu();
                    break;
                case 2:
                    serviceView.showMenu();
                    break;
                // case 3:
                // case 4:
                case 0:
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (pick != 0);
    }
}