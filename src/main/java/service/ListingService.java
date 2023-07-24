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
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    private CalendarService calendarService;

    private BookingService bookingService;

    public ListingService() throws ClassNotFoundException, SQLException {
        CalendarService calendarService = new CalendarService();
        BookingService bookingService = new BookingService();

        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
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
            ps.setFloat(5, locationLat);
            ps.setFloat(6, locationLong);
            ps.setString(7, postalCode);
            ps.setString(8, city);
            ps.setString(9, country);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[Listing Creation Failed] " + e.getMessage());
        }
    }

    public void updateListingActiveStatus(int listingId, boolean isActive){
        try {
            String sql = "UPDATE Listing SET isActive = ? WHERE listingId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isActive);
            ps.setInt(2, listingId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[Listing Update Status Failed] " + e.getMessage());
        }


    }


    public void deleteListing(int listingId){
        try {
            String sql = "DELETE Listing WHERE listingId = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(3, listingId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[Listing Update Status Failed] " + e.getMessage());
        }    
    }


    public List<Listing> getAllActiveListings(){
        try {
            String sql = "SELECT bookingId FROM Booking where isActive = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            List<Listing> listings = new ArrayList<Listing>();
            while (rs.next()){
                Listing listing = new Listing();
                listing.setListingId(rs.getInt("listingId"));
                listing.setHostUid(rs.getInt("host_userId"));
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
            System.out.println("[Get All Bookings Failed] " + e.getMessage());
            return null;
        }
    }
}
