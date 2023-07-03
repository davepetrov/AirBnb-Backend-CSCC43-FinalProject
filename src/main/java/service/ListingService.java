package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import model.constant.ListingType;

public class ListingService {
    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;

    public ListingService() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
    
    
    public void createListing(int hostUserId, ListingType type, float locationLat, float locationLong, String postalCode, String city, String country){
        // Implement here
    }

    public void updateListingActiveStatus(int listingId, boolean isActive){
        // Implement here
    }


    public void deleteListing(int listingId){
        // Implement here
    }

}
