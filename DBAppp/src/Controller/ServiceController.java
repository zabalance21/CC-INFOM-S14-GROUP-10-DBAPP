package Controller;

import Model.DAO.ContractDAO;
import Model.DAO.ContractServiceDao;
import Model.DAO.ServiceDAO;
import Model.Entities.Contract;
import Model.Entities.Invoice;
import Model.Entities.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceController {
    private ContractDAO contractDAO;
    private ServiceDAO serviceDAO;
    private ContractServiceDao contractServiceDao;
    public ServiceController(ContractDAO contractDAO, ServiceDAO serviceDAO, ContractServiceDao contractServiceDao) {
        this.contractDAO = contractDAO;
        this.serviceDAO = serviceDAO;
        this.contractServiceDao = contractServiceDao;
    }
    public boolean addService(Service s) {
        if(serviceDAO.checkServiceExists(s.getName())){
            return false;
        }
        serviceDAO.addService(s);
        return true;
    }
    public Service getServiceById(String id) { return serviceDAO.getServiceById(id); }
    public List<Service> getAllServices() { return serviceDAO.getAllServices(); }
    public void updateService(Service s) { serviceDAO.updateService(s); }
    public boolean deleteService(String id) {
        if(contractDAO.hasActiveContractsUsingService(id)){
            return false;
        }
        serviceDAO.deleteService(id);
        return true;
    }

    public List<Service> getAvailableServices(){
        return serviceDAO.getAvailableServicesOnly();
    }

    public Service getServiceByName(String name){
        return serviceDAO.getServiceByName(name);
    }

    public Map<String, List<?>> getRelatedRecords(String serviceId) {
        Map<String, List<?>> records = new HashMap<>();
        List<Contract> contracts = contractServiceDao.getContractsByServiceID(serviceId);
        List<Invoice> invoices = contractServiceDao.getInvoicesByServiceID(serviceId);

        records.put("contracts", contracts);
        records.put("invoices", invoices);
        return records;
    }
}