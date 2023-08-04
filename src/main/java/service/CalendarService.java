package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

public class CalendarService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public CalendarService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean updateListingAvailabilityAndPrice(int listingId, Map<Date, Double> availabilityAndPrice) {
        try {
            StringBuilder sql = new StringBuilder("INSERT INTO Calendar (listingId, availabilityDate, price, isAvailable) VALUES ");
    
            // Construct the values string
            for (Map.Entry<Date, Double> entry : availabilityAndPrice.entrySet()) {
                sql.append("(");
                sql.append(listingId).append(", ");
                sql.append("'").append(entry.getKey()).append("', ");
                sql.append(entry.getValue()).append(", ");
                sql.append("TRUE), ");
            }
    
            // Remove last comma and space
            sql.setLength(sql.length() - 2);
    
            sql.append(" ON DUPLICATE KEY UPDATE price = IF(bookingId IS NULL, VALUES(price), price), isAvailable = IF(bookingId IS NULL, TRUE, isAvailable)");
    
            // Execute the SQL statement
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql.toString());
    
            if (rowsAffected > 0) {
                System.out.println("Successfully updated listing availability and price for the days that were available... ");
                return true;
            } else {
                System.out.println("Failed to update listing availability and price. The listings may already be booked on these dates.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[Update Listing Availability And Price Failed] " + e.getMessage());
            return false;
        }
    }

    public boolean updateListingAvailabilityAndPrice(int listingId, Date availabilityDate, Double price) {
        try {
            String sql = "INSERT INTO Calendar (listingId, availabilityDate, price, isAvailable, bookingId) " +
                         "VALUES (?, ?, ?, TRUE, NULL) " +
                         "ON DUPLICATE KEY UPDATE price = IF(bookingId IS NULL, VALUES(price), price), isAvailable = IF(bookingId IS NULL, TRUE, isAvailable)";
    
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, availabilityDate);
            stmt.setDouble(3, price);
    
            int rowsAffected = stmt.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Successfully updated listing availability and price!");
                return true;
            } else {
                System.out.println("Failed to update listing availability and price. The listing may already be booked on these dates.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[Update Listing Availability And Price Failed] " + e.getMessage());
            return false;
        }
    }

    public boolean updateListingPrice(int listingId, Date startDate, Date endDate, float price) {
        // Update the listing price for this date ONLY if the listing is booked (ie: bookingId is not null)
        try {
            String sql = "UPDATE Calendar SET price = ? WHERE listingId = ? AND availabilityDate BETWEEN ? AND ? AND bookingId IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setFloat(1, price);
            stmt.setInt(2, listingId);
            stmt.setDate(3, startDate);
            stmt.setDate(4, endDate);
    
            int updatedRows = stmt.executeUpdate();
    
            if (updatedRows > 0) {
                System.out.println("Listing price updated successfully!");
                return true;
            } else {
                System.out.println("No bookings found for this listing in the specified date range, or the booking already has the specified price.");
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("[Update Listing Price Failed] " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateListingPrice(int listingId, Date date, float price) {
        // Update the listing price for this date ONLY if the listing is available (ie: not booked out)
        try {
            String sql = "UPDATE Calendar SET price = ? WHERE listingId = ? AND availabilityDate = ? AND bookingId IS NULL";
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
                System.out.println("Listing is NOT AVAILABLE on this date. (Host has not selected availability)");
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
            availabilityStatusMap.put(new java.sql.Date(calendar.getTimeInMillis()), "NOT AVAILABLE (Host has not selected availability)");
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
                String availability = isAvailable ? "AVAILABLE" : "NOT AVAILABLE";
                availabilityStatusMap.put(date, availability);
            }
        } catch (Exception e) {
            System.out.println("[Get Availability Status Failed] " + e.getMessage());
        }


        return availabilityStatusMap;
    }

    public void updateListingMakeUnavailable(int listingId, Map<Date, Double> datesPrices) {
        try {
            String sql = "UPDATE Calendar SET isAvailable = FALSE " +
                        "WHERE listingId = ? AND availabilityDate = ? AND bookingId IS NULL";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);

            for (Map.Entry<Date, Double> entry : datesPrices.entrySet()) {
                stmt.setDate(2, entry.getKey());
                stmt.addBatch();
            }

            int[] updateCounts = stmt.executeBatch();

            // Check to see if anything was updated
            int totalRowsAffected = Arrays.stream(updateCounts).sum();
            if (totalRowsAffected > 0) {
                System.out.println("\nListing availability updated successfully!");
            } else {
                System.out.println("\nListing availability update failed. No matching record found.");
            }
        } catch (Exception e) {
            System.out.println("[Update Listing Availability Failed 2] " + e.getMessage());
        }
    }

    public void updateListingMakeUnavailable(int listingId, Date availabilityDate) {
        try {
            String sql = "UPDATE Calendar SET isAvailable = FALSE " +
                         "WHERE listingId = ? AND availabilityDate = ? AND bookingId IS NULL";
    
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setDate(2, availabilityDate);
    
    
            if (stmt.executeUpdate() > 0) {
                System.out.println("\nListing availability updated successfully!");
            } else {
                System.out.println("\nListing availability update failed. No matching record found.");
            }
        } catch (Exception e) {
            System.out.println("[Update Listing Availability Failed] " + e.getMessage());
        }
    }
}
