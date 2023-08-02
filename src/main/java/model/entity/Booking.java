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
        String bookingString = "Booking ID: " + bookingId + "\n"
            + "Listing ID: " + listingId + "\n"
            + "Renter User ID: " + renter_userId + "\n";
        if (cancelledBy != null) {
            System.out.println("Cancelled By: " + cancelledBy);
            System.out.println("Cancelled By: " + cancelledBy.name());
            bookingString += "Cancelled By: " + cancelledBy.name() + "\n";
        }
        return bookingString;
    }

}
