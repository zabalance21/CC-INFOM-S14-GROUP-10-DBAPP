package Controller;

import Model.DAO.ServiceDAO;
import Model.Entities.Service;
import java.util.List;

public class ServiceController {
    private final ServiceDAO dao = new ServiceDAO();
    public void addService(Service s) { dao.addService(s); }
    public Service getServiceById(String id) { return dao.getServiceById(id); }
    public List<Service> getAllServices() { return dao.getAllServices(); }
    public void updateService(Service s) { dao.updateService(s); }
    public void deleteService(String id) { dao.deleteService(id); }
    public void viewRelatedRecords(String serviceId) {
        dao.printRelatedRecords(serviceId);}

    public void printAvailableServices(){
        List<Service> services = dao.getAllServices();
        System.out.println("List of available services: ");
        for(Service s: services){
            if(s.getAvailability().equals("Available")){
                System.out.println(String.format("Service ID: %s | Service Name: %s\n", s.getServiceId(), s.getName()));
            }
        }
    }
}