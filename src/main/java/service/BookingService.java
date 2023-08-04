package service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.constant.UserType;
import model.entity.Booking;

public class BookingService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public BookingService() throws ClassNotFoundException, SQLException {

        //Register JDBC driver
        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean createBooking(int listingId, int renterId, Date startDate, Date endDate) {
        try {
            String callProcedureSQL = "{CALL CreateBookingAndUpdateCalendar(?, ?, ?, ?, ?)}";
            CallableStatement callableStatement = conn.prepareCall(callProcedureSQL);
            callableStatement.setInt(1, listingId);
            callableStatement.setInt(2, renterId);
            callableStatement.setDate(3, startDate);
            callableStatement.setDate(4, endDate);
            callableStatement.registerOutParameter(5, Types.INTEGER);  // Register the OUT parameter
    
            callableStatement.executeUpdate();
    
            int result = callableStatement.getInt(5);  // Get the value of the OUT parameter
    
            if (result == 1) {
                System.out.println("\nSuccessfully created a booking and updated Calendar!");
                return true;
            } else {
                System.out.println("\nFAILED to create a booking! At least one of the days is unavailable.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("\n[Booking Failed] " + e.getMessage());
            return false;
        }
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
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("[Cancel Booking Failed] " + e.getMessage());
            return false;
        }
        return true;

    }

    public List<Booking> getBookingsByListingId(int listingId) {
        try {
            String sql = "SELECT B.bookingId, B.listingId, B.renter_userId, B.cancelledBy, MIN(C.availabilityDate) AS startDate, MAX(C.availabilityDate) AS endDate " +
                            "FROM Booking B " +
                            "LEFT JOIN Calendar C ON B.bookingId = C.bookingId " +
                            "WHERE B.listingId = ? " +
                            "GROUP BY B.bookingId, B.listingId, B.renter_userId, B.cancelledBy";
        
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, listingId);
            ResultSet rs = stmt.executeQuery();
            List<Booking> bookings = new ArrayList<Booking>();

            while (rs.next()){
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("bookingId"));
                booking.setListingId(rs.getInt("listingId"));
                booking.setRenter_userId(rs.getInt("renter_userId"));
                booking.setStartDate(rs.getDate("startDate"));
                booking.setEndDate(rs.getDate("endDate"));
                if (rs.getString("cancelledBy") != null) {
                    booking.setCancelledBy(UserType.valueOf(rs.getString("cancelledBy")));
                }
                else{
                    booking.setCancelledBy(null);
                }
                bookings.add(booking);
            }
            
            return bookings;
        } catch (Exception e) {
            System.out.println("[Get Bookings By Listing ID Failed] " + e.getMessage());
            return null;
        }
    }
}
