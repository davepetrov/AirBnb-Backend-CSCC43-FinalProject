package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReviewService {
    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    public ReviewService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean renterReviewListing(int userId, int listingId, int rating, String comment) {
        // TODO: Check if renter has rented listingId in the past week

        try {
            String sql = "INSERT INTO Renter_Review_Listing (renterUserId, listingId, comment, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("[Renter Review Listing Failed] " + e.getMessage());
            return false;
        }

        System.out.println("Successfully reviewed listing!");
        return true;
    }

    public boolean hostReviewRenter(int hostUserId, int renterUserId, int rating, String comment) {
        // TODO: Check if renter has rented any of host's listings in the past week
        
        try {
            String sql = "INSERT INTO Host_Review_Renter (hostUserId, renterUserId, comment, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hostUserId);
            stmt.setInt(2, renterUserId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("[Renter Review Listing Failed] " + e.getMessage());
            return false;
        }

        System.out.println("Successfully reviewed listing!");
        return true;
    }

    public boolean renterReviewHost(int renterUserId, int hostUserId, int rating, String comment) {
        // TODO: Check if renter has rented any of host's listings in the past week
        
        try {
            String sql = "INSERT INTO Renter_Review_Host (renterUserId, hostUserId, comment, rating) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, renterUserId);
            stmt.setInt(2, hostUserId);
            stmt.setString(3, comment);
            stmt.setInt(4, rating);
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("[Renter Review Listing Failed] " + e.getMessage());
            return false;
        }

        System.out.println("Successfully reviewed listing!");
        return true;
    }
}
