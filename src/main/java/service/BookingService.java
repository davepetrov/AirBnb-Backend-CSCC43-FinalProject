package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class BookingService {

    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    private CalendarService calendarService;
        
    public BookingService() throws ClassNotFoundException, SQLException {
        calendarService = new CalendarService();

        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean createBooking(int listingId, int renterId, int hostId, Date startDate, Date endDate) {
        // check if dates are available (not booked)        
        if (calendarService.isListingAvailable(listingId, startDate) && calendarService.isListingAvailable(listingId, endDate)) {
            calendarService.updateListingAvailability(listingId, startDate, endDate, false);
        } else {
            System.out.println("[Booking Failed] Listing is not available for the dates selected.");
            return false;
        }
        
        try{
            String sql = "INSERT INTO Booking (listingId, renter_userId) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setInt(2, renterId);
            stmt.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.out.println("[Booking Failed] " + e.getMessage());
            return false;
        }
        
        System.out.println("Successfully created a booking!");
        return true;
    }

    public void hostCancelBooking(int bookingId) {
        // Use CalendarService.updateListingAvailability() to update listing availability
        // if the host cancels the booking within 24 hour of the booking, set the availability of the current day to false
        // TODO implement here
    }

    public void renterCancelBooking(int bookingId) {
        // Use CalendarService.updateListingAvailability() to update listing availability
        // TODO implement here
    }
}
