package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.sql.Date;

public class CalendarService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public CalendarService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		// Class.forName(System.getenv("CLASSNAME"));
        Class.forName("com.mysql.cj.jdbc.Driver");

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean updateListingAvailability(int listingId, Date availabilityDate, boolean isAvailable) {
        try {
            String sql = "INSERT INTO Calendar (listingId, availabilityDate, isAvailable) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE isAvailable = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, availabilityDate);
            stmt.setBoolean(3, isAvailable);
            stmt.setBoolean(4, isAvailable);

            // If rowsAffected > 0, it means the update was successful
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully updated listing availability!");
                return true;
            } else {
                System.out.println("XXXXX");
                return false;
            }

        } catch (Exception e) {
            System.out.println("[Update Listing Availability Failed] " + e.getMessage());
            return false;
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
            stmt.executeUpdate();

            System.out.println("Listing price updated successfully!");

            return true;
        } catch (Exception e) {
            System.out.println("[Update Listing Price Failed] " + e.getMessage());
            return false;
        }
    }

    public boolean getAvailabilityStatus(int listingId, Date date) {
        String sql = "SELECT isAvailable FROM Calendar WHERE listingId = ? AND availabilityDate = ?";
        boolean isAvailable = false;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, new java.sql.Date(date.getTime())); // Convert java.util.Date to java.sql.Date
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                isAvailable = rs.getBoolean("isAvailable");
                System.out.println("Listing is " + (isAvailable ? "AVAILABLE" : "NOT AVAILABLE") + " on this date.");
            } else {
                System.out.println("No availability information found for this date.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("[Get Availability Status Failed] " + e.getMessage());
        }

        return isAvailable;
    }

    public Map<Date, String> getAvailabilityStatus(int listingId, Date startDate, Date endDate) {
        Map<Date, String> availabilityStatusMap = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            availabilityStatusMap.put(new java.sql.Date(calendar.getTimeInMillis()), "Not Available (Host has not selected availability)");
            calendar.add(Calendar.DATE, 1);
        }

        String sql = "SELECT availabilityDate, isAvailable FROM Calendar WHERE listingId = ? AND availabilityDate >= ? AND availabilityDate <= ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setString(2, dateFormat.format(startDate));
            stmt.setString(3, dateFormat.format(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getDate("availabilityDate");
                boolean isAvailable = rs.getBoolean("isAvailable");
                String availability = isAvailable ? "Available" : "Not Available";
                availabilityStatusMap.put(date, availability);
            }
        } catch (Exception e) {
            System.out.println("[Get Availability Status Failed] " + e.getMessage());
        }


        return availabilityStatusMap;
    }
}
