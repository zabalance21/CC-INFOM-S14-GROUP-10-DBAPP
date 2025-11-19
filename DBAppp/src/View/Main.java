package View;

// UNCOMMENT TO USE TERMINAL AND COMMENT OUT EVERYTHING STARTING FROM "import GUI.CLIENTGUI;"
/* 
import java.util.Scanner;
import View.App;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        App app = new App(scanner);
        app.mainApp();
    }
}
*/

import GUI.ClientGUI;
import GUI.AdminGUI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static final String ADMIN_PASSWORD = "Admin"; //Change if needed
    private static JFrame selectorFrame;
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
            e.printStackTrace();
        }

        showInterfaceSelector();
    }

    private static void showInterfaceSelector(){
        if(selectorFrame != null && selectorFrame.isVisible()){
            selectorFrame.toFront();
            return;
        }
        
        selectorFrame = new JFrame("IT Services Management System");
        selectorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectorFrame.setSize(400, 300);
        selectorFrame.setLocationRelativeTo(null);
        selectorFrame.setLayout(new BorderLayout());

        //Header
        JLabel headerLabel = new JLabel("Welcome to IT Services Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        //Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton clientButton = new JButton("Client Interface");
        JButton adminButton = new JButton("Admin Interface");

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectorFrame.setVisible(false); // Hide selector
                launchClientGUI();
            }
        });

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticateAdminGUI()) {
                    selectorFrame.setVisible(false); // Hide selector
                    launchAdminGUI();
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        
        clientButton.setFont(new Font("Arial", Font.PLAIN, 14));
        adminButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clientButton.setBackground(new Color(70, 130, 180));
        adminButton.setBackground(new Color(220, 20, 60));
        clientButton.setForeground(Color.BLACK);
        adminButton.setForeground(Color.BLACK);
        clientButton.setFocusPainted(false);
        adminButton.setFocusPainted(false);
        
        buttonPanel.add(clientButton);
        buttonPanel.add(adminButton);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JTextArea infoArea = new JTextArea(
            "Client Interface: For creating contracts, processing payments, and contract renewals.\n\n" +
            "Admin Interface: Full system management including reports, user management, and system configuration."
        );

        infoArea.setEditable(false);
        infoArea.setBackground(selectorFrame.getBackground());
        infoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoPanel.add(infoArea, BorderLayout.CENTER);

        selectorFrame.add(headerLabel, BorderLayout.NORTH);
        selectorFrame.add(buttonPanel, BorderLayout.CENTER);
        selectorFrame.add(infoPanel, BorderLayout.SOUTH);
        
        selectorFrame.setVisible(true);
    }

    private static boolean authenticateAdminGUI(){
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
          "Enter admin password: ", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null,
            message,
            "Admin Authentication",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION){
            String password = new String(passwordField.getPassword());
            return ADMIN_PASSWORD.equals(password);
        }

        return false;
    }

    private static void launchClientGUI(){
        SwingUtilities.invokeLater(() -> {
            try {
               ClientGUI clientGUI = new ClientGUI();

               clientGUI.getFrame().addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e){
                        selectorFrame.setVisible(true);
                    }
               });
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Error launching Client GUI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                selectorFrame.setVisible(true);
            }
        });
    }

    private static void launchAdminGUI(){
        SwingUtilities.invokeLater(() -> {
            try{
                AdminGUI adminGUI = new AdminGUI();

                adminGUI.getFrame().addWindowListener(new WindowAdapter() {
                   @Override
                   public void windowClosed(WindowEvent e){
                        selectorFrame.setVisible(true);
                   } 
                });
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Error launching Admin GUI: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                selectorFrame.setVisible(true);
            }
        });
    }
}
