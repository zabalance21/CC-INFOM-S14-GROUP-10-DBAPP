package GUI;

import Controller.*;
import Model.DAO.*;
import Model.Entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;
import java.math.BigDecimal;

public class ManagementGUI {
    // Controllers
    private ClientController clientController;
    private ContractController contractController;
    private PaymentController paymentController;
    private ContractServiceController contractServiceController;

    //GUI
    private JFrame mainFrame;
    private JTable clientTable;
    private DefaultTableModel tableModel;

    //DAOs
    private ContractDAO contractDAO = new ContractDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private AccountManagerDAO accountManagerDAO = new AccountManagerDAO();
    private BranchDAO branchDAO = new BranchDAO();
    private ContractServiceDao contractServiceDAO = new ContractServiceDao();




    public ManagementGUI(){
        initializeControllers();
        initializeGUI();
    }

    private void initializeControllers(){
        this.clientController = new ClientController(contractDAO, invoiceDAO, clientDAO);
        this.paymentController = new PaymentController(paymentDAO, clientDAO, invoiceDAO, contractDAO, contractServiceDAO);
        this.contractController = new ContractController(contractDAO, clientDAO, serviceDAO, branchDAO, contractServiceDAO, invoiceDAO, accountManagerDAO);
        this.contractServiceController = new ContractServiceController(contractServiceDAO, clientDAO, serviceDAO, contractDAO, invoiceDAO);
    }

    private void initializeGUI(){
        mainFrame = new JFrame("IT Services - Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLayout(new BorderLayout());

        createMenuBar();
        createTable();
        createButtonPanel();

        loadAllClients();

        mainFrame.setLocationRelativeTo(null); // this to center the window
        mainFrame.setVisible(true);
    }

    private void createMenuBar(){
        //File Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        //Operations Menu
        JMenu operationsMenu = new JMenu("Operations");
        JMenuItem contractItem = new JMenuItem("Create Contract");
        JMenuItem paymentItem = new JMenuItem("Process Payment");
        JMenuItem renewalItem = new JMenuItem("Contract Renewal");

        contractItem.addActionListener(e -> showCreateContractDialog());
        paymentItem.addActionListener(e -> showProcessPaymentDialog());
        renewalItem.addActionListener(e -> showContractRenewalDialog());

        operationsMenu.add(contractItem);
        operationsMenu.add(paymentItem);
        operationsMenu.add(renewalItem);

        menuBar.add(fileMenu);
        menuBar.add(operationsMenu);
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
        JButton contractsButton = new JButton("View Contracts");
        JButton invoicesButton = new JButton("View Invoices");

        addButton.addActionListener(e -> showAddClientDialog());
        viewButton.addActionListener(e -> viewClientDetails());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());
        refreshButton.addActionListener(e -> loadAllClients());
        contractsButton.addActionListener(e -> viewClientContracts());
        invoicesButton.addActionListener(e -> viewClientInvoices());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);    
        buttonPanel.add(contractsButton);
        buttonPanel.add(invoicesButton);
        buttonPanel.add(refreshButton);

