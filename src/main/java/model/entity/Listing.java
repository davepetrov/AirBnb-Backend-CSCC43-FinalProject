package model.entity;

import lombok.Data;
import model.constant.ListingType;

@Data
public class Listing {
    private int listingId; // PK
    private int hostUid; // FK
    private ListingType listingType;
    private boolean isActive;
    private double locationLat;
    private double locationLong;
    private String postalCode;
    private String city;
    private String country;

    private double distance;
}
