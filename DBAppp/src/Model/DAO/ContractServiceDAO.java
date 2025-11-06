package Model.DAO;

import Model.Entities.Client;
import Model.Entities.ContractService;
import Model.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContractServiceDAO {
    // CREATE
    public void addContractService(ContractService contractService){
        String sql = "INSERT INTO ContractService (contractId, serviceId) VALUES (?,?)";
        try(Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, contractService.getContractID());
            stmt.setString(2, contractService.getServiceID());
            stmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


}
