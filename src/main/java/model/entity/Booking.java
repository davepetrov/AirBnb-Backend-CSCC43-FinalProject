package model.entity;

import lombok.Data;

@Data
public class Booking {
    private int bookingId; // PK
    private int listingId; // FK
    private int renterUid;
}
