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
            int reviewId = createReview(rating, comment);
            String sql = "INSERT INTO Renter_Review_Listing (renter_userId, listingId, reviewId) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.setInt(3, reviewId);
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
            int reviewId = createReview(rating, comment);
            String sql = "INSERT INTO Host_Review_Renter (hostUserId, renterUserId, reviewId) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hostUserId);
            stmt.setInt(2, renterUserId);
            stmt.setInt(3, reviewId);
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
            int reviewId = createReview(rating, comment);
            String sql = "INSERT INTO Renter_Review_Host (renter_userId, hostUserId, reviewId) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, renterUserId);
            stmt.setInt(2, hostUserId);
            stmt.setInt(3, reviewId);
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("[Renter Review Listing Failed] " + e.getMessage());
            return false;
        }

        System.out.println("Successfully reviewed listing!");
        return true;
    }

    private int createReview(int rating, String comment) {
        int reviewId = -1;

        try {
            
            String sql = "INSERT INTO review (rating, comment) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, rating);
            stmt.setString(2, comment);
            stmt.executeUpdate(sql);

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                reviewId=generatedKeys.getInt("reviewId");
            }

        } catch (Exception e) {
            System.out.println("[Create Review Failed] " + e.getMessage());
        }
        
        return reviewId;
    }
}
