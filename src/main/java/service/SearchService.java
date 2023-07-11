package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Dto.ListingSearch;
import model.entity.Booking;
import model.entity.Listing;


public class SearchService {

    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;
    private BookingService bookingService;
    private ListingService listingService;
    private UserService userService;

    private final int EARTH_RADIUS_KM = 6371;
    private final int DEFAULT_DIAMETER_KM = 10;
        
    public SearchService() throws ClassNotFoundException, SQLException {
        bookingService = new BookingService();
        listingService = new ListingService();
        userService = new UserService();

        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
        


    public List<ListingSearch> findListingsByLatitudeLongitudeWithDistanceSortByPrice(Date startDate, Date endDate, double latitude, double longitude, int radiusKm, boolean isAscending){

        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price, " +
            " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " WHERE c.availabilityDate BETWEEN ? AND ? "+
            "   AND c.isAvailable = true AND l.isActive = true AND l.isDeleted = false" +
            " HAVING distance <= ? "+
            " ORDER BY price "+(isAscending ? "ASC" : "DESC");

        System.out.println("[DEBUG SEARCH] Ordering by price");
        return findListingsByLatitudeLongitudeWithDistance(sql,  startDate, endDate, latitude, longitude, radiusKm);
    }


    public List<ListingSearch> findListingsByLatitudeLongitudeWithDistanceSortByDistance(Date startDate, Date endDate, double latitude, double longitude, int radiusKm){

        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price, " +
            " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " WHERE c.availabilityDate BETWEEN ? AND ?"+
            "   AND c.isAvailable = true AND l.isActive = true AND l.isDeleted = false" +
            " HAVING distance <= ? " +
            " ORDER BY distance ASC";

        System.out.println("[DEBUG SEARCH] Ordering by distance");
        return findListingsByLatitudeLongitudeWithDistance(sql, startDate, endDate, latitude, longitude, radiusKm);
    }

    private List<ListingSearch> findListingsByLatitudeLongitudeWithDistance(String sql, Date startDate, Date endDate, double latitude, double longitude, int radiusKm) {
    
        List<ListingSearch> results = new ArrayList<ListingSearch>();
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, latitude);
            ps.setDouble(2, longitude);
            ps.setDouble(3, latitude);
            ps.setDate(4, startDate);
            ps.setDate(5, endDate);
            ps.setInt(6, radiusKm);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int listingId = rs.getInt("listingId");
                int hostUserId = rs.getInt("host_userId");
                double listingLatitude = rs.getDouble("locationlat");
                double listingLongitude = rs.getDouble("locationLong");
                String postalCode = rs.getString("postalCode");
                String listingCity = rs.getString("city");
                String listingCountry = rs.getString("country");
                int listingPrice = rs.getInt("price");
                double distance = rs.getDouble("distance");

                ListingSearch listingResult = new ListingSearch(listingId, hostUserId, listingLatitude, listingLongitude, postalCode, listingCity, listingCountry, listingPrice, distance);
                results.add(listingResult);
                
            }

            return results;

        } catch (SQLException e) {
            System.out.println("[Search findListingsByLatitudeLongitudeWithDistance Failed] " + e.getMessage());
            return null;
        }

    }

    
    // public List<ListingSearch> findListingsByPostalCodeSortByPrice(Date startDate, Date endDate, double latitude, double longitude, int radiusKm, boolean isAscending){

    //     String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price, " +
    //         " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
    //         " FROM Listing AS l" +
    //         " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
    //         " WHERE c.availabilityDate BETWEEN ? AND ? "+
    //         "   AND c.isAvailable = true AND l.isActive = true AND l.isDeleted = false" +
    //         " HAVING distance <= ? "+
    //         " ORDER BY price "+(isAscending ? "ASC" : "DESC");

    //     System.out.println("[DEBUG SEARCH] Ordering by price in order of "+(isAscending ? "ASC" : "DESC"));
    //     return findListingsByLatitudeLongitudeWithDistance(sql,  startDate, endDate, latitude, longitude, radiusKm);
    // }

    // }
    // private List<ListingSearch> findListingsByPostalCode(Date startDate, Date endDate, String postalCode){

    // }
    
    // The system should also support exact search queries, by address. The search
    // will accept an address in the input and return the listing in that address if one
    // exists.


    public List<ListingSearch> findListingSearchByExactAddress(Date startDate, Date endDate, String postalCode, String city, String country, boolean isAscending){

        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " WHERE c.availabilityDate BETWEEN ? AND ?"+
            "   AND c.isAvailable = true AND l.isActive = true AND l.isDeleted = false AND l.postalCode = ? AND l.city = ? AND l.country = ?" + 
            " ORDER BY price "+(isAscending ? "ASC" : "DESC");
        
        List<ListingSearch> results = new ArrayList<ListingSearch>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDate(1, startDate);  
            ps.setDate(2, endDate);
            ps.setString(3, postalCode);
            ps.setString(4, city);
            ps.setString(5, country);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                ListingSearch listingResult = new ListingSearch();
                listingResult.setListingId(rs.getInt("listingId"));
                listingResult.setHostUserId(rs.getInt("host_userId"));
                listingResult.setLocationLat(rs.getDouble("locationLat"));
                listingResult.setLocationLong(rs.getDouble("locationLong"));
                listingResult.setPostalCode(rs.getString("postalCode"));
                listingResult.setCity(rs.getString("city"));
                listingResult.setCountry(rs.getString("country"));
                listingResult.setPrice(rs.getInt("price"));
                results.add(listingResult);
            }

            return results;

        } catch (SQLException ex){
            System.out.println("[Search findListingSearchByExactAddress Failed] " + ex.getMessage());
            return null;
        }
    }


    // TODO: Another mode of search should refine the above searches with a temporal
    // filter, meaning that we should also provide a date range that we are
    // interested in and the system should return listings which are available for
    // booking in the date range specified.

    // TODO: The system should support filters for the search fully. For example searching
    // by postal code for listings with a set of amenities and time window of
    // availability and a price range should be fully supported.
    //         """;
}
