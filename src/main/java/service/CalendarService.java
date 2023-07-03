package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

public class CalendarService {

    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    public CalendarService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
    
    public void updateListingAvailability(int listingId, Date startDate, Date endDate, boolean availability) {
        // TODO implement here
    }

    public boolean updateListingPrice(int listingId, Date date, float price) {
        // check to see if date is booked. If it is, cannot update price, return false
        // else, return true
        // implement here
        return false;
    }

    public boolean isListingAvailable(int listingId, Date date) {
        // TODO implement here
        return false;
    }
}
