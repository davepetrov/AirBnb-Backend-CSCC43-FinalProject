package model.relation;

import lombok.Data;

@Data
public class ListingOffersAmeneties {
    private int listingId;  //FK
    private int amenityId;  //FK
}
