package Controller;
import Model.DAO.ContractServiceDao;
import Model.Entities.Contract;
import Model.Entities.ContractService;

public class ContractServiceController {
    private final ContractServiceDao contractServiceDao = new ContractServiceDao();

    public void addContractService(ContractService contract) {
        contractServiceDao.addContractService(contract);
    }

    public void setContractServiceInvalid(String contractId) {
        contractServiceDao.deactivateContractServices(contractId);
    }

    public void setContractServiceValid(String contractId) {
        contractServiceDao.reactivateContractServices(contractId);
    }
}
