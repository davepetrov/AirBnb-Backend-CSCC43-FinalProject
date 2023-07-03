package model.entity;

import lombok.Data;

@Data
public class Listing {
    private int listingId; // PK
    private int hostUid; // FK
    private String type;
    private boolean isActive;
    private double locationLat;
    private double locationLong;
    private String postalCode;
    private String city;
    private String country;
}
