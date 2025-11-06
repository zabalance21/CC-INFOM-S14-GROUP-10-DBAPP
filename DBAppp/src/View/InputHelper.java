package View;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    // Integer input with range validation
    public static int getIntInput(String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be blank. Please enter a number.");
                continue;
            }

            try {
                value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    public static String getStringInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Input cannot be blank. Please try again.");
            } else {
                break;
            }
        }
        return input;
    }

    public static String getYesOrNo(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + ("(Y/N): "));
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("Y") || input.equals("N")) {
                return input;
            }else{
                System.out.println("Invalid input. Please enter 'Y' or 'N' only.");
            }
        }
    }
}
