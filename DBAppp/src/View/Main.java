package View;

import GUI.ManagementGUI;

//import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Scanner scanner = new Scanner(System.in);
        // App app = new App(scanner);
        // app.mainApp();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e){
                e.printStackTrace();
            }
            new ManagementGUI();
        });
    }
}