package Controller;
import Model.DAO.*;
import Model.Entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JOptionPane;

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

    public ContractRenewalResult renewContract(String clientId, String contractId) {
        Client client = clientDAO.getClientByID(clientId);
        if (client == null) {
            JOptionPane.showMessageDialog(null, "Client not found.");
            return null;
        }

        Contract contract = contractDAO.getContractByID(contractId);
        if (contract == null) {
            JOptionPane.showMessageDialog(null, "Contract not found: " + contractId);
            return null;
        }

        if (!contract.getClientID().equals(clientId)) {
            JOptionPane.showMessageDialog(null, "Contract does not belong to this client.");
            return null;
        }
        
        if (contract.getContractStatus() != ContractStatus.CLOSED) {
            JOptionPane.showMessageDialog(null, "Only closed contracts can be renewed.");
            return null;
        }

        LocalDate oldStart = contract.getStartDate();
        LocalDate oldEnd = contract.getEndDate();
        ContractStatus oldStatus = contract.getContractStatus();

        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusYears(1));
        contract.setContractStatus(ContractStatus.ACTIVE);
        contractDAO.updateContractDetails(contract);
        
        contractServiceDao.reactivateContractServices(contractId);
        
        List<ContractService> contractServices = contractServiceDao.getContractServicesByContractId(contractId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (ContractService cs : contractServices) {
            Service service = serviceDAO.getServiceById(cs.getServiceID());
            if (service != null) {
                totalAmount = totalAmount.add(service.getRate());
            }
        }
        
        Invoice invoice = new Invoice(contract.getContractID(), LocalDate.now(), LocalDate.now().plusYears(1), totalAmount);
        invoiceDAO.addInvoice(invoice);

        return new ContractRenewalResult(oldStart, oldEnd, oldStatus, contract.getStartDate(), contract.getEndDate(), contract.getContractStatus());
    }
}
