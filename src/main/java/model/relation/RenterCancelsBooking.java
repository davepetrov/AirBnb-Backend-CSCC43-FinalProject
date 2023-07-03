package model.relation;

import lombok.Data;

@Data
public class RenterCancelsBooking {
    private int bookingId; // PK
    private int timestamp;
    private String reason;
}
