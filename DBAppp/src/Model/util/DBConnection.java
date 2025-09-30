package Model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL =  "jdbc:mysql://localhost:3306/ITServicesDB";
    private static final String USER = "root"; // your mySQL username
    private static final String PASSWORD = "<insert your MYSQL password here>"; // your mySQL password

    // Get a connected
    public static Connection getConnection() throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("MYSQL JDBC Driver not found");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }

    // Close connection safely
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