        mainFrame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAllClients(){
        tableModel.setRowCount(0);
        List<Client> clients = clientController.getAllActiveClients();

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
            int confirm = JOptionPane.showConfirmDialog(
            mainFrame, "Are you sure you want to delete client " + clientID + "?",
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

    private void showCreateContractDialog(){
        JDialog contractDialog = new JDialog(mainFrame, "Create New Contract", true);
        contractDialog.setLayout(new GridLayout(6, 2, 5, 5));
        contractDialog.setSize(500, 400);

        JTextField clientNameField = new JTextField();
        JTextField serviceIDField = new JTextField();
        JTextField managerIDField = new JTextField();

        contractDialog.add(new JLabel("Client Name: "));
        contractDialog.add(clientNameField);
        contractDialog.add(new JLabel("Service Name: "));
        contractDialog.add(serviceIDField);
        contractDialog.add(new JLabel("Manager Name: "));
        contractDialog.add(managerIDField);

        JButton createButton = new JButton("Create Contract");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            try{
                boolean success = contractController.createContractAndInvoice(clientNameField.getText(), serviceIDField.getText(), managerIDField.getText());

                if (success){
                    JOptionPane.showMessageDialog(contractDialog, "Contract and invoice created successfully!");
                    contractDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(contractDialog, "Failed to create contract. Please check the inputs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(contractDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> contractDialog.dispose());

        contractDialog.add(createButton);
        contractDialog.add(cancelButton);
        contractDialog.setLocationRelativeTo(mainFrame);
        contractDialog.setVisible(true);
    }

    private void showProcessPaymentDialog(){
        JDialog paymentDialog = new JDialog(mainFrame, "Process Payment", true);
        paymentDialog.setLayout(new GridLayout(5, 2, 5, 5));
        paymentDialog.setSize(400, 300);

        JTextField clientIDField = new JTextField();
        JTextField invoiceIDField = new JTextField();
        JTextField amountField = new JTextField();

        paymentDialog.add(new JLabel("Client ID: "));
        paymentDialog.add(clientIDField);
        paymentDialog.add(new JLabel("Invoice ID: "));
        paymentDialog.add(invoiceIDField);
        paymentDialog.add(new JLabel("Amount: "));
        paymentDialog.add(amountField);

        JButton processButton = new JButton("Process Payment");
        JButton cancelButton = new JButton("Cancel");

        processButton.addActionListener(e -> {
            try {
                boolean success = paymentController.processPayment(clientIDField.getText(), invoiceIDField.getText(), new BigDecimal(amountField.getText()));

                if (success){
                    JOptionPane.showMessageDialog(paymentDialog, "Payment processed successfully!");
                    paymentDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(paymentDialog, "Payment failed. Please check the inputs", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(paymentDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> paymentDialog.dispose());

        paymentDialog.add(processButton);
        paymentDialog.add(cancelButton);
        paymentDialog.setLocationRelativeTo(mainFrame);
        paymentDialog.setVisible(true);
    }

    private void showContractRenewalDialog(){
        int selectedRow = clientTable.getSelectedRow();
        if(selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);

            try{
                ContractRenewalResult result = contractServiceController.renewContract(clientID);

                if (result != null){
                    String message = String.format(
                        "Contract Renewal Successful!\n\n" + 
                        "Old Contract: \n" + 
                        "Start: %s\nEnd: %s\nStatus: %s\n\n" + 
                        "New Contract: \n" + "Start: %s\nEnd: %s\nStatus: %s", 
                        result.oldStart, result.oldEnd, result.oldStatus,
                        result.newStart, result.newEnd, result.newStatus
                        );
                        JOptionPane.showMessageDialog(mainFrame, message, "Contract Renewal", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Contract renewal failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }catch (Exception ex){
                JOptionPane.showMessageDialog(mainFrame, "Error" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client first.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewClientContracts(){
        int selectedRow = clientTable.getSelectedRow();
        if(selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);
            List<Contract> contracts = contractController.getContractByClientID(clientID);

            StringBuilder sb = new StringBuilder();
            sb.append("Contracts for Client: ").append(clientID).append("\n\n");

            for (Contract contract : contracts){
                sb.append(String.format("Contract ID: %s\nStatus: %s\nStart: %s\nEnd: %s\n\n", 
                contract.getContractID(), contract.getContractStatus(),
                contract.getStartDate(), contract.getEndDate()));
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Client Contracts", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client first.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewClientInvoices(){
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0){
            String clientID = (String) tableModel.getValueAt(selectedRow, 0);
            List<Invoice> invoices = invoiceDAO.getInvoicesByClientID(clientID);

            StringBuilder sb = new StringBuilder();
            sb.append("Invoices for Client: ").append(clientID).append("\n\n");
            
            for (Invoice invoice: invoices){
                sb.append(String.format("Invoice ID: %s\nAmount: ₱%s\nStatus: %s\nDue: %s\n\n",
                invoice.getInvoiceId(), invoice.getAmount(),
                invoice.getStatus(), invoice.getDueDate()));
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Client Invoices", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client first.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e){
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new ManagementGUI());
    }
}
