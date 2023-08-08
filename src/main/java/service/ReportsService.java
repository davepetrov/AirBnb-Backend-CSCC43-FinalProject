package service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsService {
    
    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/mybnb";
    private final String USER = "root";
    private final String PASSWORD = "AtTJ#;s|o|PP$?KJ";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    public ReportsService() throws ClassNotFoundException, SQLException {

        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("\n");
    }

    /*  Report 1)
        Report and provide the total number of bookings in a
        specific date range by city. We would also wish to run the same report by zip
        code within a city 
    */
    public void TotalBookingsInSpecificDateRangeByCityOrPostalCode(String city, String postalCode, Date startDate, Date endDate) {
        String sql;
        if (postalCode == null) {
            sql = "SELECT city, COUNT(*) as total_bookings " +
                "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                "WHERE B.startDate >= ? AND B.endDate <= ? AND L.city = ? " +
                "GROUP BY city";
        } else {
            sql = "SELECT city, postalCode, COUNT(*) as total_bookings " +
                "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                "WHERE B.startDate >= ? AND B.endDate <= ? AND L.postalCode = ? AND L.city= ? " +
                "GROUP BY city, postalCode";
        }

        try{
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Set parameters
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);

            if (postalCode == null) {
                ps.setString(3, city);
            } else {
                ps.setString(3, postalCode);
                ps.setString(4, city);
            }

            ResultSet resultSet = ps.executeQuery();

            // Check to see if any results
            int count = 0;
            while (resultSet.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("No results found, perhaps no bookings were made in the specified date range in this city and/or postal code?");
                return;
            }
            // reset rs1
            resultSet.beforeFirst();

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

    public void TotalNumberOfListings(int selection){
        String sql;
        // case i.
        if (selection==1){
            sql="SELECT country, COUNT(*) as total_listings FROM Listing "+
                "GROUP BY country";
        }
        // case ii.

        else if (selection==2){
            sql = "SELECT country, city, COUNT(*) as total_listings FROM Listing "+
                    "GROUP BY country, city";
        }
        // case iii.
        else if (selection==3){
            sql = "SELECT country, city, postalCode, COUNT(*) as total_listings  FROM Listing "+
                    "GROUP BY country, city, postalCode";
        }
        else{
            System.out.println("Invalid input");
            return;
        }

        try{
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = ps.executeQuery();

            // Check to see if any results
            int count = 0;
            while (resultSet.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("No results found, perhaps no listings");
                return;
            }
            // reset rs1
            resultSet.beforeFirst();

            while (resultSet.next()){
                if (selection==1){
                    System.out.println("Country: " + resultSet.getString("country") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
                }
                else if (selection==2){
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
        if (city == null) {
            sql = "SELECT DENSE_RANK() OVER (ORDER BY COUNT(*) DESC) as `ranking`, host_userId, country, COUNT(*) as total_listings " +
                  "FROM Listing " +
                  "WHERE country = ? " +
                  "GROUP BY host_userId, country " +
                  "ORDER BY total_listings DESC, host_userId";
        } else {
            sql = "SELECT DENSE_RANK() OVER (ORDER BY COUNT(*) DESC) as `ranking`, host_userId, country, city, COUNT(*) as total_listings " +
                  "FROM Listing " +
                  "WHERE country = ? AND city = ? " +
                  "GROUP BY host_userId, country, city " +
                  "ORDER BY total_listings DESC, host_userId";
        }

        try {
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Set parameters
            ps.setString(1, country);
            if (city != null) {
                ps.setString(2, city);
            }

            ResultSet resultSet = ps.executeQuery();

            // Check to see if any results
            int count = 0;
            while (resultSet.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\nNo results found, perhaps non existing country and/or city");
                return;
            }
            // reset rs1
            resultSet.beforeFirst();

            while (resultSet.next()) {
                if (city == null) {
                    System.out.println( "Ranking: " + resultSet.getInt("ranking")+
                                        ", Host ID: " + resultSet.getInt("host_userId") + 
                                        ", Country: " + resultSet.getString("country") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
                } else {
                    System.out.println( "Ranking: " + resultSet.getInt("ranking")+
                                        ", Host ID: " + resultSet.getInt("host_userId") + 
                                        ", Country: " + resultSet.getString("country") + 
                                        ", City: " + resultSet.getString("city") + 
                                        ", Total Listings: " + resultSet.getInt("total_listings"));
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
        String sql = "SELECT host_userId, country, city, COUNT(*) as host_listings, " +
                    "(SELECT COUNT(*) FROM Listing L2 WHERE L2.city = Listing.city AND L2.country = Listing.country) as total_listings_in_city, " +
                    "(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Listing L2 WHERE L2.city = Listing.city AND L2.country = Listing.country)) as percentage_owned " +
                    "FROM Listing " +
                    "GROUP BY host_userId, country, city " +
                    "HAVING host_listings > total_listings_in_city * 0.10";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    
            ResultSet resultSet = ps.executeQuery();

            // Check to see if any results
            int count = 0;
            while (resultSet.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\ni) No results found, perhaps no commercial hosts for any city");
                return;
            }
            // reset rs1
            resultSet.beforeFirst();


            
            System.out.println("i) The following hosts are commercial hosts in their specific city:\n");
            while (resultSet.next()) {
                System.out.println("Host ID: " + resultSet.getInt("host_userId") + 
                                    ", Country: " + resultSet.getString("country") + 
                                    ", City: " + resultSet.getString("city") + 
                                    ", Host Listings: " + resultSet.getInt("host_listings") +
                                    ", Total Listings in City: " + resultSet.getInt("total_listings_in_city") +
                                    ", Percentage Owned: " + resultSet.getDouble("percentage_owned") + "%");
            }
        } catch (SQLException e) {
            System.out.println("[IdentifyCommercialHosts Error] " + e.getMessage());
        }

        String sql2 = "SELECT host_userId, country, COUNT(*) as host_listings, " +
                        "(SELECT COUNT(*) FROM Listing L2 WHERE L2.country = Listing.country) as total_listings_in_country, " +
                        "(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Listing L2 WHERE L2.country = Listing.country)) as percentage_owned " +
                        "FROM Listing " +
                        "GROUP BY host_userId, country " +
                        "HAVING host_listings > total_listings_in_country * 0.10";
                    
        try {
            PreparedStatement ps = conn.prepareStatement(sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    
            ResultSet rs = ps.executeQuery();
            // Check to see if any results
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\nii) No results found, perhaps no commercial hosts for any country");
                return;
            }
            // reset rs
            rs.beforeFirst();

            System.out.println("\nii) The following hosts are commercial hosts in their specific Country:\n");

            while (rs.next()) {
                System.out.println("Host ID: " + rs.getInt("host_userId") + 
                                ", Country: " + rs.getString("country") + 
                                ", Host Listings: " + rs.getInt("host_listings") +
                                ", Total Listings in Country: " + rs.getInt("total_listings_in_country") +
                                ", Percentage Owned: " + rs.getDouble("percentage_owned") + "%");
            }
        } catch (SQLException e) {
            System.out.println("[IdentifyCommercialHosts Error] " + e.getMessage());
        }
    }


    /*  Report 5)
        i)  Rank the renters by the number of bookings in a
            specific time period. 
        ii) Rank the renters by number of bookings in a
            specific time period per city. We are only interested in
            ranking those renters that have made at least two bookings in the year.
    */
    public void RankRentersByBookings(Date startDate, Date endDate) {
        // Ranking renters by the number of bookings in a specific time period
        String sql1="SELECT RANK() OVER(ORDER BY COUNT(*) DESC) as `ranking`, renter_userId, COUNT(*) as total_bookings "+
                    "FROM Booking  "+
                    "WHERE startDate >= ? AND endDate <= ? "+
                    "GROUP BY renter_userId  "+
                    "ORDER BY total_bookings DESC ";

        String sql2 = "SELECT " +
                            "RANK() OVER(PARTITION BY L.city ORDER BY COUNT(*) DESC) as rank_per_city, " +
                            "B.renter_userId, " + 
                            "L.city, " + 
                            "COUNT(*) as citywise_bookings " +
                        "FROM Booking B " +
                        "JOIN Listing L ON B.listingId = L.listingId " +
                        "WHERE B.startDate >= ? AND B.endDate <= ? " +
                        "AND B.renter_userId IN (" +
                            "SELECT renter_userId " +
                            "FROM Booking " +
                            "WHERE YEAR(startDate) = YEAR(endDate) " +
                            "GROUP BY renter_userId " +
                            "HAVING COUNT(*) >= 2 " +
                        ") " +
                        "GROUP BY B.renter_userId, L.city " +
                        "ORDER BY L.city, rank_per_city";
    
        try {
            // Execute sql1
            PreparedStatement ps1 = conn.prepareStatement(sql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps1.setDate(1, startDate);
            ps1.setDate(2, endDate);
            ResultSet rs1 = ps1.executeQuery();

            // Check to see if any results
            int count = 0;
            while (rs1.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\ni) No results found, perhaps no renters have made any bookings in the given time period");
                return;
            }
            // reset rs1
            rs1.beforeFirst();

            System.out.println("i) Rank the renters by the number of bookings in specific time period:\n");
            while (rs1.next()) {
                System.out.println("Rank: " + rs1.getInt("ranking") +
                                   ", Renter ID: " + rs1.getInt("renter_userId") + 
                                   ", Total Bookings: " + rs1.getInt("total_bookings"));
            }
    
            // Execute sql2
            PreparedStatement ps2 = conn.prepareStatement(sql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps2.setDate(1, startDate);
            ps2.setDate(2, endDate);
            ResultSet rs2 = ps2.executeQuery();

            // Check to see if any results
            count = 0;
            while (rs2.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\nii) No results found,  no renters have made any bookings in the given time period where users have made minimum 2 bookings in the same year");
                return;
            }
            // reset rs1
            rs2.beforeFirst();

            System.out.println("\nii) Rank the renters by number of bookings in a specific time period per city (Min # of bookings is 2):\n");
            while (rs2.next()) {
                System.out.println("Rank: " + rs2.getInt("rank_per_city") +
                                   ", City: " + rs2.getString("city") +
                                   ", Renter ID: " + rs2.getInt("renter_userId") + 
                                   ", Total bookings in the same year: " + rs2.getInt("citywise_bookings"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /*  Report 6)
        Hosts and renters with the largest number of
        cancellations within a year
    */
    public void FindMaxCancellationsForTheYear(int year) {
        String sql = "WITH HostsCancellations AS (" +
                    "SELECT L.host_userId AS userId, 'host' AS userType, COUNT(*) as total_cancellations " +
                    "FROM Booking B JOIN Listing L ON B.listingId = L.listingId " +
                    "WHERE B.cancelledBy = 'Host' AND (YEAR(B.startDate) = ? OR YEAR(B.endDate) = ?) " +
                    "GROUP BY L.host_userId " +
                    "ORDER BY total_cancellations DESC), " +
                    
                    "RentersCancellations AS (" +
                    "SELECT B.renter_userId AS userId, 'renter' AS userType, COUNT(*) as total_cancellations " +
                    "FROM Booking B " +
                    "WHERE B.cancelledBy = 'Renter' AND (YEAR(B.startDate) = ? OR YEAR(B.endDate) = ?) " +
                    "GROUP BY B.renter_userId " +
                    "ORDER BY total_cancellations DESC) " +
                    
                    "SELECT * FROM (" +
                    "SELECT * FROM HostsCancellations WHERE total_cancellations = (SELECT MAX(total_cancellations) FROM HostsCancellations) " +
                    "UNION ALL " +
                    "SELECT * FROM RentersCancellations WHERE total_cancellations = (SELECT MAX(total_cancellations) FROM RentersCancellations)) AS result " +
                    "ORDER BY userType, total_cancellations DESC";
                    
        try {
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, year);
            ps.setInt(2, year);
            ps.setInt(3, year);
            ps.setInt(4, year);
    
            ResultSet rs = ps.executeQuery();

            // Check to see if any results
            int count = 0;
            while (rs.next()) {
                count++;
            }
            if (count == 0) {
                System.out.println("\nNo results found, perhaps nobody has made any cancellations in the given year");
                return;
            }
            // reset rs
            rs.beforeFirst();

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
    public void FindPopularNounPhrasesFromRenterOnListingComments(){
    }
}
