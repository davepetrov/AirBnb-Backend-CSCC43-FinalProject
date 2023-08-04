package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.constant.ListingType;
import model.constant.UserType;
import model.entity.Booking;
import model.entity.Listing;

public class ListingService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public ListingService() throws ClassNotFoundException, SQLException {

		// Class.forName(System.getenv("CLASSNAME"));
        Class.forName("com.mysql.cj.jdbc.Driver");

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
    
    
    public void createListing(int hostUserId, ListingType type, float locationLat, float locationLong, String postalCode, String city, String country){
        String sql = "INSERT INTO Listing (host_userId, listingType, locationLat, locationLong, postalCode, city, country) VALUES (?, ?, ?, ?, ?, ?, ?);";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, hostUserId);
            ps.setString(2, type.toString());
            ps.setFloat(3, locationLat);
            ps.setFloat(4, locationLong);
            ps.setString(5, postalCode);
            ps.setString(6, city);
            ps.setString(7, country);
            int rowsAffected = ps.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("\nListing Created successfully!");
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("a foreign key constraint fails")) {
                System.out.println("\n[Listing Creation Failed] HostId doesnt exist\n");
            } else {
                // Handle other SQLExceptions
                System.out.println("\n[Listing Creation Failed]An SQL exception occurred: " + e.getMessage() + "\n");
            }            
        }
    }


    public void deleteListing(int listingId){
        try {
            String sql = "DELETE FROM Listing WHERE listingId = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingId);
            int rowsAffected = ps.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("\nListing deleted successfully!");
            } else {
                System.out.println("\nNo listing was deleted. Please check the listingId!");
            }
    
        } catch (SQLException e) {
            System.out.println("\n[Listing Delete Failed] " + e.getMessage());
        }   
    }
}
