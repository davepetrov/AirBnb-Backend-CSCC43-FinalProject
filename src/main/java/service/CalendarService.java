package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Date;

public class CalendarService {

    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    public CalendarService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public void updateListingAvailability(int listingId, Date availabilityDate, boolean isAvailable) {
        try {
            String sql = "UPDATE Calendar SET isAvailable = ? WHERE listingId = ? AND availabilityDate = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, listingId);
            stmt.setDate(3, availabilityDate);
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            System.out.println("[Update Listing Availability Failed] " + e.getMessage());
        }
    }
    
    public boolean updateListingPrice(int listingId, Date date, float price) {
        // Update the listing price for this date ONLY if the listing is available (ie: not booked out)
        try {
            String sql = "UPDATE Calendar SET price = ? WHERE listingId = ? AND availabilityDate = ? AND isAvailable = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setFloat(1, price);
            stmt.setInt(2, listingId);
            stmt.setDate(3, date);
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            System.out.println("[Update Listing Price Failed] " + e.getMessage());
            return false;
        }
    }

    public boolean isListingAvailable(int listingId, Date date) {
        String sql = "SELECT * FROM Calendar WHERE listingId = ? AND availabilityDate = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, date);
            stmt.executeQuery();
            return true;
        } catch (Exception e) {
            System.out.println("[Is Listing Available Failed] " + e.getMessage());
            return false;
        }
    }

    public boolean isListingAvailable(int listingId, Date startDate, Date endDate) {
        String sql = "SELECT * FROM Calendar WHERE listingId = ? AND availabilityDate >= ? AND availabilityDate <= ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            stmt.executeQuery();
            return true;
        } catch (Exception e) {
            System.out.println("[Is Listing Available Failed] " + e.getMessage());
            return false;
        }
        
    }

}
