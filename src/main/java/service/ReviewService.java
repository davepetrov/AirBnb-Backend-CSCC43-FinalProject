package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public void renterReviewListing(int userId, int listingId, int rating, String comment) {
        // Check if renter has rented listingId in the past week
        // TODO implement here
    }

    public void hostReviewRenter(int hostUserId, int renterUserId, int rating, String comment) {
        // Check if renter has rented any of host's listings in the past week
        // TODO implement here
    }

    public void renterReviewHost(int renterUserId, int hostUserId, int rating, String comment) {
        // Check if renter has rented any of host's listings in the past week
        // TODO implement here
    }
}
