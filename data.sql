select * from BNBUser;
select * from Listing;
select * from Booking;
select * from Calendar;
select * from Amenities;
select * from Listing_Offers_Amenities;
select * from Host_Review_Renter;
select * from Renter_Review_Host;
select * from Renter_Review_Listing;

SELECT 
    l.listingId, 
    CONCAT(u.firstName, ' ', u.surName) AS hostFullName, 
    l.locationLat, 
    l.locationLong, 
    l.postalCode, 
    l.city, 
    l.country, 
    c.price, 
    c.availabilityDate
FROM 
    Listing AS l
JOIN 
    Calendar AS c ON l.listingId = c.listingId
JOIN 
    BNBUser AS u ON l.host_userId = u.userId
WHERE 
     c.price >= 0 
    AND l.postalCode = 20000
    AND l.city = 'Los Angeles'
    AND l.country = 'USA'
    AND c.bookingId IS NULL;
    

WITH CurrentAmenities AS (
    SELECT amenityName
    FROM Listing_Offers_Amenities
    WHERE listingId = 1
)

SELECT 
    a.amenityName,
    COALESCE((ROUND(AVG(price),2) - (SELECT ROUND(AVG(price),2) FROM Calendar WHERE bookingID IS NOT NULL)), 0) AS expectedRevenueIncrease 
FROM 
    Amenities a 
LEFT JOIN 
    Listing_Offers_Amenities la ON a.amenityName = la.amenityName 
LEFT JOIN 
    Calendar c ON la.listingId = c.listingId 
WHERE 
    c.bookingID IS NOT NULL
AND 
	-- go through amentities that this listing does not currently offer
    a.amenityName NOT IN (SELECT amenityName FROM CurrentAmenities) 
GROUP BY 
    a.amenityName
HAVING
    expectedRevenueIncrease > 0
ORDER BY 
    expectedRevenueIncrease DESC;
    

INSERT INTO BNBUser (firstName, surName, dob, sin, occupation, postalCode, city, country, creditcard) VALUES 
('John', 'Doe', '1985-05-20', '111-111-111', 'Engineer', '11111', 'Toronto', 'Canada', '1111 1111 1111'),
('Jane', 'Smith', '1987-02-28', '222-222-222', 'Doctor', '22222', 'Vancouver', 'Canada', '2345 2222 2222 2222'),
('Alice', 'Johnson', '1990-12-31', '333-333-333', 'Teacher', '22222', 'Los Angeles', 'USA', '3333 3333 3333'),
('Bob', 'Brown', '1988-04-15', '444-444-444', 'Designer', '33333', 'Chicago', 'USA', '4567 4444 4444 4444'),
('Charlie', 'Davis', '1992-07-01', '555-555-555', 'Artist', '44444', 'Austin', 'USA', '5678 5555 5555 5555'),
('Emma', 'Williams', '1990-10-10', '666-666-666', 'Software Developer', '55555', 'Seattle', 'USA', '1111 6666 6666'),
('Oliver', 'Johnson', '1989-09-09', '777-777-777', 'Architect', '66666', 'Boston', 'USA', '2222 7777 7777'),
('Sophia', 'Jones', '1992-08-08', '888-888-888', 'Lawyer', '77777', 'Dallas', 'USA', '3333 8888 8888'),
('Liam', 'Brown', '1988-07-07', '999-999-999', 'Journalist', '88888', 'San Diego', 'USA', '4444 9999 9999'),
('Isabella', 'Davis', '1991-06-06', '101-101-101', 'Chef', '99999', 'Philadelphia', 'USA', '5555 1010 1010');

INSERT INTO Listing (host_userId, listingType, locationLat, locationLong, postalCode, city, country) VALUES 
(1, 'Apartment', 43.70, -79.42, '10000', 'Toronto', 'Canada'),
(1, 'House', 49.28, -123.12, '10001', 'Toronto', 'Canada'),
(2, 'Guesthouse', 34.05, -118.25, '20000', 'Los Angeles', 'USA'),
(2, 'Hotel', 41.88, -87.63, '20001', 'Los Angeles', 'USA'),
(2, 'Apartment', 30.26, -97.74, '20002', 'Chicago', 'USA'),
(3, 'House', 47.60, -122.33, '30000', 'Chicago', 'USA'),
(3, 'Hotel', 42.36, -71.05, '30001', 'Chicago', 'USA'),
(3, 'Guesthouse', 32.77, -96.79, '300002', 'Chicago', 'USA'),
(4, 'Apartment', 32.71, -117.16, '400000', 'Toronto', 'Canada'),
(4, 'House', 39.95, -75.16, '400001', 'Toronto', 'Canada');


DELIMITER $$ 
CREATE PROCEDURE IF NOT EXISTS InsertRecordsIntoCalendar(
    IN p_listingId INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_price DECIMAL(10,2)
)
BEGIN
    DECLARE currDate DATE;

    SET currDate = p_startDate;

    WHILE currDate <= p_endDate DO
        INSERT INTO Calendar (listingId, availabilityDate, price, isAvailable, bookingId) 
        VALUES (p_listingId, currDate, p_price, TRUE, NULL);

        SET currDate = DATE_ADD(currDate, INTERVAL 1 DAY);
    END WHILE;
    
END 
$$ DELIMITER 

