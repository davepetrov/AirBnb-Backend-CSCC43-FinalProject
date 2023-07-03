package model.entity;

import lombok.Data;

@Data
public class Calendar {
    private int calendarId; // PK
    private int listingId; // PK FK
    private String availabilityDate; // PK
    private int price;
    private boolean isAvailable;
}
