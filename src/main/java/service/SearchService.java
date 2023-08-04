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
import model.constant.SortBy;

public class SearchService {

    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";
    
    private Connection conn;

    private Utils utils;

    private final int DEFAULT_DIAMETER_KM = 10;
        
    public SearchService() throws ClassNotFoundException, SQLException {
        utils = new Utils();

		// Class.forName(System.getenv("CLASSNAME"));
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
        
    public List<ListingSearch> findAvailableListingsByLatitudeLongitude(Date startDate, Date endDate, double latitude, double longitude, int radiusKm, boolean isAscending, SortBy sort){

        if (radiusKm <= 0) {
            radiusKm = DEFAULT_DIAMETER_KM;
        }
        String sql;
        if (startDate == null || endDate == null) {
            sql = "SELECT l.listingId, CONCAT(u.firstName, ' ', u.surName) AS hostFullName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate, " +
                " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
                " FROM Listing AS l" +
                " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
                " INNER JOIN BNBUser AS u ON l.host_userId = u.userId" +
                " WHERE c.availabilityDate >= CURDATE() "+
                "   AND c.isAvailable = true AND c.price>=0" +
                " HAVING distance <= ? ";
        }
        else {
            sql = "SELECT l.listingId, CONCAT(u.firstName, ' ', u.surName) AS hostFullName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate, " +
                " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
                " FROM Listing AS l" +
                " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
                " INNER JOIN BNBUser AS u ON l.host_userId = u.userId" +
                " WHERE c.availabilityDate BETWEEN ? AND ? "+
                "   AND c.isAvailable = true AND c.price>=0" +
                " HAVING distance <= ? ";
        }

        if (sort == SortBy.DISTANCE) {
            sql += "ORDER BY distance "+(isAscending ? "ASC" : "DESC");
        }
        else if (sort == SortBy.PRICE) {
            sql +=  "ORDER BY price "+(isAscending ? "ASC" : "DESC");
        }

        List<ListingSearch> results = new ArrayList<ListingSearch>();
        
        try {
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, latitude);
            ps.setDouble(2, longitude);
            ps.setDouble(3, latitude);
            if (startDate!=null && endDate!=null) {
                ps.setDate(4, startDate);
                ps.setDate(5, endDate);
                ps.setInt(6, radiusKm);
            }
            else{
                ps.setInt(4, radiusKm);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ListingSearch listingResult = new ListingSearch();
                listingResult.setListingId(rs.getInt("listingId"));
                listingResult.setHostName(rs.getString("hostFullName"));
                listingResult.setLocationLat(rs.getDouble("locationLat"));
                listingResult.setLocationLong(rs.getDouble("locationLong"));
                listingResult.setPostalCode(rs.getString("postalCode"));
                listingResult.setCity(rs.getString("city"));
                listingResult.setCountry(rs.getString("country"));
                listingResult.setPrice(rs.getDouble("price"));
                listingResult.setAvailabilityDate(rs.getDate("availabilityDate"));
                listingResult.setDistanceFromSearch(rs.getDouble("distance"));
                results.add(listingResult);   
            }
            return results;

        } catch (SQLException e) {
            System.out.println("[Search findAvailableListingsByLatitudeLongitude Failed] " + e.getMessage());
            return null;
        }    
    }

    public List<ListingSearch> findAvailableListingSearchByExactAddress(Date startDate, Date endDate, String postalCode, String city, String country){

        String sql;
        if (startDate != null && endDate != null) {
            sql = "SELECT l.listingId, CONCAT(u.firstName, ' ', u.surName) AS hostFullName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " INNER JOIN BNBUser AS u ON l.host_userId = u.userId" +
            " WHERE c.availabilityDate BETWEEN ? AND ?"+
            "   AND l.postalCode = ? AND  c.price>=0 AND l.city = ? AND l.country = ?";
        }

        else {
            sql = "SELECT l.listingId, CONCAT(u.firstName, ' ', u.surName) AS hostFullName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " INNER JOIN BNBUser AS u ON l.host_userId = u.userId" +
            " WHERE c.availabilityDate >= CURDATE()"+
            "   AND l.postalCode = ? AND c.price>=0 AND l.city = ? AND l.country = ?";
        }
        
        List<ListingSearch> results = new ArrayList<ListingSearch>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (startDate != null && endDate != null) {
                ps.setDate(1, startDate);  
                ps.setDate(2, endDate);
                ps.setString(3, postalCode);
                ps.setString(4, city);
                ps.setString(5, country);
            }
            else{
                ps.setString(1, postalCode);
                ps.setString(2, city);
                ps.setString(3, country);
            }
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                ListingSearch listingResult = new ListingSearch();
                listingResult.setListingId(rs.getInt("listingId"));
                listingResult.setHostName(rs.getString("hostFullName"));
                listingResult.setLocationLat(rs.getDouble("locationLat"));
                listingResult.setLocationLong(rs.getDouble("locationLong"));
                listingResult.setPostalCode(rs.getString("postalCode"));
                listingResult.setCity(rs.getString("city"));
                listingResult.setCountry(rs.getString("country"));
                listingResult.setPrice(rs.getDouble("price"));
                listingResult.setAvailabilityDate(rs.getDate("availabilityDate"));

                results.add(listingResult);
            }

