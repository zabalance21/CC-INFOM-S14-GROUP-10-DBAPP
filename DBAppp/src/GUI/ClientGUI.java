package GUI;

import Controller.*;
import Model.DAO.*;
import Model.Entities.*;

import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;


public class ClientGUI {
    // Controllers
    private ClientController clientController;
    private ContractController contractController;
    private PaymentController paymentController;
    private ContractServiceController contractServiceController;
    private ManagerController managerController;
    private ServiceController serviceController;

    //GUI
    private JFrame mainFrame;
    private JPanel operationsPanel;

    //DAOs
    private ContractDAO contractDAO = new ContractDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private AccountManagerDAO accountManagerDAO = new AccountManagerDAO();
    private ContractServiceDao contractServiceDAO = new ContractServiceDao();

    public ClientGUI(){
        initializeControllers();
        initializeGUI();
    }

    private void initializeControllers(){
        this.clientController = new ClientController(contractDAO, invoiceDAO, clientDAO);
        this.paymentController = new PaymentController(paymentDAO, clientDAO, invoiceDAO, contractDAO);
        this.contractController = new ContractController(contractDAO, clientDAO, serviceDAO, contractServiceDAO, invoiceDAO, accountManagerDAO);
        this.contractServiceController = new ContractServiceController(contractServiceDAO, clientDAO, serviceDAO, contractDAO, invoiceDAO);
        this.managerController = new ManagerController(accountManagerDAO, contractDAO, clientDAO);
        this.serviceController = new ServiceController(contractDAO, serviceDAO, contractServiceDAO);
    }

