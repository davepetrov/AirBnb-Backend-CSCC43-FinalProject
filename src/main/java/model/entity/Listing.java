package model.entity;

import lombok.Data;
import model.constant.ListingType;

@Data
public class Listing {
    private int listingId; // PK
    private int host_userId; // FK
    private ListingType listingType;
    private boolean isActive;
    private double locationLat;
    private double locationLong;
    private String postalCode;
    private String city;
    private String country;

    private Double distance;

    // to string
    @Override
    public String toString() {
        if (distance!=null){
            return "Listing [listingId=" + listingId + ", host_userId=" + host_userId + ", listingType=" + listingType
                    + ", isActive=" + isActive + ", locationLat=" + locationLat + ", locationLong=" + locationLong
                    + ", postalCode=" + postalCode + ", city=" + city + ", country=" + country + ", distance=" + distance
                    + "]";

        } else {
            return "Listing [listingId=" + listingId + ", host_userId=" + host_userId + ", listingType=" + listingType
                    + ", isActive=" + isActive + ", locationLat=" + locationLat + ", locationLong=" + locationLong
                    + ", postalCode=" + postalCode + ", city=" + city + ", country=" + country + "]";
        }
    }
}
