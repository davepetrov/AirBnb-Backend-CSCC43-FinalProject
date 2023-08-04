package model.Dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingSearch {
    private int listingId;
    private String hostName;
    private double locationLat;
    private double locationLong;
    private String postalCode;
    private String city;
    private String country;

    private Double price;
    private Double distanceFromSearch;
    private Date availabilityDate;


    // to string
    @Override
    public String toString() {
        String listingString = "Listing ID: " + listingId
            + ", Host Name: " + hostName 
            + ", Location Lat: " + locationLat 
            + ", Location Long: " + locationLong
            + ", Postal Code: " + postalCode
            + ", City: " + city 
            + ", Country: " + country;
        if (price != null) {
            listingString += ", Price($): " + price ;
        }
        if (availabilityDate != null) {
            listingString += ", Date: " + availabilityDate ;
        }
        if (distanceFromSearch != null) {
            listingString += ", Distance(km): " + round(distanceFromSearch,2);
        }
        return listingString;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
