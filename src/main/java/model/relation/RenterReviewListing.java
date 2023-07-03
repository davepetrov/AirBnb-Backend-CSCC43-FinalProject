package model.relation;

import lombok.Data;

@Data
public class RenterReviewListing {
    private int renterUid; // PK 
    private int listingId; // PK 
    private int reviewId; // FK
    private int timestamp;
}