    private void initializeGUI(){
        mainFrame = new JFrame("IT Services - Management System");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(800, 400);
        mainFrame.setLayout(new BorderLayout());

        createOperationsPanel();
        createMenuBar();

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public JFrame getFrame(){
        return mainFrame;
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

    private void createOperationsPanel(){
        operationsPanel = new JPanel(new GridBagLayout());
        operationsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("IT Services Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        operationsPanel.add(titleLabel, gbc);

        // Create Contract Button
        JButton createContractBtn = createStyledButton("Create New Contract");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        operationsPanel.add(createContractBtn, gbc);

        // Process Payment Button
        JButton processPaymentBtn = createStyledButton("Process Payment");
        gbc.gridx = 1;
        operationsPanel.add(processPaymentBtn, gbc);

        // Contract Renewal Button
        JButton contractRenewalBtn = createStyledButton("Contract Renewal");
        gbc.gridx = 0;
        gbc.gridy = 2;
        operationsPanel.add(contractRenewalBtn, gbc);

        // Add action listeners
        createContractBtn.addActionListener(e -> showCreateContractDialog());
        processPaymentBtn.addActionListener(e -> showProcessPaymentDialog());
        contractRenewalBtn.addActionListener(e -> showContractRenewalDialog());

        mainFrame.add(operationsPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    private void showCreateContractDialog() {
        JDialog contractDialog = new JDialog(mainFrame, "Create New Contract", true);
        contractDialog.setLayout(new GridBagLayout());
        contractDialog.setSize(600, 500);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Client Selection
        JLabel clientLabel = new JLabel("Client Name:");
        clientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        contractDialog.add(clientLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> clientCombo = new JComboBox<>();
        clientCombo.addItem("-- Select Client --");
        List<Client> activeClients = clientController.getAllActiveClients();
        for (Client client : activeClients) {
            clientCombo.addItem(client.getName() + " (" + client.getClientId() + ")");
        }
        contractDialog.add(clientCombo, gbc);

        // Service Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel serviceLabel = new JLabel("Services Available:");
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        contractDialog.add(serviceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JPanel servicesPanel = new JPanel(new GridLayout(0, 1));
        servicesPanel.setBorder(BorderFactory.createTitledBorder("Available Services"));
        JScrollPane servicesScroll = new JScrollPane(servicesPanel);
        servicesScroll.setPreferredSize(new Dimension(350, 150));
        
        List<Service> availableServices = serviceDAO.getAvailableServicesOnly();
        JCheckBox[] serviceCheckboxes = new JCheckBox[availableServices.size()];
        
        for (int i = 0; i < availableServices.size(); i++) {
            Service service = availableServices.get(i);
            serviceCheckboxes[i] = new JCheckBox(
                String.format("%s - ₱%,.2f/month - %s", 
                    service.getName(), 
                    service.getRate(), 
                    service.getDescription())
            );
            servicesPanel.add(serviceCheckboxes[i]);
        }
        contractDialog.add(servicesScroll, gbc);

        // Manager Selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel managerLabel = new JLabel("Account Manager:");
        managerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        contractDialog.add(managerLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JComboBox<String> managerCombo = new JComboBox<>();
        managerCombo.addItem("-- Select Manager --");
        List<AccountManager> managers = accountManagerDAO.getAllActiveManagers();
        for (AccountManager manager : managers) {
            managerCombo.addItem(manager.getName() + " (" + manager.getManagerID() + ")");
        }
        contractDialog.add(managerCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Contract");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            try {
                if (clientCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(contractDialog, "Please select a client.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (managerCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(contractDialog, "Please select a manager.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedClientText = (String) clientCombo.getSelectedItem();
                String clientName = selectedClientText.split(" \\(")[0];
                Client selectedClient = clientController.getClientByName(clientName);
                
                String selectedManagerText = (String) managerCombo.getSelectedItem();
                String managerName = selectedManagerText.split(" \\(")[0];
                AccountManager selectedManager = managerController.getManagerByName(managerName);
                
                // Get selected services
                List<String> selectedServicesNames = new ArrayList<>();
                int serviceCount = 0;
                for (JCheckBox checkbox : serviceCheckboxes) {
                    if (checkbox.isSelected()) {
                        String serviceText = checkbox.getText();
                        String serviceName = serviceText.split(" - ")[0];
                        selectedServicesNames.add(serviceName);
                        serviceCount++;
                    }
                }

                List<String> selectedServicesIDs = new ArrayList<>();
                for (String name : selectedServicesNames){
                    Service service = serviceController.getServiceByName(name);
                    selectedServicesIDs.add(service.getServiceId());
                }
                
                if (serviceCount == 0) {
                    JOptionPane.showMessageDialog(contractDialog, "Please select at least one service.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = contractController.createContractAndInvoice(
                    selectedClient.getName(), 
                    selectedServicesIDs,
                    selectedManager.getManagerID()
                );

                if (success) {
                    JOptionPane.showMessageDialog(contractDialog, 
                        "Contract and invoice created successfully!\n\n" +
                        "Client: " + selectedClient.getName() + "\n" +
                        "Services: " + selectedServicesNames.toString() + "\n" +
                        "Manager: " + selectedManager.getName(),
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    contractDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(contractDialog, "Failed to create contract. Please check the inputs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(contractDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> contractDialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        contractDialog.add(buttonPanel, gbc);

        contractDialog.setLocationRelativeTo(mainFrame);
        contractDialog.setVisible(true);
    }

    private void showProcessPaymentDialog() {
        JDialog paymentDialog = new JDialog(mainFrame, "Process Payment", true);
        paymentDialog.setLayout(new GridBagLayout());
        paymentDialog.setSize(500, 350);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Client Selection
        JLabel clientLabel = new JLabel("Client Name:");
        clientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentDialog.add(clientLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> clientCombo = new JComboBox<>();
        clientCombo.addItem("-- Select Client --");
        List<Client> activeClients = clientController.getAllActiveClients();
        for (Client client : activeClients) {
            clientCombo.addItem(client.getName() + " (" + client.getClientId() + ")");
        }
        paymentDialog.add(clientCombo, gbc);

        // Invoice Selection (dynamically populated based on client)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel invoiceLabel = new JLabel("Invoice to Pay:");
        invoiceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentDialog.add(invoiceLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> invoiceCombo = new JComboBox<>();
        invoiceCombo.addItem("-- Select Invoice --");
        paymentDialog.add(invoiceCombo, gbc);

        // Invoice Details Panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel invoiceDetailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        invoiceDetailsPanel.setBorder(BorderFactory.createTitledBorder("Invoice Details"));
        invoiceDetailsPanel.setPreferredSize(new Dimension(450, 80));
        
        JLabel amountLabel = new JLabel("Invoice Amount:");
        JLabel amountValue = new JLabel("₱0.00");
        JLabel dueDateLabel = new JLabel("Due Date:");
        JLabel dueDateValue = new JLabel("-");
        JLabel statusLabel = new JLabel("Status:");
        JLabel statusValue = new JLabel("-");
        
        invoiceDetailsPanel.add(amountLabel);
        invoiceDetailsPanel.add(amountValue);
        invoiceDetailsPanel.add(dueDateLabel);
        invoiceDetailsPanel.add(dueDateValue);
        invoiceDetailsPanel.add(statusLabel);
        invoiceDetailsPanel.add(statusValue);
        
        paymentDialog.add(invoiceDetailsPanel, gbc);

        // Amount to Pay
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel amountPayLabel = new JLabel("Amount to Pay:");
        amountPayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentDialog.add(amountPayLabel, gbc);
        
        gbc.gridx = 1;
        JTextField amountField = new JTextField();
        paymentDialog.add(amountField, gbc);

        // Update invoices when client changes
        clientCombo.addActionListener(e -> {
            invoiceCombo.removeAllItems();
            invoiceCombo.addItem("-- Select Invoice --");
            amountValue.setText("₱0.00");
            dueDateValue.setText("-");
            statusValue.setText("-");
            amountField.setText("");
            
            if (clientCombo.getSelectedIndex() > 0) {
                String selectedClientText = (String) clientCombo.getSelectedItem();
                String clientId = selectedClientText.split("\\(")[1].replace(")", "");
                Client selectedClient = clientController.getClientByID(clientId);
                
                if (selectedClient != null) {
                    List<Invoice> invoices = invoiceDAO.getActiveInvoicesForClient(selectedClient.getClientId());
                    for (Invoice invoice : invoices) {
                        if (invoice.getStatus() != InvoiceStatus.PAID) {
                            String invoiceText = String.format("INV-%s | ₱%,.2f | Due: %s | %s", 
                                invoice.getInvoiceId().substring(4), // Remove "INV-" prefix for display
                                invoice.getAmount(),
                                invoice.getDueDate(),
                                invoice.getStatus());
                            invoiceCombo.addItem(invoiceText);
                        }
                    }
                }
            }
        });

        // Update invoice details when invoice selection changes
        invoiceCombo.addActionListener(e -> {
            if (invoiceCombo.getSelectedIndex() > 0 && clientCombo.getSelectedIndex() > 0) {
                String invoiceText = (String) invoiceCombo.getSelectedItem();
                String invoiceId = "INV-" + invoiceText.split(" \\| ")[0].substring(4);
                
                Invoice selectedInvoice = invoiceDAO.getInvoiceById(invoiceId);
                if (selectedInvoice != null) {
                    amountValue.setText(String.format("₱%,.2f", selectedInvoice.getAmount()));
                    dueDateValue.setText(selectedInvoice.getDueDate().toString());
                    statusValue.setText(selectedInvoice.getStatus().toString());
                    amountField.setText(selectedInvoice.getAmount().toString());
                }
            } else {
                amountValue.setText("₱0.00");
                dueDateValue.setText("-");
                statusValue.setText("-");
                amountField.setText("");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton processButton = new JButton("Process Payment");
        JButton cancelButton = new JButton("Cancel");

        processButton.addActionListener(e -> {
            try {
                if (clientCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(paymentDialog, "Please select a client.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (invoiceCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(paymentDialog, "Please select an invoice.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedClientText = (String) clientCombo.getSelectedItem();
                String clientId = selectedClientText.split("\\(")[1].replace(")", "");
                
                String invoiceText = (String) invoiceCombo.getSelectedItem();
                String invoiceId = "INV-" + invoiceText.split(" \\| ")[0].substring(4);
                
                if (amountField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(paymentDialog, "Please enter payment amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = paymentController.processPayment(
                    clientId, 
                    invoiceId, 
                    new BigDecimal(amountField.getText())
                );

                if (success) {
                    JOptionPane.showMessageDialog(paymentDialog, 
                        "Payment processed successfully!\n\n" +
                        "Client: " + clientCombo.getSelectedItem() + "\n" +
                        "Invoice: " + invoiceId + "\n" +
                        "Amount: ₱" + amountField.getText(),
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    paymentDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(paymentDialog, "Payment failed. Please check the inputs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> paymentDialog.dispose());

        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        paymentDialog.add(buttonPanel, gbc);

        paymentDialog.setLocationRelativeTo(mainFrame);
        paymentDialog.setVisible(true);
    }

    private void showContractRenewalDialog() {
        JDialog renewalDialog = new JDialog(mainFrame, "Contract Renewal", true);
        renewalDialog.setLayout(new GridBagLayout());
        renewalDialog.setSize(500, 300);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Client Selection
        JLabel clientLabel = new JLabel("Client Name:");
        clientLabel.setFont(new Font("Arial", Font.BOLD, 12));
        renewalDialog.add(clientLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> clientCombo = new JComboBox<>();
        clientCombo.addItem("-- Select Client --");
        List<Client> activeClients = clientController.getAllActiveClients();
        for (Client client : activeClients) {
            clientCombo.addItem(client.getName() + " (" + client.getClientId() + ")");
        }
        renewalDialog.add(clientCombo, gbc);

        // Contract Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel contractLabel = new JLabel("Contract to Renew:");
        contractLabel.setFont(new Font("Arial", Font.BOLD, 12));
        renewalDialog.add(contractLabel, gbc);
        
        gbc.gridx = 1;
        JComboBox<String> contractCombo = new JComboBox<>();
        contractCombo.addItem("-- Select Contract --");
        renewalDialog.add(contractCombo, gbc);

        // Update contracts when client changes
        clientCombo.addActionListener(e -> {
            contractCombo.removeAllItems();
            contractCombo.addItem("-- Select Contract --");
            
            if (clientCombo.getSelectedIndex() > 0) {
                String selectedClientText = (String) clientCombo.getSelectedItem();
                String clientId = selectedClientText.split("\\(")[1].replace(")", "");
                Client selectedClient = clientController.getClientByID(clientId);
                
                if (selectedClient != null) {
                    List<Contract> closedContracts = contractDAO.getClosedContractsForClient(selectedClient.getClientId());
                    if (closedContracts.isEmpty()) {
                        //Do nothing
                    } else {
                        for (Contract contract : closedContracts) {
                            String contractText = String.format("CT-%s | %s to %s | %s", 
                                contract.getContractID().substring(3), // Remove "CT-" prefix
                                contract.getStartDate(),
                                contract.getEndDate(),
                                contract.getContractStatus());
                            contractCombo.addItem(contractText);
                        }
                    }
                }
            }
        });


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton renewButton = new JButton("Renew Contract");
        JButton cancelButton = new JButton("Cancel");

        renewButton.addActionListener(e -> {
            try {
                if (clientCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(renewalDialog, "Please select a client.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (contractCombo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(renewalDialog, 
                        "No closed contracts available for renewal for this client.\n\n" +
                        "To renew a contract, it must first be closed or expired.",
                        "No Contracts Available", 
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String selectedClientText = (String) clientCombo.getSelectedItem();
                String clientId = selectedClientText.split("\\(")[1].replace(")", "");
                
                String contractText = (String) contractCombo.getSelectedItem();
                String contractId = "CT-" + contractText.split(" \\| ")[0].substring(3);
                
                int confirm = JOptionPane.showConfirmDialog(
                    renewalDialog,
                    "Renew this contract for 1 year?\n\n" +
                    "This will:\n" +
                    "• Reactivate the contract\n" +
                    "• Reactivate ALL services in the contract\n" +
                    "• Create a new invoice\n" +
                    "• Set new 1-year dates",
                    "Confirm Contract Renewal",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    ContractRenewalResult result = contractServiceController.renewContract(
                        clientId, 
                        contractId
                    );

                    if (result != null) {
                        String message = String.format(
                            "Contract Renewal Successful!\n\n" + 
                            "Old Contract Details:\n" + 
                            "• Start: %s\n• End: %s\n• Status: %s\n\n" + 
                            "New Contract Details:\n" + 
                            "• Start: %s\n• End: %s\n• Status: %s\n\n" +
                            "All services have been reactivated automatically.", 
                            result.oldStart, result.oldEnd, result.oldStatus,
                            result.newStart, result.newEnd, result.newStatus
                        );
                        JOptionPane.showMessageDialog(renewalDialog, message, "Contract Renewed", JOptionPane.INFORMATION_MESSAGE);
                        renewalDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(renewalDialog, "Contract renewal failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(renewalDialog, "Error during renewal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> renewalDialog.dispose());

        buttonPanel.add(renewButton);
        buttonPanel.add(cancelButton);
        renewalDialog.add(buttonPanel, gbc);

        renewalDialog.setLocationRelativeTo(mainFrame);
        renewalDialog.setVisible(true);
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