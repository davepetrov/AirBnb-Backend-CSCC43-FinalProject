package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public UserService() throws ClassNotFoundException, SQLException {

        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean createUser(String firstname, String surname, Date dob, String occupation, String sin, String postalCode, String city, String country, String creditcard ) {
        try {
            String sql = "INSERT INTO BNBUser (firstname, surname, dob, occupation, sin, postalCode, city, country, creditcard) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstname);
            stmt.setString(2, surname);
            stmt.setDate(3, dob);
            stmt.setString(4, occupation);
            stmt.setString(5, sin);
            stmt.setString(6, postalCode);
            stmt.setString(7, city);
            stmt.setString(8, country);
            stmt.setString(9, creditcard);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("\n[User Creation Failed] " + e.getMessage());
            return false;
        }
        System.out.println("\nSuccessfully created a user!");
        return true;
        
    }


    public void deleteUser(int userId) {
        try {
            String sql = "DELETE FROM BNBUser WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("\nUser does not exist.");
            }
            else{
                System.out.println("\nSuccessfully deleted user!");
            }
        } catch (SQLException e) {
            System.out.println("[User Deletion Failed] " + e.getMessage());
        }
    }

    public void updateCreditcard(int userId, String creditcard) {
        try {
            String sql = "UPDATE BNBUser SET creditcard = ? WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, creditcard);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[Creditcard Update Failed] " + e.getMessage());
            return;
        }
        System.out.println("Successfully updated creditcard!\n");

    }

    public void isRenter(int userId) {
        // check if user has creditcard field not null, return 
        try {
            String sql = "SELECT userId, creditcard FROM BNBUser WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                if (rs.getString("creditcard")==null || rs.getString("creditcard").isEmpty()) {
                    System.out.println("\nUser is NOT eligible to be a renter (Their Creditcard # is not setup)");
                    return;
                }
                else{
                    System.out.println("\nUser is eligible to be a renter (Their Creditcard # is setup)");
                }
            }
            else{
                System.out.println("\nUser does not exist.");
            }

        } catch (SQLException e) {
            System.out.println("[isRenter Query Failed] " + e.getMessage());
        }        
    }

}
