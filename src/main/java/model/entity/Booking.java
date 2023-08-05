package model.entity;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.constant.UserType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private int bookingId; // PK
    private int listingId; // FK
    private int renter_userId;
    private Date startDate;
    private Date endDate;
    private UserType cancelledBy;
    
    @Override
    public String toString() {
        String bookingString = "Booking ID: " + bookingId 
            + ", Listing ID: " + listingId
            + ", Renter User ID: " + renter_userId;
        if (cancelledBy != null) {
            bookingString += ", Cancelled By: " + cancelledBy.name() ;
        }
        return bookingString;
    }

}
