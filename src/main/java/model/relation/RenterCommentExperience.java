package model.relation;

import lombok.Data;

@Data
public class RenterCommentExperience {
    private int renterUid; // PK 
    private int bookingId; // PK 
    private int reviewId; // FK
    private int timestamp;
}
