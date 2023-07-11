package model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingSearch {
    private int listingId;
    private int hostUserId;
    private double locationLat;
    private double locationLong;
    private String postalCode;
    private String city;
    private String country;
    private double price;
    private double distanceFromSearch;
}
