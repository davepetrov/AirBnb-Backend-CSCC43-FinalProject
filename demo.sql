select * from Calendar;
select * from BNBUser;
select * from Booking;
select * from Listing;
select * from Renter_Review_Host;
select * from Renter_Review_Listing;
select * from Host_Review_Renter;


select * from Booking JOIN Listing ON Listing.listingId = Booking.listingId;

-- listing 3, host 2, renter 4
-- lat 43.7, long -79.42


