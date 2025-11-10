package Controller;
import Model.DAO.*;
import Model.Entities.*;
import View.InputHelper;

import java.time.LocalDate;
import java.util.List;

public class ContractServiceController {
    private final ContractServiceDao contractServiceDao;
    private final ClientDAO clientDAO;
    private final ServiceDAO serviceDAO;
    private final ContractDAO  contractDAO;
    private final InvoiceDAO invoiceDAO;

    public ContractServiceController(ContractServiceDao contractServiceDao, ClientDAO clientDAO, ServiceDAO serviceDAO,
                                     ContractDAO contractDAO, InvoiceDAO invoiceDAO) {
        this.contractServiceDao = contractServiceDao;
        this.clientDAO = clientDAO;
        this.contractDAO = contractDAO;
        this.serviceDAO = serviceDAO;
        this.invoiceDAO = invoiceDAO;
    }

    private void showAllInactive(){
        List<ContractService> inactiveCS = contractServiceDao.getAllInactiveContractServices();
        for(ContractService cs: inactiveCS){
            System.out.println(cs.toString());
        }
    }

    public ContractRenewalResult renewContract(String clientId) {
        Client client = clientDAO.getClientByID(clientId);
        if (client == null) {
            System.out.println("Client not found.");
            return null;
        }

        // Show closed contracts for that client
        showAllInactive();

        String contractId = InputHelper.getStringInput("Enter the Contract Service ID of the contract you would like to renew: ");
        ContractService cs = contractServiceDao.getContractServiceById(contractId);
        Contract contract = contractServiceDao.getContractByContractServiceId(contractId);
        Service service = serviceDAO.getServiceById(cs.getServiceID());


        if (contract == null) {
            System.out.println("Contract not found for this client.");
            return null;
        }

        // Store old details
        LocalDate oldStart = contract.getStartDate();
        LocalDate oldEnd = contract.getEndDate();
        ContractStatus oldStatus = ContractStatus.CLOSED;

        // Update the contract
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setContractStatus(ContractStatus.ACTIVE);
        contractDAO.updateContractDetails(contract);
        Invoice invoice = new Invoice(contract.getContractID(), client.getClientId(), LocalDate.now(), LocalDate.now().plusYears(1), service.getRate());

        // Update linked services
        contractServiceDao.reactivateContractServices(contractId);
        invoiceDAO.addInvoice(invoice);

        // Return a result object containing both old & new data for display
        return new ContractRenewalResult(oldStart, oldEnd, oldStatus, contract.getStartDate(), contract.getEndDate(), contract.getContractStatus());
    }
}
