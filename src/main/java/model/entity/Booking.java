package model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import model.constant.UserType;

@Data
@AllArgsConstructor
public class Booking {
    private int bookingId; // PK
    private int listingId; // FK
    private int renterUid;
    private UserType cancelledBy;
}
