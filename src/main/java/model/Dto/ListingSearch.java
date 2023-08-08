package model.Dto;

import java.sql.Date;

import com.mysql.cj.util.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.Utils;

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
        try{
            Utils utils = new Utils();
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
                listingString += ", Distance(km): " + utils.round(distanceFromSearch,2);
            }
            return listingString;
        } catch(Exception e){
            System.out.println("[Error ListingSearch toString] " + e.getMessage());
            return null;
        }
    }
}
