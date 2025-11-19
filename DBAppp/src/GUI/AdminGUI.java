package GUI;

import Controller.*;
import Model.DAO.*;
import Model.Entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.time.temporal.ChronoUnit;

public class AdminGUI {
    // Controllers
    private ClientController clientController;
    private ContractController contractController;
    private PaymentController paymentController;
    private ContractServiceController contractServiceController;
    private ManagerController managerController;
    private BranchController branchController;
    private ServiceController serviceController;

    //DAOs
    private ContractDAO contractDAO = new ContractDAO();
    private ClientDAO clientDAO = new ClientDAO();
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private AccountManagerDAO accountManagerDAO = new AccountManagerDAO();
    private BranchDAO branchDAO = new BranchDAO();
    private ContractServiceDao contractServiceDAO = new ContractServiceDao();

    //GUI
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;

    private JTable clientTable, contractTable, serviceTable, managerTable, branchTable, invoiceTable;
    private DefaultTableModel clientModel, contractModel, serviceModel, managerModel, branchModel, invoiceModel;

    public AdminGUI(){
        initializeControllers();
        initializeGUI();
    }

    private void initializeControllers(){
        this.clientController = new ClientController(contractDAO, invoiceDAO, clientDAO);
        this.paymentController = new PaymentController(paymentDAO, clientDAO, invoiceDAO, contractDAO, contractServiceDAO);
        this.contractController = new ContractController(contractDAO, clientDAO, serviceDAO, contractServiceDAO, invoiceDAO, accountManagerDAO);
        this.contractServiceController = new ContractServiceController(contractServiceDAO, clientDAO, serviceDAO, contractDAO, invoiceDAO);
        this.managerController = new ManagerController(accountManagerDAO, contractDAO, clientDAO);
        this.branchController = new BranchController(branchDAO, clientDAO, accountManagerDAO, contractDAO);
        this.serviceController = new ServiceController(contractDAO, serviceDAO, contractServiceDAO);
    }   