CALL InsertRecordsIntoCalendar(1,'2023-08-07', '2023-08-14', 50);
CALL InsertRecordsIntoCalendar(1,'2023-08-15', '2023-08-21', 50);
CALL InsertRecordsIntoCalendar(1,'2023-08-22', '2023-08-23', 80);
CALL InsertRecordsIntoCalendar(2,'2023-08-15', '2023-08-21', 40);
CALL InsertRecordsIntoCalendar(2,'2022-01-01', '2022-01-01', 80);
CALL InsertRecordsIntoCalendar(3,'2023-08-03', '2023-08-04', 160);
CALL InsertRecordsIntoCalendar(3, '2023-08-05', '2023-08-08', 190);
CALL InsertRecordsIntoCalendar(4, '2023-08-20', '2023-08-21', 60);


-- listing, renter, startdate, enddate, result
CALL CreateBookingAndUpdateCalendar(1, 1, '2023-08-07', '2023-08-14', @result1); -- book INTO THE FUTURE 	for 8 days
CALL CreateBookingAndUpdateCalendar(1, 2, '2023-08-15', '2023-08-21', @result2); -- book INTO THE FUTURE 	for 7 days
CALL CreateBookingAndUpdateCalendar(1, 1, '2023-08-22', '2023-08-23', @result3); -- book INTO THE FUTURE 	for 2 days
CALL CreateBookingAndUpdateCalendar(2, 1, '2023-08-15', '2023-08-21', @result4); -- book INTO THE FUTURE 	for 7 days
CALL CreateBookingAndUpdateCalendar(2, 2, '2022-01-01', '2022-01-01', @result5); -- booked IN THE PAST 		for 1 day
CALL CreateBookingAndUpdateCalendar(3, 4, '2023-08-3', '2023-08-4', @result6);	 -- book INTO THE FUTURE 	for 2 days
CALL CreateBookingAndUpdateCalendar(3, 9, '2023-08-5', '2023-08-8', @result7);	 -- book INTO THE FUTURE 	for 4 days
CALL CreateBookingAndUpdateCalendar(4, 10, '2023-08-20', '2023-08-21', @result8);-- book INTO THE FUTURE 	for 2 days

INSERT INTO Amenities (amenityName) VALUES
    ("Wifi"),
    ("Kitchen"),
    ("Washer"),
    ("Dryer"),
    ("Air conditioning"),
    ("Heating"),
    ("Dedicated workspace"),
    ("TV"),
    ("Hair dryer"),
    ("Iron"),
    ("Pool"),
    ("Hot tub"),
    ("Free parking"),
    ("EV charger"),
    ("Crib"),
    ("Gym"),
    ("BBQ grill"),
    ("Breakfast"),
    ("Indoor fireplace"),
    ("Smoking allowed"),
    ("Beachfront"),
    ("Waterfront"),
    ("Smoke alarm"),
    ("Carbon monoxide alarm");
    
INSERT INTO Listing_Offers_Amenities (listingId, amenityName) VALUES 
(1, 'Air conditioning'),
(1, 'BBQ grill'),
(1, 'Beachfront'),
(1, 'Wifi'),
(1, 'TV'),
(1, 'Pool'),
(1, 'Kitchen'),
(1, 'Gym'),
(2, 'Beachfront'),
(2, 'Breakfast'),
(2, 'Carbon monoxide alarm'),
(2, 'Dedicated workspace'),
(2, 'Dryer'),
(2, 'Heating'),
(3, 'EV charger'),
(3, 'Free parking'),
(3, 'Gym'),
(3, 'Hot tub'),
(3, 'Wifi'),
(3, 'Kitchen'),
(3, 'Pool'),
(4, 'Air conditioning'),
(4, 'Washer'),
(4, 'Wifi'),
(4, 'TV'),
(4, 'Smoking allowed'),
(5, 'BBQ grill'),
(5, 'Beachfront'),
(5, 'Wifi'),
(5, 'Kitchen'),
(5, 'Pool'),
(6, 'Air conditioning'),
(6, 'Heating'),
(6, 'Hot tub'),
(6, 'Indoor fireplace'),
(6, 'Iron'),
(6, 'Gym'),
(7, 'TV'),
(7, 'Pool'),
(7, 'Dedicated workspace'),
(7, 'Dryer'),
(7, 'Washer'),
(7, 'Hot tub'),
(7, 'Kitchen'),
(7, 'Wifi'),
(8, 'EV charger'),
(8, 'Free parking'),
(8, 'Wifi'),
(8, 'TV'),
(8, 'Kitchen'),
(8, 'Pool'),
(9, 'Air conditioning'),
(9, 'BBQ grill'),
(9, 'Wifi'),
(9, 'Indoor fireplace'),
(9, 'Iron'),
(9, 'Gym'),
(9, 'Kitchen'),
(10, 'Wifi'),
(10, 'TV'),
(10, 'Beachfront'),
(10, 'Breakfast'),
(10, 'Pool'),
(10, 'Kitchen'),
(10, 'Dedicated workspace'),
(10, 'Dryer');

-- --- SPECIFIC UPDATES -----
Update Booking SET cancelledBy='Host' WHERE bookingId=2;
Update Booking SET cancelledBy='Renter' WHERE bookingId=1;
Update Calendar SET price=200 WHERE listingId=1 AND availabilityDate='2023-08-21';
Update Calendar SET price=180 WHERE listingId=1 AND availabilityDate='2023-08-20';
Update Calendar SET price=160 WHERE listingId=1 AND availabilityDate='2023-08-19';
Update Calendar SET price=140 WHERE listingId=1 AND availabilityDate='2023-08-18';

