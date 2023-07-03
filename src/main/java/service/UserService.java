package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

public class UserService {
    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    public UserService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }


    public boolean createUser(String firstname, String lastname, Date dob, String occupation, String sin, String postalcode, String city, String country, String creditcard ) {
        try {
            String sql = "INSERT INTO user (firstname, lastname, dob, occupation, sin, postalcode, city, country, creditcard) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstname);
            stmt.setString(2, lastname);
            stmt.setDate(3, dob);
            stmt.setString(4, occupation);
            stmt.setString(5, sin);
            stmt.setString(6, postalcode);
            stmt.setString(7, city);
            stmt.setString(8, country);
            stmt.setString(9, creditcard);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("[User Creation Failed] " + e.getMessage());
            return false;
        }

        System.out.println("Successfully created a user!");
        return true;
        
    }


    public boolean deleteUser(int userId) {
        try {
            String sql = "DELETE FROM user WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("[User Deletion Failed] " + e.getMessage());
            return false;
        }
        System.out.println("Successfully deleted a user!");
        return true;
    }

    public void updateCreditcard(int userId, String creditcard) {
        try {
            String sql = "UPDATE user SET creditcard = ? WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, creditcard);
            stmt.setInt(2, userId);
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("[Creditcard Update Failed] " + e.getMessage());
            return;
        }
        System.out.println("Successfully updated creditcard!");

    }

    public void isRenter(int userId) {
        // check if user has creditcard field not null, return 
        try {
            String sql = "SELECT creditcard FROM user WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()){
                if (rs.getString("creditcard") == null) {
                    System.out.println("User is not a renter");
                    return;
                }
            }
            System.out.println("User is a renter");

        } catch (SQLException e) {
            System.out.println("[isRenter Query Failed] " + e.getMessage());
            return;
        }        
    }

}