    private void initializeGUI(){
        mainFrame = new JFrame("IT Services - Admin System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1400,900);
        mainFrame.setLayout(new BorderLayout());

        createMenuBar();
        createTabbedInterface();
        
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        JMenuItem exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(e -> refreshAllData());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem financialReportItem = new JMenuItem("Financial Overview");
        JMenuItem clientReportItem = new JMenuItem("Client Analytics");
        JMenuItem serviceReportItem = new JMenuItem("Service Performance");

        financialReportItem.addActionListener(e -> showFinancialReport());
        clientReportItem.addActionListener(e -> showClientAnalytics());
        serviceReportItem.addActionListener(e -> showServicePerformance());

        reportsMenu.add(financialReportItem);
        reportsMenu.add(clientReportItem);
        reportsMenu.add(serviceReportItem);

        menuBar.add(fileMenu);
        menuBar.add(reportsMenu);
        mainFrame.setJMenuBar(menuBar);
    }

    private void createTabbedInterface(){
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Clients", createClientManagementPanel());
        tabbedPane.addTab("Contracts", createContractsManagementPanel());
        tabbedPane.addTab("Invoices & Payments", createInvoicePaymentPanel());
        tabbedPane.addTab("Services", createServiceManagementPanel());
        tabbedPane.addTab("Branches", createBranchManagementPanel());
        tabbedPane.addTab("Managers", createManagerManagementPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        mainFrame.add(tabbedPane, BorderLayout.CENTER);
    }

    private void showFinancialReport(){
        Map<String, Map<String, BigDecimal>> monthlyCollections = paymentController.getCollectionsPerMonth();
        Map<String, Map<String, BigDecimal>> quarterlyRevenue = paymentController.getRevenuePerQuarter();

        StringBuilder report = new StringBuilder();
        report.append("=== FINANCIAL REPORT ===\n\n");
        
        report.append("Monthly Collection:\n");
        for(Map.Entry<String, Map<String, BigDecimal>> entry : monthlyCollections.entrySet()){
            report.append("Client: ").append(entry.getKey()).append(("\n"));
            for(Map.Entry<String, BigDecimal> monthEntry: entry.getValue().entrySet()){
                report.append("  ").append(monthEntry.getKey()).append(": ₱").append(monthEntry.getValue()).append(("\n"));
            }
        }

        report.append("\nQuarterly Revenue by Service:\n");
        for(Map.Entry<String, Map<String,BigDecimal>> entry : quarterlyRevenue.entrySet()){
            report.append("Service: ").append(entry.getKey()).append("\n");
            for(Map.Entry<String, BigDecimal> quarterEntry: entry.getValue().entrySet()){
                report.append("  ").append(quarterEntry.getKey()).append(": ₱").append(quarterEntry.getValue()).append("\n");
            }
        }

        displayReport("Financial Report", report.toString());
    }

    private void showClientAnalytics(){
        List<Client> clients = clientController.getAllClients();
        StringBuilder report = new StringBuilder();
        report.append("=== CLIENT ANALYTICS REPORT ===\n\n");

        report.append("Total Clients: ").append(clients.size()).append("\n");
        report.append("Active Clients: ").append(clientController.getAllActiveClients().size()).append("\n");
        report.append("Inactive Clients: ").append(clients.size() - clientController.getAllActiveClients().size()).append("\n\n");

        report.append("Client Details:\n");
        for (Client client : clients){
            List<Contract> contracts = contractDAO.getContractsByClientId(client.getClientId());
            List<Invoice> invoices = invoiceDAO.getInvoicesByClientID(client.getClientId());

            report.append(client.getName()).append(" (").append(client.getClientId()).append(")\n");
            report.append("  Contracts: ").append(contracts.size()).append("\n");
            report.append("  Invoices: ").append(invoices.size()).append("\n");
            report.append("  Status: ").append(client.getStatus()).append("\n\n");
        }
        displayReport("Client Analytics", report.toString());
    }

    private void showServicePerformance(){
        List<Service> services = serviceController.getAllServices();
        Map<String, Map<String, BigDecimal>> quarterlyRevenue = paymentController.getRevenuePerQuarter();

        StringBuilder report = new StringBuilder();
        report.append("=== SERVICE PERFORMANCE REPORT ===\n\n");

        for (Service service : services){
            report.append("Service: ").append(service.getName()).append("\n");
            report.append("  Rate: ₱").append(service.getRate()).append("\n");
            report.append("  Availability: ").append(service.getAvailability()).append("\n");

            Map<String, BigDecimal> serviceRevenue = quarterlyRevenue.get(service.getName());

            if (serviceRevenue != null && !serviceRevenue.isEmpty()){
                report.append("  Revenue:\n");
                for(Map.Entry<String, BigDecimal> entry : serviceRevenue.entrySet()){
                    report.append("    ").append(entry.getKey()).append(": ₱").append(entry.getValue()).append("\n");
                }
            } else {
                report.append("  Revenue: No revenue recorded\n");
            }
            report.append("\n");
        }
        displayReport("Service Performance", report.toString());
    }

    private void displayReport(String title, String content){
        JDialog reportDialog = new JDialog(mainFrame, title, true);
        reportDialog.setLayout(new BorderLayout());
        reportDialog.setSize(600, 500);

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        reportDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> reportDialog.dispose());
        reportDialog.add(closeButton, BorderLayout.SOUTH);

        reportDialog.setLocationRelativeTo(mainFrame);
        reportDialog.setVisible(true);
    }

    private void refreshAllData(){
        loadClientData();
        loadContractData();
        loadServiceData();
        loadBranchData();
        loadManagerData();
        loadInvoiceData();
        JOptionPane.showMessageDialog(mainFrame, "All data refreshed successfully!");
    }

    //DASHBOARD PANEL
    private JPanel createDashboardPanel(){
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int totalClients = clientDAO.getAllClients().size();
        int activeClients = clientDAO.getActiveClientsCount();
        int totalContracts = contractDAO.getAllContractsCount();
        int activeContracts = contractDAO.getActiveContractsCount();
        int totalServices = serviceController.getAllServices().size();
        int availableServices = serviceController.getAvailableServices().size();
        int totalBranches = branchDAO.getAllBranches().size();
        int operationalBrances = branchController.getAllOperationalBranches().size();
        int activeManagers = managerController.getAllActiveManagers().size();
        BigDecimal monthlyRevenue = paymentController.getMonthlyRevenue();
        int overdueInvoices = invoiceDAO.getOverdueInvoicesCount();
        int unpaidInvoices = invoiceDAO.getUnpaidInvoicesCount();

        summaryPanel.add(createMetricCard("Total Clients", String.valueOf(totalClients), Color.BLUE));
        summaryPanel.add(createMetricCard("Active Clients", String.valueOf(activeClients), Color.GREEN));
        summaryPanel.add(createMetricCard("Total Contracts", String.valueOf(totalContracts), Color.ORANGE));
        summaryPanel.add(createMetricCard("Active Contracts", String.valueOf(activeContracts), Color.CYAN));
        summaryPanel.add(createMetricCard("Total Services", String.valueOf(totalServices), Color.MAGENTA));
        summaryPanel.add(createMetricCard("Available Services", String.valueOf(availableServices), new Color(0, 128, 0)));
        summaryPanel.add(createMetricCard("Operational Branches", String.valueOf(operationalBrances), Color.RED));
        summaryPanel.add(createMetricCard("Active Managers", String.valueOf(activeManagers), Color.PINK));
        summaryPanel.add(createMetricCard("Monthly Revenue", "₱" + monthlyRevenue, new Color(0, 100, 0)));
        summaryPanel.add(createMetricCard("Overdue Invoices", String.valueOf(overdueInvoices), Color.RED));
        summaryPanel.add(createMetricCard("Unpaid Invoices", String.valueOf(unpaidInvoices), Color.ORANGE));
        summaryPanel.add(createMetricCard("Total Brances", String.valueOf(totalBranches), new Color(128, 0, 128)));

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));

        String[] activityColumns = {"Type", "Description", "Date", "Status"};
        DefaultTableModel activityModel = new DefaultTableModel(activityColumns, 0);
        JTable activityTable = new JTable(activityModel);

        loadRecentActivities(activityModel);
        activityPanel.add(new JScrollPane(activityTable), BorderLayout.CENTER);

        //Alerts
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBorder(BorderFactory.createTitledBorder("System Alerts"));

        JTextArea alertsArea = new JTextArea();
        alertsArea.setEditable(false);
        alertsArea.setText(checkSystemAlerts());

