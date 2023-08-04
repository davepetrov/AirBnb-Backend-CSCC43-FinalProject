package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsService {
    
    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/69project";
    private final String USER = "root";
    private final String PASSWORD = "dp05092001";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public ReportsService() throws ClassNotFoundException, SQLException {

        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("Successfully connected to MySQL!");
    }

    /*  Report 1)
        Report and provide the total number of bookings in a
        specific date range by city. We would also wish to run the same report by zip
        code within a city 
    */
    public void TotalBookingsInSpecificDateRangeByCityOrPostalCode(String city, String postalCode, Date startDate, Date endDate) {
        String sql;
        if (city != null) {
            sql = "SELECT city, COUNT(*) as total_bookings " +
                "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                "WHERE startDate >= ? AND endDate <= ? AND city = ? " +
                "GROUP BY city";
        } else {
            sql = "SELECT city, postalCode, COUNT(*) as total_bookings " +
                "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                "WHERE startDate >= ? AND endDate <= ? AND postalCode = ? " +
                "GROUP BY postalCode";
        }

        try{
            PreparedStatement ps = conn.prepareStatement(sql);

            // Set parameters
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            if (city != null) {
                ps.setString(3, city);
            } else {
                ps.setString(3, postalCode);
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                if (city != null) {
                    System.out.println("City: " + resultSet.getString("city") + ", Total Bookings: " + resultSet.getInt("total_bookings"));
                } else {
                    System.out.println("City: " + resultSet.getString("city") + ", Postal Code: " + resultSet.getString("postalCode") + ", Total Bookings: " + resultSet.getInt("total_bookings"));
                }
            }
            
        } catch (SQLException e) {
            System.out.println("[Error in TotalBookingsInSpecificDateRangeByCityOrPostalCode] " + e.getMessage());
        }
    }

    /*  Report 2)
        Report and provide the total number of listings per:
        i.      country, 
        ii.     country and city
        iii.    country, city and postal code
    */

    public void TotalNumberOfListings(String country, String city, String postalCode){
        String sql;
        // case i.
        if (country != null && city == null && postalCode == null){
            sql="SELECT country, COUNT(*) as total_listings"+
                "GROUP BY country";
        }
        // case ii.

        else if (country != null && city != null && postalCode == null){
            sql = "SELECT country, city, COUNT(*) as total_listings"+
                    "GROUP BY country, city";
        }
        // case iii.
        else if (country != null && city != null && postalCode != null){
            sql = "SELECT country, city, postalCode, COUNT(*) as total_listings"+
                    "GROUP BY country, city, postalCode";
        }
        else{
            System.out.println("Invalid input");
            return;
        }

        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                if (city == null){
                    System.out.println("Country: " + resultSet.getString("country") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
                }
                else if (postalCode == null){
                    System.out.println("Country: " + resultSet.getString("country") + 
                                        ", City: " + resultSet.getString("city") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
                }
                else{
                    System.out.println("Country: " + resultSet.getString("country") + 
                                        ", City: " + resultSet.getString("city") + 
                                        ", Postal Code: " + resultSet.getString("postalCode") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error in TotalNumberOfListings]: " + e.getMessage());
        }
    }

    /*  Report 3)
        Rank the hosts by the total number of listings they have
        overall per country, but also be able to refine this reporting for the hosts
        based on the number of listings they have by city.
    */
    public void RankHostsByNumberOfListings(String country, String city) {
        String sql;
        if (country != null && city == null) {
            sql = "SELECT host_userId, country, COUNT(*) as total_listings, RANK() OVER (ORDER BY COUNT(*) DESC) as rank " +
                  "FROM Listing " +
                  "WHERE country = ? " +
                  "GROUP BY host_userId, country " +
                  "ORDER BY total_listings DESC, host_userId";
        } else if (country != null && city != null) {
            sql = "SELECT host_userId, country, city, COUNT(*) as total_listings, RANK() OVER (ORDER BY COUNT(*) DESC) as rank " +
                  "FROM Listing " +
                  "WHERE country = ? AND city = ? " +
                  "GROUP BY host_userId, country, city " +
                  "ORDER BY total_listings DESC, host_userId";
        } else {
            System.out.println("Invalid input");
            return;
        }

        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            // Set parameters
            ps.setString(1, country);
            if (city != null) {
                ps.setString(2, city);
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                if (city == null) {
                    System.out.println("Host ID: " + resultSet.getInt("host_userId") + ", Country: " + resultSet.getString("country") + 
                                       ", Total Listings: " + resultSet.getInt("total_listings") + ", Rank: " + resultSet.getInt("rank"));
                } else {
                    System.out.println("Host ID: " + resultSet.getInt("host_userId") + ", Country: " + resultSet.getString("country") + 
                                       ", City: " + resultSet.getString("city") + ", Total Listings: " + resultSet.getInt("total_listings") + 
                                       ", Rank: " + resultSet.getInt("rank"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /*  Report 4)
        For every city and country a report should provide the hosts that have a
        number of listings that is more than 10% of the number of listings in that
        city and country. This is a query that identifies the possible commercial
        hosts, something that the system should flag and prohibit.
    */
    public void IdentifyCommercialHosts() {
        String sql = "SELECT host_userId, country, city, COUNT(*) as host_listings " +
                     "FROM Listing " +
                     "GROUP BY host_userId, country, city " +
                     "HAVING host_listings > ( " +
                     "  SELECT COUNT(*) * 0.10 " +
                     "  FROM Listing as L " +
                     "  WHERE L.country = Listing.country AND L.city = Listing.city " +
                     ")";
    
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
    
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                System.out.println("Host ID: " + resultSet.getInt("host_userId") + 
                                   ", Country: " + resultSet.getString("country") + 
                                   ", City: " + resultSet.getString("city") + 
                                   ", Host Listings: " + resultSet.getInt("host_listings"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /*  Report 5)
        i)  Rank the renters by the number of bookings in a
            specific time period. 
        ii) Rank them by number of bookings in a
            specific time period per city. We are only interested in
            ranking those renters that have made at least two bookings in the year.
    */
    public void RankRentersByBookings(Date startDate, Date endDate, int year) {
        // Ranking renters by the number of bookings in a specific time period
        String sql1 = "SELECT renterId, COUNT(*) as total_bookings " +
                      "FROM Booking " +
                      "WHERE startDate >= ? AND endDate <= ? " +
                      "GROUP BY renterId " +
                      "ORDER BY total_bookings DESC";
                      
        // Ranking renters by number of bookings in a specific time period per city, 
        // for renters that have made at least two bookings in the year
        String sql2 = "SELECT B.renterId, L.city, COUNT(*) as total_bookings " +
                      "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                      "WHERE B.startDate >= ? AND B.endDate <= ? AND " +
                      "      B.renterId IN ( " +
                      "          SELECT renterId " +
                      "          FROM Booking " +
                      "          WHERE YEAR(startDate) = ? OR YEAR(endDate) = ? " +
                      "          GROUP BY renterId " +
                      "          HAVING COUNT(*) >= 2 " +
                      "      ) " +
                      "GROUP BY B.renterId, L.city " +
                      "ORDER BY total_bookings DESC";
    
        try {
            // Execute sql1
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setDate(1, startDate);
            ps1.setDate(2, endDate);
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                System.out.println("Renter ID: " + rs1.getInt("renterId") + 
                                   ", Total Bookings: " + rs1.getInt("total_bookings"));
            }
    
            // Execute sql2
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setDate(1, startDate);
            ps2.setDate(2, endDate);
            ps2.setInt(3, year);
            ps2.setInt(4, year);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                System.out.println("Renter ID: " + rs2.getInt("renterId") + 
                                   ", City: " + rs2.getString("city") + 
                                   ", Total Bookings: " + rs2.getInt("total_bookings"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /*  Report 6)
        Hosts and renters with the largest number of
        cancellations within a year
    */
    public void FindMaxCancellations(int year) {
        String sql = "SELECT * FROM " +
                     "((SELECT L.hostId AS userId, 'host' AS userType, COUNT(*) as total_cancellations " +
                     "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                     "WHERE B.cancelled = true AND (YEAR(B.startDate) = ? OR YEAR(B.endDate) = ?) " +
                     "GROUP BY L.hostId " +
                     "ORDER BY total_cancellations DESC " +
                     "LIMIT 1) " +
    
                     "UNION ALL " +
    
                     "(SELECT B.renterId AS userId, 'renter' AS userType, COUNT(*) as total_cancellations " +
                     "FROM Booking B " +
                     "WHERE B.cancelled = true AND (YEAR(B.startDate) = ? OR YEAR(B.endDate) = ?) " +
                     "GROUP BY B.renterId " +
                     "ORDER BY total_cancellations DESC " +
                     "LIMIT 1)) AS result " +
                     "ORDER BY total_cancellations DESC";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, year);
            ps.setInt(2, year);
            ps.setInt(3, year);
            ps.setInt(4, year);
    
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("User ID: " + rs.getInt("userId") + 
                                   ", User Type: " + rs.getString("userType") + 
                                   ", Total Cancellations: " + rs.getInt("total_cancellations"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /*  TODO: Report 7)
        Since renters comment on the listings, the listings accumulate comments in
        text form. We would like to run a report that presents for each listing the set
        of most popular noun phrases associated with the listing. That can form the
        basis of creating a word cloud for each listing that represents what renters
        say. You do not have to create any visualization as part of this project, only
        report the noun phrases
    */
}
