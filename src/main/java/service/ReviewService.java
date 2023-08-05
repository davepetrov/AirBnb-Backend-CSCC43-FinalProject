package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public ReviewService() throws ClassNotFoundException, SQLException {

        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public void renterReviewListing(int userId, int listingId, int rating, String comment) {
        try {
            // Check if renter has rented the specified listing in the past week
            String sql = "INSERT INTO Renter_Review_Listing (renterUserId, listingId, comment, rating) " +
                         "SELECT ?, ?, ?, ? " +
                         "FROM Booking " +
                         "WHERE renter_userId = ? AND listingId = ? AND " +
                         "created_at >= DATE_SUB(NOW(), INTERVAL 1 WEEK) " +
                         "LIMIT 1";
    
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.setInt(5, userId);
            stmt.setInt(6, listingId);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Renter has not rented the specified listing in the past week.\n");
            }
            else{
                System.out.println("Successfully reviewed listing!\n");
            }
        } catch (SQLException e) {
            System.out.println("[renterReviewListing Error] " + e.getMessage());
        }
    }

    public void hostReviewRenter(int hostUserId, int renterUserId, int rating, String comment) {
        try {
            // Check if renter has rented any of host's listings in the past week
            String sql = "INSERT INTO Host_Review_Renter (host_userId, renter_userId, comment, rating) " +
                         "SELECT ?, ?, ?, ? " +
                         "FROM Booking B " +
                         "INNER JOIN Listing L ON B.listingId = L.listingId " +
                         "WHERE B.renter_userId = ? AND L.host_userId = ? AND " +
                         "B.created_at >= DATE_SUB(NOW(), INTERVAL 1 WEEK) " +
                         "LIMIT 1";
    
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hostUserId);
            stmt.setInt(2, renterUserId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.setInt(5, renterUserId);
            stmt.setInt(6, hostUserId);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Renter has not rented any of the host's listings in the past week.\n");
            }
            else{
                System.out.println("Successfully reviewed listing!\n");
            }
        } catch (SQLException e) {
            System.out.println("[hostReviewRenter Error] " + e.getMessage());
        }
    }

    public void renterReviewHost(int renterUserId, int hostUserId, int rating, String comment) {
        try {
            // Check if renter has rented any of host's listings in the past week
            String sql = "INSERT INTO Renter_Review_Host (renter_userId, host_userId, comment, rating) " +
                         "SELECT ?, ?, ?, ? " +
                         "FROM Booking B " +
                         "INNER JOIN Listing L ON B.listingId = L.listingId " +
                         "WHERE B.renter_userId = ? AND L.host_userId = ? AND " +
                         "B.created_at >= DATE_SUB(NOW(), INTERVAL 1 WEEK) " +
                         "LIMIT 1";
    
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, renterUserId);
            stmt.setInt(2, hostUserId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.setInt(5, renterUserId);
            stmt.setInt(6, hostUserId);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Renter has not rented any of the host's listings in the past week.\n");
                return;
            }
            else{
                System.out.println("Successfully reviewed host!\n");
            }
        } catch (SQLException e) {
            System.out.println("[renterReviewHost Error] " + e.getMessage());
        }
    
    }
}