            return results;

        } catch (SQLException ex){
            System.out.println("[Search findListingSearchByExactAddress Failed] " + ex.getMessage());
            return null;
        }
    }

    public List<ListingSearch> searchAvailableListingsByFilters(String postalCode, List<String> amenities, Date startDate, Date endDate, Double minPrice, Double maxPrice, boolean isAscending, SortBy sort) {
        
        String sql = "SELECT l.listingId, CONCAT(u.firstName, ' ', u.surName) AS hostFullName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate" +
                    " FROM Listing AS l" +
                    " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
                    " INNER JOIN Listing_Offers_Amenities AS la ON l.listingId = la.listingId" +
                    " INNER JOIN BNBUser AS u ON l.host_userId = u.userId" +
                    " INNER JOIN Amenities AS a ON a.amenityId = la.amenityId" +
                    " WHERE c.isAvailable = true AND c.price>=0";


        List<Object> params = new ArrayList<>();

        if (postalCode != null && !postalCode.isEmpty()) {
            sql += " AND l.postalCode = ?";
            params.add(postalCode);
        }

        if (amenities != null && !amenities.isEmpty()) {
            for (String a: amenities) {
                a = a.trim();
                if (!utils.isValidAmenity(a)) {
                    System.out.println("[Search searchListingsByFilters Failed] Invalid amenity: " + a);
                    return null;
                }
            }
            sql += " AND a.amenityName IN (" + getAmenityPlaceholders(amenities.size()) + ")";
            params.addAll(amenities);
        }

        if (startDate != null && endDate != null) {
            sql += " AND c.availabilityDate BETWEEN ? AND ?";
            params.add(startDate);
            params.add(endDate);
        }
        else{
            sql += " AND c.availabilityDate >= CURDATE()";
        }

        if (minPrice != null) {
            sql += " AND c.price >= ?";
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql += " AND c.price <= ?";
            params.add(maxPrice);
        }

        sql+= "GROUP BY l.listingId, u.firstName, u.surName, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, c.price, c.availabilityDate";

        if (sort == SortBy.DISTANCE) {
            sql += " ORDER BY distance "+(isAscending ? "ASC" : "DESC");
        }
        else if (sort == SortBy.PRICE) {
            sql +=  " ORDER BY price "+(isAscending ? "ASC" : "DESC");
        }

        List<ListingSearch> results = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            setStatementParameters(ps, params);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ListingSearch listingResult = new ListingSearch();
                listingResult.setListingId(rs.getInt("listingId"));
                listingResult.setHostName(rs.getString("hostFullName"));
                listingResult.setLocationLat(rs.getDouble("locationLat"));
                listingResult.setLocationLong(rs.getDouble("locationLong"));
                listingResult.setPostalCode(rs.getString("postalCode"));
                listingResult.setCity(rs.getString("city"));
                listingResult.setCountry(rs.getString("country"));
                listingResult.setPrice(rs.getDouble("price"));
                listingResult.setAvailabilityDate(rs.getDate("availabilityDate"));
                results.add(listingResult);
            }

            return results;

        } catch (SQLException ex) {
            System.out.println("[Search searchListingsByFilters Failed] " + ex.getMessage());
            return null;
        }
    }

    private String getAmenityPlaceholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(" ? ");
            if (i < count - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private void setStatementParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof Date) {
                ps.setDate(i + 1, (Date) param);
            } else if (param instanceof String) {
                ps.setString(i + 1, (String) param);
            } else if (param instanceof Double) {
                ps.setDouble(i + 1, (Double) param);
            }
        }
    }
}