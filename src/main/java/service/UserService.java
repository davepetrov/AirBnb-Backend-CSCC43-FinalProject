package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

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
            System.out.println("[User Creation Failed] " + e.getMessage());
            return false;
        }
        System.out.println("Successfully created a user!\n");
        return true;
        
    }


    public boolean deleteUser(int userId) {
        try {
            String sql = "DELETE FROM BNBUser WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[User Deletion Failed] " + e.getMessage());
            return false;
        }
        System.out.println("Successfully deleted a user!\n");
        return true;
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
            String sql = "SELECT creditcard FROM BNBUser WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                if (rs.getString("creditcard")==null || rs.getString("creditcard").isEmpty()) {
                    System.out.println("User is not a renter\n");
                    return;
                }
            }
            System.out.println("User is a renter\n");

        } catch (SQLException e) {
            System.out.println("[isRenter Query Failed] " + e.getMessage());
            return;
        }        
    }

}
