package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.constant.UserType;

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

    public boolean createBooking(int listingId, int renterId, int hostId, Date startDate, Date endDate) {
        // check if dates are available (not booked)      
        // using calendarService.isListingAvailable(listingId, date), check every date between start and end date
        

        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        List<LocalDate> dates = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .collect(Collectors.toList());

        for (LocalDate date: dates){
            if (!calendarService.isListingAvailable(listingId, Date.valueOf(date))){
                System.out.println("[Booking Failed] Listing is not available for one of the dates selected: " + date.toString());
                return false;
            }
        }
        
        calendarService.updateListingAvailability(listingId, startDate, endDate, false);
        
        try{
            String sql = "INSERT INTO Booking (listingId, renter_userId, cancelledBy) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            stmt.setInt(2, renterId);
            stmt.setString(3, null);
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

    public boolean cancelBooking(int bookingId, UserType cancelledBy) {
        
        // Find the booking with bookingId and set cancelledBy (meaning cancelled)
        try {
            String sql = "UPDATE Booking SET cancelledBy = ? WHERE bookingId = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, cancelledBy.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate(sql);
            System.out.println("Successfully cancelled a booking!");
        } catch (Exception e) {
            System.out.println("[Cancel Booking Failed 1] " + e.getMessage());
            return false;
        }

        // Use CalendarService.updateListingAvailability() to update listing availability
        // if the host cancels the booking the day of the booking, set the availability of the current day to false
        try {
            int listingId = getListingId(bookingId);

            List<Date> bookedDates = calendarService.getBookedDates(listingId);
            Date currentDate = calendarService.getCurrentDate();
            // if the booking is cancelled the day of the booking, set the availability of the current day to false
            if (bookedDates.contains(currentDate)) {
                calendarService.updateListingAvailability(listingId, currentDate, false);
            }
            
        } catch (Exception e) {
            System.out.println("[Cancel Booking Failed 2] " + e.getMessage());
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
}
