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

public class SearchService {

    //Database credentials
    private final String CONNECTION = System.getenv("CONNECTION");
    private final String USER = System.getenv("USER");
    private final String PASSWORD = System.getenv("PASSWORD");
    private Connection conn;
    private Utils utils;

    private final int DEFAULT_DIAMETER_KM = 10;
        
    public SearchService() throws ClassNotFoundException, SQLException {
        utils = new Utils();


        //Register JDBC driver
		Class.forName(System.getenv("CLASSNAME"));
        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }
        
    public List<ListingSearch> findListingsByLatitudeLongitudeWithDistanceSortByPrice(Date startDate, Date endDate, double latitude, double longitude, int radiusKm, boolean isAscending){

        if (radiusKm <= 0) {
            radiusKm = DEFAULT_DIAMETER_KM;
        }

        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price, " +
            " (6371 * acos(cos(radians( ? )) * cos(radians(l.locationLat)) * cos(radians(l.locationLong) - radians( ? )) + sin(radians( ? )) * sin(radians(l.locationLat)))) AS distance" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " WHERE c.availabilityDate BETWEEN ? AND ? "+
            "   AND c.isAvailable = true AND l.isActive = true" +
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
            "   AND c.isAvailable = true AND l.isActive = true" +
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
                double listingPrice = rs.getDouble("price");
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


    public List<ListingSearch> findListingSearchByExactAddressSortByPrice(Date startDate, Date endDate, String postalCode, String city, String country, boolean isAscending){

        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price" +
            " FROM Listing AS l" +
            " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
            " WHERE c.availabilityDate BETWEEN ? AND ?"+
            "   AND c.isAvailable = true AND l.isActive = true AND l.postalCode = ? AND l.city = ? AND l.country = ?" + 
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
                listingResult.setPrice(rs.getDouble("price"));
                results.add(listingResult);
            }

            return results;

        } catch (SQLException ex){
            System.out.println("[Search findListingSearchByExactAddress Failed] " + ex.getMessage());
            return null;
        }
    }

    public List<ListingSearch> searchListingsByFiltersSortByPrice(String postalCode, List<String> amenities, Date startDate, Date endDate, Double minPrice, Double maxPrice, boolean isAscending) {
        String sql = "SELECT l.listingId, l.host_userId, l.locationLat, l.locationLong, l.postalCode, l.city, l.country, l.price" +
                " FROM Listing AS l" +
                " INNER JOIN Calendar AS c ON l.listingId = c.listingId" +
                " INNER JOIN ListingAmenities AS la ON l.listingId = la.listingId" +
                " WHERE c.isAvailable = true AND l.isActive = true";

        List<Object> params = new ArrayList<>();

        if (postalCode != null) {
            sql += " AND l.postalCode = ?";
            params.add(postalCode);
        }

        if (amenities != null && !amenities.isEmpty()) {
            for (String a: amenities) {
                if (!utils.isValidAmenity(a)) {
                    System.out.println("[Search searchListingsByFilters Failed] Invalid amenity: " + a);
                    return null;
                }
            }
            sql += " AND la.amenityName IN (" + getAmenityPlaceholders(amenities.size()) + ")";
            params.addAll(amenities);
        }

        if (startDate != null && endDate != null) {
            sql += " AND c.availabilityDate BETWEEN ? AND ?";
            params.add(startDate);
            params.add(endDate);
        }

        if (minPrice != null) {
            sql += " AND l.price >= ?";
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql += " AND l.price <= ?";
            params.add(maxPrice);
        }

        sql += " ORDER BY l.price " + (isAscending ? "ASC" : "DESC");

        List<ListingSearch> results = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            setStatementParameters(ps, params);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ListingSearch listingResult = new ListingSearch();
                listingResult.setListingId(rs.getInt("listingId"));
                listingResult.setHostUserId(rs.getInt("host_userId"));
                listingResult.setLocationLat(rs.getDouble("locationLat"));
                listingResult.setLocationLong(rs.getDouble("locationLong"));
                listingResult.setPostalCode(rs.getString("postalCode"));
                listingResult.setCity(rs.getString("city"));
                listingResult.setCountry(rs.getString("country"));
                listingResult.setPrice(rs.getDouble("price"));
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
            sb.append("?");
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