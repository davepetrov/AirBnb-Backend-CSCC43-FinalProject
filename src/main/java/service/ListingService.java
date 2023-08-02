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
        String sql = "INSERT INTO Listing (host_userId, listingType, isActive, locationLat, locationLong, postalCode, city, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, hostUserId);
            ps.setString(2, type.toString());
            ps.setBoolean(3, true);
            ps.setFloat(4, locationLat);
            ps.setFloat(5, locationLong);
            ps.setString(6, postalCode);
            ps.setString(7, city);
            ps.setString(8, country);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("a foreign key constraint fails")) {
                System.out.println("[Listing Creation Failed] HostId doesnt exist\n");
            } else {
                // Handle other SQLExceptions
                System.out.println("[Listing Creation Failed]An SQL exception occurred: " + e.getMessage() + "\n");
            }            
        }
    }

    public void updateListingActiveStatus(int listingId, boolean isActive){
        try {
            String sql = "UPDATE Listing SET isActive = ? WHERE listingId = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, listingId);
            ps.executeUpdate();
            System.out.println("Listing status updated successfully!");

        } catch (SQLException e) {
            System.out.println("[Listing Update Status Failed] " + e.getMessage());
        }
    }


    public void deleteListing(int listingId){
        try {
            String sql = "DELETE FROM Listing WHERE listingId = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, listingId);
            ps.executeUpdate();
            System.out.println("Listing deleted successfully!");

        } catch (SQLException e) {
            System.out.println("[Listing Delete Failed] " + e.getMessage());
        }   
    }


    public List<Listing> getAllActiveListings(){
        try {
            String sql = "SELECT * FROM Listing where isActive = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            List<Listing> listings = new ArrayList<Listing>();
            while (rs.next()){
                Listing listing = new Listing();
                listing.setListingId(rs.getInt("listingId"));
                listing.setHost_userId(rs.getInt("host_userId"));
                listing.setListingType(ListingType.valueOf(rs.getString("listingType")));
                listing.setActive(rs.getBoolean("isActive"));
                listing.setLocationLat(rs.getFloat("locationLat"));
                listing.setLocationLong(rs.getFloat("locationLong"));
                listing.setPostalCode(rs.getString("postalCode"));
                listing.setCity(rs.getString("city"));
                listing.setCountry(rs.getString("country"));
                listings.add(listing);
            }
            return listings;

        } catch (Exception e) {
            System.out.println("[Get All Active Listings Failed] " + e.getMessage());
            return null;
        }
    }
}
