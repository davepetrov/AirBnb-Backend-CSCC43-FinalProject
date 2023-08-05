-------------------------ENTITIES-------------------------
DROP TABLE BNBUser;

DROP TABLE Calendar;

DROP TABLE Booking;

DROP TABLE Listing;

DROP TABLE Amenities;

-------------------------RELATIONS-------------------------

DROP TABLE Listing_Offers_Amenities;

DROP TABLE Host_Review_Renter;

DROP TABLE Renter_Review_Host;

DROP TABLE Renter_Review_Listing;

-------------------------TRIGGERS-------------------------

DROP TRIGGER ToBookAvailabilityTrigger;

DROP TRIGGER ToDeleteAvailabilityTrigger;