package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.constant.ListingType;
import model.entity.Amenity;

public class ListingService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/mybnb";
    private final String USER = "root";
    private final String PASSWORD = "AtTJ#;s|o|PP$?KJ";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public ListingService() throws ClassNotFoundException, SQLException {

        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("\n");
    }
    
    
    public void createListing(int hostUserId, ListingType type, float locationLat, float locationLong, String postalCode, String city, String country, List<String> amenities){
    String sql = "INSERT INTO Listing (host_userId, listingType, locationLat, locationLong, postalCode, city, country) VALUES (?, ?, ?, ?, ?, ?, ?);";
    try{
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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

            // Get the ID of the listing we just inserted
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int listingId = rs.getInt(1);

                // Link amenities
                for (String amenity : amenities) {
                    String amenityIdSql = "SELECT amenityName FROM Amenities WHERE amenityName = ?;";
                    PreparedStatement amenityIdPs = conn.prepareStatement(amenityIdSql);
                    amenityIdPs.setString(1, amenity);
                    ResultSet amenityIdResults = amenityIdPs.executeQuery();
                    if (amenityIdResults.next()) {
                        String linkSql = "INSERT INTO Listing_Offers_Amenities (listingId, amenityName) VALUES (?, ?);";
                        PreparedStatement linkPs = conn.prepareStatement(linkSql);
                        linkPs.setInt(1, listingId);
                        linkPs.setString(2, amenity);
                        linkPs.executeUpdate();
                    } else {
                        System.out.println("Amenity not found: " + amenity);
                    }
                }
            }
        }

    } catch (SQLException e) {
        if (e.getMessage().contains("a foreign key constraint fails")) {
            System.out.println("\n[Listing Creation Failed] HostId doesnt exist\n");
        } else {
            // Handle other SQLExceptions
            System.out.println("\n[Listing Creation Failed] An SQL exception occurred: " + e.getMessage() + "\n");
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


    public List<String> getAmenities(int listingId) {
        String sql = "SELECT amenityName FROM Listing_Offers_Amenities WHERE listingId = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingId);
            ResultSet rs = ps.executeQuery();

            List<String> currentAmenities = new ArrayList<String>();
            System.out.println("\nYour listing currently has the following Amenities\n---------------------------------------------");

            while (rs.next()) {
                System.out.println(rs.getString("amenityName"));
                currentAmenities.add(rs.getString("amenityName"));
            }
            return currentAmenities;
        } catch (SQLException e) {
            System.out.println("\n[Get Amenities Failed] " + e.getMessage());
        }
        return null;
    }


    public void addAmenities(int listingId, List<String> amenities) {
        String sql = "INSERT INTO Listing_Offers_Amenities (listingId, amenityName) VALUES (?, ?);";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (String amenity : amenities) {
                ps.setInt(1, listingId);
                ps.setString(2, amenity);
                ps.addBatch();
            }
            int[] rowsAffected = ps.executeBatch();
            int totalRows = Arrays.stream(rowsAffected).sum();

            if (totalRows == amenities.size()) {
                System.out.println("\nAll amenities added!");
            } else {
                System.out.println("\nSome amenities were not added. Please check the listingId!");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")){
                System.out.println("\n[Add Amenity Failed] Amenity already exists");
            } else if (e.getMessage().contains("a foreign key constraint fails")) {
                System.out.println("\n[Add Amenity Failed] ListingId doesnt exist");
            } else {
                System.out.println("\n[Add Amenity Failed]: " + e.getMessage() + "\n");
            }
        }
    }


    public void removeAmenities(int listingId, List<String> amenities) {
        String sql = "DELETE FROM Listing_Offers_Amenities WHERE listingId = ? AND amenityName = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            for (String amenity : amenities) {
                ps.setInt(1, listingId);
                ps.setString(2, amenity);
                ps.addBatch();
            }
            int[] rowsAffected = ps.executeBatch();
            int totalRows = Arrays.stream(rowsAffected).sum();
    
            if (totalRows == amenities.size()) {
                System.out.println("\nAll amenities removed!");
            } else {
                System.out.println("\nSome amenities were not removed. Please check the listingId and the amenity names!");
            }
        } catch (SQLException e) {
            System.out.println("\n[Remove Amenity Failed] " + e.getMessage());
        }
    }

    public void getRecommendedAmenities(int listingId) {
        String sql = "WITH CurrentAmenities AS ("
                    + "    SELECT amenityName"
                    + "    FROM Listing_Offers_Amenities"
                    + "    WHERE listingId = ?"
                    + ")"
                    + "SELECT "
                    + "    a.amenityName,"
                    + "    COALESCE((ROUND(AVG(price),2) - (SELECT ROUND(AVG(price),2) FROM Calendar WHERE bookingID IS NOT NULL)), 0) AS expectedRevenueIncrease "
                    + "FROM "
                    + "    Amenities a "
                    + "LEFT JOIN "
                    + "    Listing_Offers_Amenities la ON a.amenityName = la.amenityName "
                    + "LEFT JOIN "
                    + "    Calendar c ON la.listingId = c.listingId "
                    + "WHERE "
                    + "    c.bookingID IS NOT NULL "
                    + "AND "
                    + "    a.amenityName NOT IN (SELECT amenityName FROM CurrentAmenities) "
                    + "GROUP BY "
                    + "    a.amenityName "
                    + "HAVING "
                    + "    expectedRevenueIncrease > 0 "
                    + "ORDER BY "
                    + "    expectedRevenueIncrease DESC;";
    
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingId);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                String amenityName = rs.getString("amenityName");
                double expectedRevenueIncrease = rs.getDouble("expectedRevenueIncrease");
                System.out.println("Amenity: " + amenityName + ", Expected Revenue Increase: $" + expectedRevenueIncrease);
            }
    
        } catch (SQLException e) {
            System.out.println("[Get Recommended Amenities Failed] " + e.getMessage());
        }
    }
}
