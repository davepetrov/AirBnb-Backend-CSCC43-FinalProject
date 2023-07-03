package model.relation;

import lombok.Data;

@Data
public class RenterReviewHost {
    // Renter-UID, Host-UID, RID, Timestamp
    private int renterUid; // PK
    private int hostUid; // PK
    private int reviewId; // FK
    private int timestamp;
}
