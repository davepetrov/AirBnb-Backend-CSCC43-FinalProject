package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.constant.UserType;
import model.entity.Booking;

import java.sql.Date;

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

    public static void main(String[] args){
        System.out.println("HI");
    }

    public boolean createBooking(int listingId, int renterId, int hostId, Date startDate, Date endDate) {
    
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.toList());

        // Using calendarService.isListingAvailable(listingId, date), check every date between start and end date

        for (LocalDate date: dates){
            if (!calendarService.isListingAvailable(listingId, Date.valueOf(date))){
                System.out.println("[Booking Failed] Listing is not available for one of the dates selected ("+  date.toString() +") between the startDate ("+  startDate.toString() +") and endDate ("+  endDate.toString());
                return false;
            }
        }
                
        try{
            String sql = "INSERT INTO Booking (listingId, renter_userId, cancelledBy, startDate, endDate) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setInt(2, renterId);
            stmt.setString(3, null);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);
            stmt.executeUpdate(sql);
            
        } catch (SQLException e) {
            System.out.println("[Booking Failed] " + e.getMessage());
            return false;
        }
        
        System.out.println("Successfully created a booking!");
        return true;
    }


    public boolean hostCancelBooking(int bookingId) {
        if (cancelBooking(bookingId, UserType.Host)){
            System.out.println("Host successfully cancelled a booking!");
            return true;
        }
        return false;
    }

    public boolean renterCancelBooking(int bookingId) {
        if (cancelBooking(bookingId, UserType.Renter)){
            System.out.println("Renter successfully cancelled a booking!");
            return true;
        }
        return false;
    }

    private boolean cancelBooking(int bookingId, UserType cancelledBy) {
        
        // Find the booking with bookingId and set cancelledBy (meaning cancelled)
        try {
            String sql = "UPDATE Booking SET cancelledBy = ? WHERE bookingId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cancelledBy.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate(sql);
            System.out.println("Successfully cancelled a booking!");
        } catch (Exception e) {
            System.out.println("[Cancel Booking Failed] " + e.getMessage());
            return false;
        }
        return true;

    }

    private int getListingId(int bookingId){
        try {
            String sql = "SELECT listingId FROM Booking WHERE bookingId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int listingId = rs.getInt("listingId"); 
            return listingId;    
        } catch (Exception e) {
            System.out.println("[Get Listing ID Failed] " + e.getMessage());
            return -1;
        }
    }

    public List<Booking> getBookingsByListingId(int listingId) {
        try {
            String sql = "SELECT bookingId, listingId, renter_userId, cancelledBy FROM Booking WHERE listingId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            ResultSet rs = stmt.executeQuery(sql);
            List<Booking> bookings = new ArrayList<Booking>();

            while (rs.next()){
                bookings.add(new Booking(
                    rs.getInt("bookingId"), 
                    rs.getInt("listingId"), 
                    rs.getInt("renter_userId"), 
                    UserType.valueOf(rs.getString("cancelledBy"))));
            }
            
            return bookings;
        } catch (Exception e) {
            System.out.println("[Get Bookings By Listing ID Failed] " + e.getMessage());
            return null;
        }
    }
}
