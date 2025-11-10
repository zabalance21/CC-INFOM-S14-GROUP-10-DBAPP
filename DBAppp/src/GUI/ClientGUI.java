package GUI;

import Controller.ClientController;
import Model.Entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

public class ClientGUI {
    private ClientController clientController;
    private JFrame mainFrame;
    private JTable clientTable;
    private DefaultTableModel tableModel;

    public ClientGUI(){
        clientController = new ClientController();
        initializeGUI();
    }

    private void initializeGUI(){
        mainFrame = new JFrame("IT Services - Client Management");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLayout(new BorderLayout());

        createMenuBar();
        createTable();
        createButtonPanel();

        loadAllClients();

        mainFrame.setLocationRelativeTo(null); // this to center the window
        mainFrame.setVisible(true);
    }

    private void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        mainFrame.setJMenuBar(menuBar);
    }

    private void createTable(){
        String[] columnNames = {"Client ID", "Name", "Email", "Phone", "Address", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
    }

    private void createButtonPanel(){
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Client");
        JButton viewButton = new JButton("View Details");
        JButton updateButton = new JButton("Update Client");
        JButton deleteButton = new JButton("Delete Client");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddClientDialog());
        viewButton.addActionListener(e -> viewClientDetails());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());
        refreshButton.addActionListener(e -> loadAllClients());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAllClients(){
        tableModel.setRowCount(0);
        List<Client> clients = clientController.getAllClients();

        for (Client client : clients){
            tableModel.addRow(new Object[]{
                client.getClientId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress(),
                client.getStatus().toString()
            });
        }
    }

    private void showAddClientDialog(){
        JDialog addDialog = new JDialog(mainFrame, "Add New Client", true);
        addDialog.setLayout(new GridLayout(6, 2, 5, 5));
        addDialog.setSize(400, 300);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        
        addDialog.add(new JLabel("Name: "));
        addDialog.add(nameField);
        addDialog.add(new JLabel("Email: "));
        addDialog.add(emailField);
        addDialog.add(new JLabel("Phone: "));
        addDialog.add(phoneField);
        addDialog.add(new JLabel("Address: "));
        addDialog.add(addressField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
           try {
                Client client = new Client(
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText()
                );
                clientController.addClient(client);
                loadAllClients();
                addDialog.dispose();
                JOptionPane.showMessageDialog(mainFrame, "Client added successfully!");
           } catch(Exception ex){
                JOptionPane.showMessageDialog(mainFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
           }
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.add(saveButton);
        addDialog.add(cancelButton);
        addDialog.setLocationRelativeTo(mainFrame);
        addDialog.setVisible(true);
    }

    private void viewClientDetails(){
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);
            Client client = clientController.getClientByID(clientID);

            if (client != null){
                String details = String.format("Client ID: %s\nName: %s\nPhone: %s\nAddress: %s\nStatus: %s",
                client.getClientId(), client.getName(), client.getEmail(), client.getPhone(), client.getAddress(), client.getStatus());
                JOptionPane.showMessageDialog(mainFrame, details, "Client Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client first.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateClient(){
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);
            Client client = clientController.getClientByID(clientID);

            if (client != null){
                showUpdateClientDialog(client);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client to update.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteClient(){
        int selectedRow = clientTable.getSelectedRow();
        if(selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete client " + clientID + "?",
                                                     "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION){
                clientController.deleteClient(clientID);
                loadAllClients();
                JOptionPane.showMessageDialog(mainFrame, "Client marked as INACTIVE");
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showUpdateClientDialog(Client client){
        JDialog updateDialog = new JDialog(mainFrame, "Update Client", true);
        updateDialog.setLayout(new GridLayout(7, 2, 5, 5));
        updateDialog.setSize(400, 350);

        JTextField nameField = new JTextField(client.getName());
        JTextField emailField = new JTextField(client.getEmail());
        JTextField phoneField = new JTextField(client.getPhone());
        JTextField addressField = new JTextField(client.getAddress());

        JComboBox<ClientStatus> statusCombo = new JComboBox<>(ClientStatus.values());
        statusCombo.setSelectedItem(client.getStatus());

        updateDialog.add(new JLabel("Client ID: "));
        updateDialog.add(new JLabel(client.getClientId())); // For display, not supposed to be editable
        updateDialog.add(new JLabel("Name: "));
        updateDialog.add(nameField);
        updateDialog.add(new JLabel("Email: "));
        updateDialog.add(emailField);
        updateDialog.add(new JLabel("Phone: "));
        updateDialog.add(phoneField);
        updateDialog.add(new JLabel("Address: "));
        updateDialog.add(addressField);
        updateDialog.add(new JLabel("Status: "));
        updateDialog.add(statusCombo);

        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                client.setName(nameField.getText());
                client.setEmail(emailField.getText());
                client.setPhone(phoneField.getText());
                client.setAddress(addressField.getText());
                client.setStatus((ClientStatus) statusCombo.getSelectedItem());
                
                clientController.updateExistingClient(client);

                loadAllClients();

                updateDialog.dispose();
                JOptionPane.showMessageDialog(mainFrame, "Client updated successfully!");
            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(updateDialog, "Error: " + ex.getMessage(), "Validation error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex){
                JOptionPane.showMessageDialog(updateDialog, "Error updating client: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> updateDialog.dispose());

        updateDialog.add(saveButton);
        updateDialog.add(cancelButton);
        updateDialog.setLocationRelativeTo(mainFrame);
        updateDialog.setVisible(true);
    }

    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e){
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}
