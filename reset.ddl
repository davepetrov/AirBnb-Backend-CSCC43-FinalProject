-- -----------------------ENTITIES/RELATIONS-------------------------
DROP TABLE IF EXISTS Calendar;
DROP TABLE IF EXISTS Listing_Offers_Amenities;
DROP TABLE IF EXISTS Renter_Review_Host;
DROP TABLE IF EXISTS Renter_Review_Listing;
DROP TABLE IF EXISTS Host_Review_Renter;
DROP TABLE IF EXISTS Amenities;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Listing;
DROP TABLE IF EXISTS BNBUser;

-- -----------------------TRIGGERS-------------------------

DROP TRIGGER IF EXISTS UpdateAvailabilityOnCancelTrigger;
DROP TRIGGER IF EXISTS UpdateAvailabilityOnUNDOCancelTrigger;

-- -----------------------PROCEDURE-------------------------
DROP PROCEDURE IF EXISTS CreateBookingAndUpdateCalendar;
DROP PROCEDURE IF EXISTS InsertRecordsIntoCalendar;