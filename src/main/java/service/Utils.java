package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    //Database credentials
    private final String CONNECTION = "jdbc:mysql://34.130.232.208/mybnb";
    private final String USER = "root";
    private final String PASSWORD = "AtTJ#;s|o|PP$?KJ";
    private final String CLASSNAME = "com.mysql.cj.jdbc.Driver";

    private Connection conn;

    // constructor
    public Utils() throws SQLException, ClassNotFoundException {
        //Register JDBC driver
        Class.forName(CLASSNAME);

        conn = DriverManager.getConnection(CONNECTION,USER,PASSWORD);
        System.out.println("\n");
    }

    public Boolean isValidAmenity(String amenityName) {
        return getAllAmenities().contains(amenityName);
    }

    public List<String> getAllAmenities(){
        try {
            String sql = "SELECT amenityName FROM Amenities";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<String> amenityNames = new ArrayList<String>();
            while (rs.next()) {
                amenityNames.add(rs.getString("amenityName"));
            }
            return amenityNames;
        } catch (SQLException e) {
            System.out.println("[Error getAllAmenities] " + e.getMessage());
            return null;
        }
    }

    public double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    
}