        alertsPanel.add(new JScrollPane(alertsArea), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(activityPanel);
        centerPanel.add(alertsPanel);

        dashboardPanel.add(summaryPanel, BorderLayout.NORTH);
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JPanel createMetricCard(String title, String value, Color color){
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }


    private String checkSystemAlerts(){
        StringBuilder alerts = new StringBuilder();

        List<Client> clients = clientController.getAllActiveClients();
        int overdueCount = 0;
        for (Client client : clients){
            List<Invoice> invoices = invoiceDAO.getInvoicesByClientID(client.getClientId());
            for (Invoice invoice : invoices){
                if(invoice.getStatus() == InvoiceStatus.OVERDUE){
                    overdueCount++;
                }
            }
        }
        if(overdueCount > 0){
            alerts.append(overdueCount).append(" overdue invoices\n");
        }

        int expiringCount = 0;
        List<Contract> contracts = contractController.getAllContracts();
        for (Contract contract : contracts){
            if(contract.getEndDate().isBefore(LocalDate.now().plusDays(30))){
                expiringCount++;
            }
        }
        if (expiringCount > 0){
            alerts.append(expiringCount).append(" contracts expiring soon\n");
        }
        if(alerts.length() == 0){
            alerts.append("All systems normal\n");
            alerts.append("No critical alerts\n");
        }
        return alerts.toString();
    }

    private void loadRecentActivities(DefaultTableModel activityModel){
        activityModel.setRowCount(0);

        List<Payment> recentPayments = paymentDAO.getRecentPayments(30);
        for(Payment payment : recentPayments){
            Invoice invoice = invoiceDAO.getInvoiceById(payment.getInvoiceId());
            if(invoice != null){
                activityModel.addRow(new Object[]{
                    "Payment",
                    payment.getInvoiceId() + " - ₱" + payment.getAmount(),
                    payment.getPaymentDate(),
                    "Completed"
                });
            }
        }

        List<Contract> recentContracts = contractDAO.getRecentContracts(30);
        for(Contract contract : recentContracts){
            Client client = clientController.getClientByID(contract.getClientID());
            if(client != null){
                activityModel.addRow(new Object[]{
                    "Contract",
                    "New contract for " + client.getName(),
                    contract.getStartDate(),
                    contract.getContractStatus().toString()
                });
            }
        }

        List<Invoice> overdueInvoices = invoiceDAO.getOverdueInvoices();
        for(Invoice invoice : overdueInvoices){
            Client client = clientController.getClientByInvoice(invoice);
            if(client != null){
                activityModel.addRow(new Object[]{
                    "Invoice",
                    "Overdue invoice for " + client.getName(),
                    invoice.getDueDate(),
                    "OVERDUE"
                });
            }
        }
    }

    //Client Management Panel
    private JPanel createClientManagementPanel(){
        JPanel clientPanel = new JPanel(new BorderLayout());

        JToolBar toolBar=new JToolBar();
        JButton addButton = new JButton("Add Client");
        JButton editButton = new JButton("Edit Client");
        JButton deleteButton = new JButton("Delete Client");
        JButton historyButton = new JButton("View History");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddClientDialog());
        editButton.addActionListener(e -> editSelectedClient());
        deleteButton.addActionListener(e -> deleteSelectedClient());
        historyButton.addActionListener(e -> showClientHistory());
        refreshButton.addActionListener(e -> loadClientData());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(historyButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Client ID", "Name", "Email", "Phone", "Address", "Status"};
        clientModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        clientTable = new JTable(clientModel);

        loadClientData();

        clientPanel.add(toolBar, BorderLayout.NORTH);
        clientPanel.add(new JScrollPane(clientTable), BorderLayout.CENTER);

        return clientPanel;
    }

    private void loadClientData(){
        clientModel.setRowCount(0);
        List<Client> clients = clientController.getAllClients();
        for (Client client : clients){
            clientModel.addRow(new Object[]{
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
        JDialog dialog = new JDialog(mainFrame, "Add New Client", true);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));
        dialog.setSize(400, 300);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try{
                Client client = new Client(
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText()
                );

                if(clientController.addClient(client)){
                    loadClientData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Client added successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Client already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void editSelectedClient(){
        int selectedRow = clientTable.getSelectedRow();
        if(selectedRow >= 0){
            String clientID = (String) clientModel.getValueAt(selectedRow, 0);
            Client client = clientController.getClientByID(clientID);

            if(client != null){
                JDialog dialog = new JDialog(mainFrame, "Edit Client", true);
                dialog.setLayout(new GridLayout(7, 2, 5, 5));
                dialog.setSize(400, 300);

                JTextField nameField = new JTextField(client.getName());
                JTextField emailField = new JTextField(client.getEmail());
                JTextField phoneField = new JTextField(client.getPhone());
                JTextField addressField = new JTextField(client.getAddress());
                JComboBox<ClientStatus> statusCombo = new JComboBox<>(ClientStatus.values());
                statusCombo.setSelectedItem(client.getStatus());

                dialog.add(new JLabel("Client ID:"));
                dialog.add(new JLabel(client.getClientId()));
                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Email:"));
                dialog.add(emailField);
                dialog.add(new JLabel("Phone:"));
                dialog.add(phoneField);
                dialog.add(new JLabel("Address:"));
                dialog.add(addressField);
                dialog.add(new JLabel("Status:"));
                dialog.add(statusCombo);

                JButton saveButton = new JButton("Save Changes");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(e -> {
                    try{
                        Client updatedClient = new Client(
                            client.getClientId(),
                            nameField.getText(),
                            emailField.getText(), 
                            phoneField.getText(),
                            addressField.getText()
                        );

                        updatedClient.setStatus((ClientStatus) statusCombo.getSelectedItem());

                        clientController.updateExistingClient(updatedClient);
                        loadClientData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(mainFrame, "Client updated successfully!");
                    } catch (Exception ex){
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(saveButton);
                dialog.add(cancelButton);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Client not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedClient(){
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow >= 0){
            String clientID = (String) clientModel.getValueAt(selectedRow, 0);
            Client client = clientController.getClientByID(clientID);

            if(client != null){
                int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to mark client '" + client.getName() + "' as INACTIVE?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION){
                    boolean success = clientController.deleteClient(clientID);
                    if(success){
                        loadClientData();
                        JOptionPane.showMessageDialog(mainFrame, "Client marked as INACTIVE successfully!");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Cannot delete client. Client has active contracts or invoices",
                            "Delete failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showClientHistory(){
        int selectedRow = clientTable.getSelectedRow();
        if(selectedRow >= 0){
            String clientID = (String) clientModel.getValueAt(selectedRow, 0);
            Client client = clientController.getClientByID(clientID);

            if(client != null){
                List<ClientHistory> clientHistories = clientController.getClientHistory(clientID);

                StringBuilder history = new StringBuilder();
                history.append("=== CLIENT HISTORY: ").append(client.getName()).append(" ===\n\n");
                history.append("Client ID: ").append(clientID).append("\n");
                history.append("Email: ").append(client.getEmail()).append("\n");
                history.append("Phone: ").append(client.getPhone()).append("\n");
                history.append("Status: ").append(client.getStatus()).append("\n\n");

                if(clientHistories.isEmpty()){
                    history.append("No history records found.\n");
                } else {
                    clientHistories.sort((h1, h2) -> Integer.compare(h2.getYear(), h1.getYear()));

                    for (ClientHistory historyRecord : clientHistories){
                        history.append("YEAR ").append(historyRecord.getYear()).append(":\n");
                        history.append("  Contracts: ").append(historyRecord.getContractIds().isEmpty() ? "None": historyRecord.getContractIds()).append("\n");
                        history.append("  Invoices: ").append(historyRecord.getInvoiceIds().isEmpty() ? "None" : historyRecord.getInvoiceIds()).append("\n");
                        history.append("  Payments: ").append(historyRecord.getPaymentIds().isEmpty() ? "None" : historyRecord.getPaymentIds()).append("\n");
                        history.append("  ----------------------------------\n");
                    }
                }
                displayReport("Client History - " + client.getName(), history.toString());
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a client first.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Contract Management
    private JPanel createContractsManagementPanel(){
        JPanel contractPanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton createButton = new JButton("Create Contract");
        JButton renewButton = new JButton("Renew Contract");
        JButton closeButton = new JButton("Close Contract");
        JButton refreshButton = new JButton("Refresh");

        createButton.addActionListener(e -> showCreateContractDialog());
        renewButton.addActionListener(e -> renewSelectedContract());
        closeButton.addActionListener(e -> closeSelectedContract());
        refreshButton.addActionListener(e -> loadContractData());

        toolBar.add(createButton);
        toolBar.add(renewButton);
        toolBar.add(closeButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Contract ID", "Client", "Manager", "Start Date", "End Date", "Status"};
        contractModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        contractTable = new JTable(contractModel);

        loadContractData();
        
        contractPanel.add(toolBar, BorderLayout.NORTH);
        contractPanel.add(new JScrollPane(contractTable), BorderLayout.CENTER);

        return contractPanel;
    }

    private void loadContractData(){
        contractModel.setRowCount(0);
        List<Contract> contracts = contractController.getAllContracts();
        for (Contract contract : contracts){
            Client client = clientController.getClientByID(contract.getClientID());
            AccountManager manager = accountManagerDAO.getManagerByID(contract.getManagerID());

            contractModel.addRow(new Object[]{
                contract.getContractID(),
                client != null ? client.getName() : "N/A",
                manager != null ? manager.getName() : "N/A",
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getContractStatus().toString()
            });
        }
    }

    private void showCreateContractDialog(){
        JDialog dialog = new JDialog(mainFrame, "Create New Contract", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        JComboBox<Client> clientCombo = new JComboBox<>();
        List<Client> activeClients = clientController.getAllActiveClients();
        for(Client client : activeClients){
            clientCombo.addItem(client);
        }

        JComboBox<Service> serviceCombo = new JComboBox<>();
        List<Service> availableServices = serviceController.getAvailableServices();
        for(Service service : availableServices){
            serviceCombo.addItem(service);
        }

        JComboBox<AccountManager> managerCombo = new JComboBox<>();
        List<AccountManager> activeManagers = managerController.getAllActiveManagers();
        for (AccountManager manager : activeManagers){
            managerCombo.addItem(manager);
        }

        JTextField startDateField = new JTextField(LocalDate.now().toString());
        JTextField endDateField = new JTextField(LocalDate.now().plusYears(1).toString());

        formPanel.add(new JLabel("Client:"));
        formPanel.add(clientCombo);
        formPanel.add(new JLabel("Service:"));
        formPanel.add(serviceCombo);
        formPanel.add(new JLabel("Account Manager:"));
        formPanel.add(managerCombo);
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        formPanel.add(startDateField);
        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        formPanel.add(endDateField);

        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton();
        JButton cancelButton = new JButton();

        createButton.addActionListener(e ->{
            try {
                Client selectedClient = (Client) clientCombo.getSelectedItem();
                Service selectedService = (Service) serviceCombo.getSelectedItem();
                AccountManager selectedManager = (AccountManager) managerCombo.getSelectedItem();

                if (selectedClient == null || selectedService == null || selectedManager == null){
                    JOptionPane.showMessageDialog(dialog, "Please select all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = contractController.createContractAndInvoice(selectedClient.getName(), selectedService.getServiceId(), selectedManager.getManagerID());

                if (success){
                    loadContractData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Contract created succesfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create contract. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void renewSelectedContract(){
        int selectedRow = contractTable.getSelectedRow();
        if(selectedRow >= 0){
            String contractID = (String) contractModel.getValueAt(selectedRow, 0);
            Contract contract = contractController.getContractByID(contractID);

            if(contract != null){
                Client client = clientController.getClientByID(contract.getClientID());
                if(client != null){
                    String contractServiceID = showContractServiceSelectionDialog(client.getClientId());

                    if(contractServiceID != null && !contractServiceID.trim().isEmpty()){
                        ContractRenewalResult result = contractServiceController.renewContract(client.getClientId(), contractServiceID);
                
                        if (result != null){
                            loadContractData();
                            String message = String.format(
                                "Contract Renewed Successfully!\n\n" +
                                "Old Contract:\n" +
                                "  Start: %s\n  End: %s\n  Status: %s\n\n" +
                                "New Contract:\n" + 
                                "  Start: %s\n  End: %s\n  Status: %s",
                                result.oldStart, result.oldEnd, result.oldStatus,
                                result.newStart, result.newEnd, result.newStatus
                            );
                            JOptionPane.showMessageDialog(mainFrame, message, "Contract Renewed", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(mainFrame, "Contract renewal failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a contract to renew.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String showContractServiceSelectionDialog(String clientId) {
        // Get inactive contract services for this client
        List<ContractService> inactiveServices = contractServiceDAO.getAllInactiveContractServices();
        
        // Filter by client (you might need to add this method to your DAO)
        List<ContractService> clientInactiveServices = new ArrayList<>();
        for(ContractService cs : inactiveServices){
            Contract contract = contractServiceDAO.getContractByContractServiceId(cs.getContractServiceID());

            if (contract != null && contract.getClientID().equals(clientId)){
                clientInactiveServices.add(cs);
            }
        }
        
        if (clientInactiveServices.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "No inactive contract services found for this client.", 
                "No Services", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        
        // Create selection dialog
        JDialog dialog = new JDialog(mainFrame, "Select Contract Service to Renew", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 300);
        
        // Create table for contract services
        String[] columns = {"Contract Service ID", "Service ID", "Contract ID", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable serviceTable = new JTable(model);
        
        // Populate table
        for (ContractService cs : clientInactiveServices) {
            model.addRow(new Object[]{
                cs.getContractServiceID(),
                cs.getServiceID(),
                cs.getContractID(),
                cs.getStatus().toString()
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(serviceTable);
        
        // Selection panel
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.add(new JLabel("Select a contract service to renew:"), BorderLayout.NORTH);
        selectionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select");
        JButton cancelButton = new JButton("Cancel");
        
        final String[] selectedID = {null};
        
        selectButton.addActionListener(e -> {
            int selectedRow = serviceTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedID[0] = (String) model.getValueAt(selectedRow, 0);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a contract service.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(selectionPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
        
        return selectedID[0];
    }

    private void closeSelectedContract(){
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow >= 0){
            String contractID = (String) contractModel.getValueAt(selectedRow, 0);
            Contract contract = contractController.getContractByID(contractID);

            if(contract != null){
                int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to close contract " + contractID + "?",
                    "Confirm Close Contract",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION){
                    contractController.deleteContract(contractID);
                    loadContractData();
                    JOptionPane.showMessageDialog(mainFrame, "Contract closed successfully!");
                }
            }
        } else{
           JOptionPane.showMessageDialog(mainFrame, "Please select a contract to close.", "Warning", JOptionPane.WARNING_MESSAGE); 
        }
    }

    //Service Management
    private JPanel createServiceManagementPanel(){
        JPanel servicePanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Service");
        JButton editButton = new JButton("Edit Service");
        JButton deleteButton = new JButton("Delete Service");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddServiceDialog());
        editButton.addActionListener(e -> editSelectedService());
        deleteButton.addActionListener(e -> deleteSelectedService());
        refreshButton.addActionListener(e -> loadServiceData());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Service ID", "Name", "Description", "Rate", "Availability"};
        serviceModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        serviceTable = new JTable(serviceModel);

        loadServiceData();

        servicePanel.add(toolBar, BorderLayout.NORTH);
        servicePanel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        return servicePanel;
    }

    private void loadServiceData(){
        serviceModel.setRowCount(0);
        List<Service> services = serviceController.getAllServices();
        for (Service service : services){
            serviceModel.addRow(new Object[]{
                service.getServiceId(),
                service.getName(),
                service.getDescription(),
                "₱" + service.getRate(),
                service.getAvailability()
            });
        }
    }

    private void showAddServiceDialog(){
        JDialog dialog = new JDialog(mainFrame, "Add New Service", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField rateField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Description:"));
        dialog.add(descField);
        dialog.add(new JLabel("Rate:"));
        dialog.add(rateField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try{
                Service service = new Service(
                    nameField.getText(),
                    descField.getText(),
                    new BigDecimal(rateField.getText())
                    );
                    if(serviceController.addService(service)){
                        loadServiceData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(mainFrame, "Service added successfully!");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Service already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    //Branch Management
    private JPanel createBranchManagementPanel(){
        JPanel branchPanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Branch");
        JButton editButton = new JButton("Edit Branch");
        JButton closeButton = new JButton("Close Branch");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddBranchDialog());
        editButton.addActionListener(e -> editSelectedBranch());
        closeButton.addActionListener(e -> closeSelectedBranch());
        refreshButton.addActionListener(e -> loadBranchData());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(closeButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Branch ID", "Name", "Address", "City", "Contact", "Status"};
        branchModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        branchTable = new JTable(branchModel);

        loadBranchData();
        
        branchPanel.add(toolBar, BorderLayout.NORTH);
        branchPanel.add(new JScrollPane(branchTable), BorderLayout.CENTER);

        return branchPanel;
    }

    private void loadBranchData(){
        branchModel.setRowCount(0);
        List<Branch> branches = branchDAO.getAllBranches();
        for (Branch branch : branches){
            branchModel.addRow(new Object[]{
                branch.getBranchID(),
                branch.getName(),
                branch.getAddress(),
                branch.getCity(),
                branch.getContactNumber(),
                branch.getStatus().toString()
            });
        }
    }

    private void showAddBranchDialog(){
        JDialog dialog = new JDialog(mainFrame, "Add New Branch", true);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));
        dialog.setSize(400, 300);

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField contactField = new JTextField();

        dialog.add(new JLabel("Branch Name: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("City:"));
        dialog.add(cityField);
        dialog.add(new JLabel("Contact Number:"));
        dialog.add(contactField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try{    
                Branch branch = new Branch(
                    nameField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    contactField.getText()
                );

                if (branchController.addBranch(branch)){
                    loadBranchData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Branch added successfull!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Branch with this address already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: "+ ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void editSelectedBranch(){
        int selectedRow = branchTable.getSelectedRow();
        if (selectedRow >= 0){
            String branchID = (String) branchModel.getValueAt(selectedRow, 0);
            Branch branch = branchController.getBranchByID(branchID);

            if(branch != null){
                JDialog dialog = new JDialog(mainFrame, "Edit Branch", true);
                dialog.setLayout(new GridLayout(7, 2, 5, 5));
                dialog.setSize(400, 350);

                JTextField nameField = new JTextField(branch.getName());
                JTextField addressField = new JTextField(branch.getAddress());
                JTextField cityField = new JTextField(branch.getCity());
                JTextField contactField = new JTextField(branch.getContactNumber());
                JComboBox<BranchStatus> statusCombo = new JComboBox<>(BranchStatus.values());
                statusCombo.setSelectedItem(branch.getStatus());

                dialog.add(new JLabel("Branch ID:"));
                dialog.add(new JLabel(branch.getBranchID()));
                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Address:"));
                dialog.add(addressField);
                dialog.add(new JLabel("City:"));
                dialog.add(cityField);
                dialog.add(new JLabel("Contact:"));
                dialog.add(contactField);
                dialog.add(new JLabel("Status:"));
                dialog.add(statusCombo);

                JButton saveButton = new JButton("Save Changes");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(e -> {
                    try {
                        branch.setName(nameField.getText());
                        branch.setAddress(addressField.getText());
                        branch.setRegion(cityField.getText());
                        branch.setPhone(contactField.getText());
                        branch.setStatus((BranchStatus) statusCombo.getSelectedItem());

                        branchController.updateExistingBranch(branch);
                        loadBranchData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(mainFrame, "Branch Updated successfully!");
                    } catch(Exception ex){
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(saveButton);
                dialog.add(cancelButton);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a branch to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void closeSelectedBranch(){
        int selectedRow = branchTable.getSelectedRow();
        if(selectedRow >= 0){
            String branchID = (String) branchModel.getValueAt(selectedRow, 0);
            Branch branch = branchController.getBranchByID(branchID);

            if (branch != null){
                int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to close branch '" + branch.getName() + "'?",
                    "Confirm Close Branch",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if(confirm == JOptionPane.YES_OPTION){
                    boolean success = branchController.closeBranch(branchID);
                    if (success){
                        loadBranchData();
                        JOptionPane.showMessageDialog(mainFrame, "Branch closed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                        "Cannot close branch. Branch still has active contracts.",
                        "Close Failed",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a branch to close.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    //Manager Management
    private JPanel createManagerManagementPanel(){
        JPanel managerPanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Manager");
        JButton editButton = new JButton("Edit Manager");
        JButton removeButton = new JButton("Remove Manager");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddManagerDialog());
        editButton.addActionListener(e -> editSelectedManager());
        removeButton.addActionListener(e -> removeSelectedManager());
        refreshButton.addActionListener(e -> loadManagerData());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(removeButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Manager ID", "Name", "Contact", "Branch", "Status"};
        managerModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        managerTable = new JTable(managerModel);

        loadManagerData();

        managerPanel.add(toolBar, BorderLayout.NORTH);
        managerPanel.add(new JScrollPane(managerTable), BorderLayout.CENTER);

        return managerPanel;
    }

    private void loadManagerData(){
        managerModel.setRowCount(0);
        List<AccountManager> managers = accountManagerDAO.getAllManagers();
        for(AccountManager manager : managers){
            Branch branch = branchDAO.getBranchByID(manager.getBranchID());
            managerModel.addRow(new Object[]{
                manager.getManagerID(),
                manager.getName(),
                manager.getContactInfo(),
                branch != null ? branch.getName() : "N/A",
                manager.getStatus().toString()
            });
        }
    }

    private void showAddManagerDialog(){
        JDialog dialog = new JDialog(mainFrame, "Add New Manager", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));
        dialog.setSize(400,250);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<Branch> branchCombo = new JComboBox<>();

        List<Branch> operationalBranches = branchController.getAllOperationalBranches();
        for (Branch branch : operationalBranches){
            branchCombo.addItem(branch);
        }

        dialog.add(new JLabel("Name: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Branch:"));
        dialog.add(branchCombo);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                Branch selectedBranch = (Branch) branchCombo.getSelectedItem();
                if(selectedBranch == null){
                    JOptionPane.showMessageDialog(dialog, "Please select a branch.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                AccountManager manager = new AccountManager(nameField.getText(), emailField.getText(), selectedBranch.getBranchID());
                if (managerController.addManager(manager)){
                    loadManagerData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Manager added succesfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Manager with this name already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void editSelectedManager(){
        int selectedRow = managerTable.getSelectedRow();
        if (selectedRow >= 0){
            String managerID = (String) managerModel.getValueAt(selectedRow, 0);
            AccountManager manager = managerController.getManagerByID(managerID);

            if(manager != null){
                JDialog dialog = new JDialog(mainFrame, "Edit Manager", true);
                dialog.setLayout(new GridLayout(6, 2, 5, 5));
                dialog.setSize(400, 300);

                JTextField nameField = new JTextField(manager.getName());
                JTextField emailField = new JTextField(manager.getContactInfo());
                JComboBox<ManagerStatus> statusCombo = new JComboBox<>(ManagerStatus.values());
                statusCombo.setSelectedItem(manager.getStatus());

                dialog.add(new JLabel("Manager ID:"));
                dialog.add(new JLabel(manager.getManagerID()));
                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Email:"));
                dialog.add(emailField);
                dialog.add(new JLabel("Status:"));
                dialog.add(statusCombo);

                JButton saveButton = new JButton("Save Changes");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(e -> {
                    try{
                        manager.setName(nameField.getText());
                        manager.setContactInfo(emailField.getText());
                        manager.setStatus((ManagerStatus) statusCombo.getSelectedItem());

                        managerController.updateExistingManager(manager);
                        loadManagerData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(mainFrame, "Manager updated successfully!");
                    } catch (Exception ex){
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(saveButton);
                dialog.add(cancelButton);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a manager to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void removeSelectedManager(){
        int selectedRow = managerTable.getSelectedRow();
        if(selectedRow >= 0){
            String managerID = (String) managerModel.getValueAt(selectedRow, 0);
            AccountManager manager = managerController.getManagerByID(managerID);

            if(manager != null){
                int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to mark manager '" + manager.getName() + "' as RESIGNED?",
                    "Confirm Remove Manager",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION){
                    boolean success = managerController.removeManager(managerID);
                    if(success){
                        loadManagerData();
                        JOptionPane.showMessageDialog(mainFrame, "Manager marked as resigned successfully!");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                        "Cannot remove manager. Manager still has active contracts.",
                        "Remove Failed",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a manager to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
    // Invoice and payment
    private JPanel createInvoicePaymentPanel(){
        JPanel invoicePanel = new JPanel(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        JButton processPaymentButton = new JButton("Process Payment");
        JButton viewInvoicesButton = new JButton("View Invoice Details");
        JButton refreshButton = new JButton("Refresh");

        processPaymentButton.addActionListener(e -> showProcessPaymentDialog());
        viewInvoicesButton.addActionListener(e -> viewInvoiceDetails());
        refreshButton.addActionListener(e -> loadInvoiceData());

        toolBar.add(processPaymentButton);
        toolBar.add(viewInvoicesButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);

        String[] columns = {"Invoice ID", "Client", "Amount", "Due Date", "Status", "Late Fee"};
        invoiceModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        invoiceTable = new JTable(invoiceModel);

        loadInvoiceData();

        invoicePanel.add(toolBar, BorderLayout.NORTH);
        invoicePanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        return invoicePanel;
    }

    private void loadInvoiceData(){
        invoiceModel.setRowCount(0);
        List<Client> clients = clientController.getAllClients();
        for(Client client : clients){
            List<Invoice> invoices = invoiceDAO.getInvoicesByClientID(client.getClientId());
            for(Invoice invoice : invoices){
                invoiceModel.addRow(new Object[]{
                    invoice.getInvoiceId(),
                    client.getName(),
                    "₱" + invoice.getAmount(),
                    invoice.getDueDate(),
                    invoice.getStatus().toString(),
                    "₱" + invoice.getLateFee()
                });
            }
        }
    }

    private JPanel createReportsPanel(){
        JPanel reportsPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton financialReportBtn = new JButton("Financial Report");
        JButton clientReportsBtn = new JButton("Client Analytics");
        JButton serviceReportsBtn = new JButton("Service Performance");
        JButton contractReportsBtn = new JButton("Contract Overview");
        JButton paymentReportsBtn = new JButton("Payment History");
        
        financialReportBtn.addActionListener(e -> showFinancialReport());
        clientReportsBtn.addActionListener(e-> showClientAnalytics());
        serviceReportsBtn.addActionListener(e -> showServicePerformance());
        contractReportsBtn.addActionListener(e -> showContractOverview());
        paymentReportsBtn.addActionListener(e -> showPaymentHistory());

        buttonPanel.add(financialReportBtn);
        buttonPanel.add(clientReportsBtn);
        buttonPanel.add(serviceReportsBtn);
        buttonPanel.add(contractReportsBtn);
        buttonPanel.add(paymentReportsBtn);

        reportsPanel.add(buttonPanel, BorderLayout.WEST);
        reportsPanel.add(new JLabel("Select a report to generate...", SwingConstants.CENTER), BorderLayout.CENTER);

        return reportsPanel;
    }

    private void showContractOverview(){
        StringBuilder report = new StringBuilder();
        report.append("=== CONTRACT OVERVIEW REPORT ==\n\n");

        List<Contract> allContracts = contractController.getAllContracts();
        List<Client> allClients = clientController.getAllClients();

        int totalContracts = allContracts.size();
        int activeContracts = contractDAO.getActiveContractsCount();
        int closedContracts = totalContracts - activeContracts;
        int expiringSoon = contractDAO.getExpiringContractCount(30);

        report.append("CONTRACT STATISTICS:\n");
        report.append("Total Contracts: ").append(totalContracts).append("\n");
        report.append("Active Contracts: ").append(activeContracts).append("\n");
        report.append("Closed Contracts: ").append(closedContracts).append("\n");
        report.append("Expiring in 30 days: ").append(expiringSoon).append("\n");

        report.append("CONTRACTS BY STATUS:\n");
        Map<ContractStatus, Integer> contractByStatus = new HashMap<>();
        for (Contract contract: allContracts){
            contractByStatus.merge(contract.getContractStatus(), 1, Integer::sum);
        }

        for(Map.Entry<ContractStatus, Integer> entry : contractByStatus.entrySet()){
            report.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\n");

        report.append("CONTRACTS BY CLIENT:\n");
        for (Client client : allClients){
            List<Contract> clientContracts = contractDAO.getContractsByClientId(client.getClientId());
            if(!clientContracts.isEmpty()){
                report.append("  ").append(client.getName()).append(": ").append(clientContracts.size()).append(" contracts\n");
            }
        }
        report.append("\n");

        if (expiringSoon > 0){
            report.append("CONCTRACTS EXPIRING SOON:\n");
            for(Contract contract : allContracts){
                if(contract.getContractStatus() == ContractStatus.ACTIVE && contract.getEndDate().isBefore(LocalDate.now().plusDays(30))){
                    Client client = clientController.getClientByID(contract.getClientID());
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate());
                    report.append("  ").append(contract.getContractID()).
                           append(" - ").append(client != null ? client.getName() : "Uknown Client").
                           append(" (Expires in ").append(daysLeft).append(" days)\n");
                }
            }
        }

        displayReport("Contract Overview", report.toString());
    }

    private void showPaymentHistory(){
        StringBuilder report = new StringBuilder();
        report.append("=== PAYMENT HISTORY REPORT ===\n\n");

        List<Payment> allPayments = paymentDAO.getAllPayments();

        if(allPayments.isEmpty()){
            report.append("No payment records found.\n");
        } else {
            allPayments.sort((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()));

            BigDecimal totalRevenue = allPayments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            report.append("PAYMENT STATICS:\n");
            report.append("Total Payments: ").append(allPayments.size()).append("\n");
            report.append("Total Revenue: ₱").append(totalRevenue).append("\n\n");

            report.append("RECENT PAYMENTS:\n");
            int count = Math.min(allPayments.size(), 30);
            for(int i = 0; i < count; i++){
                Payment payment = allPayments.get(i);
                Invoice invoice = invoiceDAO.getInvoiceById(payment.getInvoiceId());
                Client client = invoice != null ? clientController.getClientByInvoice(invoice) : null;

                report.append("  ").append(payment.getPaymentDate())
                      .append(" - ").append(payment.getInvoiceId())
                      .append(" - ").append(client != null ? client.getName() : "Unknown Client")
                      .append(" - ₱").append(payment.getAmount())
                      .append(" - ").append(payment.getReceiptNumber()).append("\n");
            }

            report.append("\nMONTHLY BREAKDOWN:\n");
            Map<String, BigDecimal> monthlyRevenue = new TreeMap<>();
            for(Payment payment : allPayments){
                String monthYear = payment.getPaymentDate().getMonth().toString() + " " + payment.getPaymentDate().getYear();
                monthlyRevenue.merge(monthYear, payment.getAmount(), BigDecimal::add);
            }

            for (Map.Entry<String, BigDecimal> entry : monthlyRevenue.entrySet()){
                report.append("  ").append(entry.getKey()).append(": ₱").append(entry.getValue()).append("\n");
            }
        }
        displayReport("Payment History", report.toString());
    }

    private void showProcessPaymentDialog(){
        JDialog dialog = new JDialog(mainFrame, "Process Payment", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));
        dialog.setSize(400, 250);

        JTextField clientIDField = new JTextField();
        JTextField invoiceIDField = new JTextField();
        JTextField amountField = new JTextField();

        dialog.add(new JLabel("Client ID: "));
        dialog.add(clientIDField);
        dialog.add(new JLabel("Invoice ID: "));
        dialog.add(invoiceIDField);
        dialog.add(new JLabel("Amount: "));
        dialog.add(amountField);

        JButton processButton = new JButton("Process Payment");
        JButton cancelButton = new JButton("Cancel");

        processButton.addActionListener(e -> {
            try{
                String clientID = clientIDField.getText();
                String invoiceID = invoiceIDField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());

                boolean success = paymentController.processPayment(clientID, invoiceID, amount);

                if(success){
                    loadInvoiceData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(mainFrame, "Payment processed successfully!");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Payment failed. Please check client and/or invoice information", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex){
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(processButton);
        dialog.add(cancelButton);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void viewInvoiceDetails(){
        int selectedRow = invoiceTable.getSelectedRow();
        if(selectedRow >= 0){
            String invoiceID = (String) invoiceModel.getValueAt(selectedRow, 0);
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceID);

            if (invoice != null){
                Client client = clientController.getClientByInvoice(invoice);
                
                StringBuilder details = new StringBuilder();
                details.append("=== INVOICE DETAILS ===\n\n");
                details.append("Invoice ID: ").append(invoice.getInvoiceId()).append("\n");
                details.append("Client: ").append(client != null ? client.getName() : "Unknown").append("\n");
                details.append("Contract ID: ").append(invoice.getContractId()).append("\n");
                details.append("Invoice Date: ").append(invoice.getInvoiceDate()).append("\n");
                details.append("Due Date: ").append(invoice.getDueDate()).append("\n");
                details.append("Amount: ₱").append(invoice.getAmount()).append("\n");
                details.append("Late Fee: ₱").append(invoice.getLateFee()).append("\n");
                details.append("Status: ").append(invoice.getStatus()).append("\n");

                if(invoice.getStatus() == InvoiceStatus.OVERDUE){
                    long daysOverdue = ChronoUnit.DAYS.between(invoice.getDueDate(), LocalDate.now());
                    details.append("Days Overdue: ").append(daysOverdue).append("\n");
                }
                displayReport("Invoice Details - " + invoiceID, details.toString());
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select an invoice to view details.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editSelectedService(){
        int selectedRow = serviceTable.getSelectedRow();
        if(selectedRow >= 0){
            String serviceID = (String) serviceModel.getValueAt(selectedRow, 0);
            Service service = serviceController.getServiceById(serviceID);

            if (service != null){
                JDialog dialog = new JDialog(mainFrame, "Edit Service", true);
                dialog.setLayout(new GridLayout(6, 2, 5, 5));
                dialog.setSize(400, 300);

                JTextField nameField = new JTextField(service.getName());
                JTextField descField = new JTextField(service.getDescription());
                JTextField rateField = new JTextField(service.getRate().toString());
                JComboBox<String> availabilityCombo = new JComboBox<>(new String[]{"Available", "Unavailable", "Discontinued"});
                availabilityCombo.setSelectedItem(service.getAvailability());

                dialog.add(new JLabel("Service ID:"));
                dialog.add(new JLabel(service.getServiceId()));
                dialog.add(new JLabel("Name:"));
                dialog.add(nameField);
                dialog.add(new JLabel("Description:"));
                dialog.add(descField);
                dialog.add(new JLabel("Rate:"));
                dialog.add(rateField);
                dialog.add(new JLabel("Availability:"));
                dialog.add(availabilityCombo);

                JButton saveButton = new JButton("Save Changes");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(e -> {
                    try {
                        service.setName(nameField.getText());
                        service.setDescription(descField.getText());
                        service.setRate(new BigDecimal(rateField.getText()));
                        service.setAvailability((String) availabilityCombo.getSelectedItem());

                        serviceController.updateService(service);
                        loadServiceData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(mainFrame, "Service updated successfully!");
                    } catch(Exception ex){
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(saveButton);
                dialog.add(cancelButton);
                dialog.setLocationRelativeTo(mainFrame);
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a service to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedService(){
        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow >= 0){
            String serviceID = (String) serviceModel.getValueAt(selectedRow, 0);
            Service service = serviceController.getServiceById(serviceID);

            if (service != null){
                int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to mark service '" + service.getName() + "' as UNAVAILABLE?",
                    "Confirm Delete Service",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION){
                    boolean success = serviceController.deleteService(serviceID);
                    if (success){
                        loadServiceData();
                        JOptionPane.showMessageDialog(mainFrame, "Service marked as unavailable successfully!");
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                        "Cannot delete service. Service is still used in active contracts.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please select a service to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e){
                e.printStackTrace();
            }
            new AdminGUI();
        });
    }
}
