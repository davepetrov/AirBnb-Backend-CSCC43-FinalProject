package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public BookingService() throws ClassNotFoundException, SQLException {

        //Register JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    public boolean createBooking(int listingId, int renterId, Date startDate, Date endDate) {
            
        try{
            String sql = "INSERT INTO Booking (listingId, renter_userId, cancelledBy) " +
            "SELECT ?, ?, NULL " +
            "FROM Calendar " +
            "WHERE listingId = ? " +
            "AND availabilityDate BETWEEN ? AND ? " +
            "AND isAvailable = TRUE" +
            "AND price != NULL" +
            "GROUP BY listingId, availabilityDate " +
            "HAVING COUNT(*) = DATEDIFF(?, ?) + 1;";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, listingId);
            stmt.setInt(2, renterId);
            stmt.setInt(3, listingId);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);
            stmt.setDate(6, startDate);
            stmt.setDate(7, endDate);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Get the auto-generated bookingId from the inserted row
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                int bookingId = -1;
                if (generatedKeys.next()) {
                    bookingId = generatedKeys.getInt(1);
                }
    
                if (bookingId == -1) {
                    System.out.println("[Booking Failed] Failed to get the generated bookingId.");
                    return false;
                }
    
                System.out.println("Successfully created a booking with bookingId: " + bookingId);
    
                // Update Calendar to set the bookingId for booked dates
                String updateSql = "UPDATE Calendar " +
                                   "SET bookingId = ? , isAvailable = false " +
                                   "WHERE listingId = ? " +
                                   "AND availabilityDate BETWEEN ? AND ? " +
                                   "AND isAvailable = TRUE;";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookingId);
                updateStmt.setInt(2, listingId);
                updateStmt.setDate(3, startDate);
                updateStmt.setDate(4, endDate);
                updateStmt.executeUpdate();
    
                return true;
            } else {
                System.out.println("[Booking Failed] There is a date (1 or more) between the startDate and endDate that is/are not available.");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("[Booking Failed] " + e.getMessage());
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
