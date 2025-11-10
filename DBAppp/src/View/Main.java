package View;

import GUI.ClientGUI;
import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

//For debugging purposes I still added ClientView.java. :))) - Mico
//Once GUI is goods we can just delete lines 19-23 and 25-28
public class Main {
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            e.printStackTrace();
        }

        String[] options = {"GUI Interface", "Console Interface"};
        int choice = JOptionPane.showOptionDialog(null, "Choose interface type: ", "IT Services Management System",
                                                 JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
        if (choice == 0){
            SwingUtilities.invokeLater(() -> new ClientGUI());
        } else {
            ClientView clientView = new ClientView();
            clientView.showClientMenu();
        }
    }
}