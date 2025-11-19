package Controller;

import Model.Entities.*;
import Model.DAO.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ContractController {
    private final ContractDAO contractDAO;
    private final ClientDAO clientDAO;
    private final ServiceDAO serviceDAO;
    private final AccountManagerDAO  accountManagerDAO;
    private final ContractServiceDao contractServiceDAO;
    private final InvoiceDAO invoiceDAO;


    public ContractController(ContractDAO contractDAO,
                              ClientDAO clientDAO, ServiceDAO serviceDAO, ContractServiceDao contractServiceDAO,
                              InvoiceDAO invoiceDAO, AccountManagerDAO accountManagerDAO) {
        this.contractDAO = contractDAO;
        this.clientDAO = clientDAO;
        this.serviceDAO = serviceDAO;
        this.contractServiceDAO = contractServiceDAO;
        this.invoiceDAO = invoiceDAO;
        this.accountManagerDAO = accountManagerDAO;
    }
    // Add a new contract
    public void addContract(Contract contract){
        contractDAO.addContract(contract);
    }

    // Retrieve a contract by ID
    public Contract getContractByID(String id) {
        return contractDAO.getContractByID(id);
    }

    // Soft Delete a contract by ID
    public void deleteContract(String id){
        contractDAO.closeContract(id);
    }

    // View related records
    public void viewRelatedRecords(String id){
        contractDAO.viewRelatedRecords(id);
    }

    public List<Contract> getAllContracts(){
        List<Contract> allContracts = new ArrayList<>();
        ClientController clientController = new ClientController(contractDAO, invoiceDAO, clientDAO);
        
        List<Client> clients = clientController.getAllClients();
        for(Client client : clients){
            allContracts.addAll(contractDAO.getContractsByClientId(client.getClientId()));
        }
        return allContracts;

    }
    // Prints all Closed Contracts
    public void showAllClosedContracts(String clientid){
        List<Contract> contracts = contractDAO.getContractsByClientId(clientid);
        System.out.println("List of closed contracts: ");
        for (Contract contract : contracts){
            if(contract.getContractStatus() == ContractStatus.CLOSED){
                System.out.println(contract.toString());
            }
        }
    }
    public boolean createContractAndInvoice(String clientName, String serviceId, String managerId) {
        Client client = clientDAO.getClientByName(clientName);
        if (client == null) {
            return false;
        }

        Service service = serviceDAO.getServiceById(serviceId);
        if (service == null) {
            return false;
        }

        AccountManager manager = accountManagerDAO.getManagerByID(managerId);
        if (manager == null) {
            return false;
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        LocalDate invoiceDue = startDate.plusDays(30);

        Contract contract = new Contract(
                client.getClientId(),
                managerId,
                startDate,
                endDate
        );

        ContractService contractService = new ContractService(serviceId, contract.getContractID());
        Invoice invoice = new Invoice(contract.getContractID(), startDate, invoiceDue, service.getRate());

        contractDAO.addContract(contract);
        contractServiceDAO.addContractService(contractService);
        invoiceDAO.addInvoice(invoice);

        return true;
    }

    public boolean createContractAndInvoice(String clientName, List<String> serviceIds, String managerId) {
        Client client = clientDAO.getClientByName(clientName);
        if (client == null) {
            return false;
        }

        List<Service> selectedServices = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (String serviceId : serviceIds){
            Service service = serviceDAO.getServiceById(serviceId);
            if (service == null) {
                return false;
            }
            if (!"Available".equals(service.getAvailability())){
                return false;
            }

            selectedServices.add(service);
            totalAmount = totalAmount.add(service.getRate());
        }

        AccountManager manager = accountManagerDAO.getManagerByID(managerId);
        if (manager == null) {
            return false;
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        LocalDate invoiceDue = startDate.plusDays(30);

        Contract contract = new Contract(
                client.getClientId(),
                managerId,
                startDate,
                endDate
        );

        try{
            contractDAO.addContract(contract);
            for(String serviceId : serviceIds){
                ContractService contractService = new ContractService(serviceId, contract.getContractID());
                contractServiceDAO.addContractService(contractService);
            }
            Invoice invoice = new Invoice(contract.getContractID(), startDate, invoiceDue, totalAmount);
            invoiceDAO.addInvoice(invoice);

            return true;
        } catch (Exception e){
            return false;
        }
    }

}

