-------------------------ENTITIES-------------------------
CREATE TABLE BNBUser (
    userId INT NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(255),
    surName VARCHAR(255),
    dob DATE,
    sin VARCHAR(255),
    occupation VARCHAR(255),
    postalCode VARCHAR(10),
    city VARCHAR(255),
    country VARCHAR(255),
    creditcard VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (userId)
);

CREATE TABLE Listing (
    listingId INT NOT NULL AUTO_INCREMENT,
    host_userId INT,
    listingType ENUM ('House', 'Apartment', 'Guesthouse', 'Hotel'),
    locationLat FLOAT,
    locationLong FLOAT,
    postalCode VARCHAR(10),
    city VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- trigger
    PRIMARY KEY (listingId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId) ON DELETE
    SET
        NULL ON UPDATE CASCADE
);

CREATE TABLE Amenities (
    amenityId INT NOT NULL,
    amenityName VARCHAR(255) UNIQUE,
    PRIMARY KEY(amenityId)
);

CREATE TABLE Booking (
    bookingId INT NOT NULL AUTO_INCREMENT,
    listingId INT,
    renter_userId INT,
    cancelledBy ENUM ('Host', 'Renter'),
    startDate DATE,
    endDate DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- trigger
    PRIMARY KEY (bookingId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId) ON DELETE
    SET
        NULL ON UPDATE CASCADE,
        FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId) ON DELETE
    SET
        NULL ON UPDATE CASCADE
);

CREATE TABLE Calendar (
    listingId INT,
    bookingId INT,
    availabilityDate DATE,
    price DECIMAL(10, 2),
    isAvailable BOOLEAN,
    PRIMARY KEY (listingId, availabilityDate),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (bookingId) REFERENCES Booking (bookingId) ON DELETE CASCADE ON UPDATE
    SET
        NULL
);

-------------------------RELATIONS-------------------------
CREATE TABLE Listing_Offers_Amenities (
    listingId INT,
    amenityId INT,
    PRIMARY KEY (listingId, amenityId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (amenityId) REFERENCES Amenities (amenityId) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Host_Review_Renter (
    hostUserId INT,
    renterUserId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- trigger
    PRIMARY KEY (hostUserId, renterUserId),
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CheckRating1 CHECK (
        rating BETWEEN 1
        and 5
    )
);

CREATE TABLE Renter_Review_Host (
    renterUserId INT,
    hostUserId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- trigger
    PRIMARY KEY (renterUserId, hostUserId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CheckRating2 CHECK (
        rating BETWEEN 1
        and 5
    )
);

CREATE TABLE Renter_Review_Listing (
    renterUserId INT,
    listingId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- trigger
    PRIMARY KEY (renterUserId, listingId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (listingId) REFERENCES Listing (listingId) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CheckRating3 CHECK (
        rating BETWEEN 1
        and 5
    )
);

-------------------------PROCEDURES / TRIGGERS-------------------------
-- When a booking takes place, update unavailability in Calendar
DELIMITER $ $ CREATE PROCEDURE CreateBookingAndUpdateCalendar(
    IN p_listingId INT,
    IN p_renterId INT,
    IN p_startDate DATE,
    IN p_endDate DATE,
    OUT p_result INT
) BEGIN DECLARE v_bookingId INT;

DECLARE v_available INT;

-- Check if all the dates for the booking are available
SELECT
    COUNT(*) INTO v_available
FROM
    Calendar
WHERE
    listingId = p_listingId
    AND availabilityDate BETWEEN p_startDate
    AND p_endDate
    AND isAvailable = TRUE;

-- If all the dates are available (v_available equals the number of days in the booking),
-- proceed with creating the booking
IF v_available = DATEDIFF(p_endDate, p_startDate) + 1 THEN
INSERT INTO
    Booking (
        listingId,
        renter_userId,
        cancelledBy,
        startDate,
        endDate
    )
VALUES
    (
        p_listingId,
        p_renterId,
        NULL,
        p_startDate,
        p_endDate
    );

SET
    v_bookingId = LAST_INSERT_ID();

-- Now that the booking has been created, update the Calendar table
UPDATE
    Calendar
SET
    isAvailable = FALSE,
    bookingId = v_bookingId
WHERE
    listingId = p_listingId
    AND availabilityDate BETWEEN p_startDate
    AND p_endDate;

-- Indicate success
SET
    p_result = 1;

ELSE -- Indicate failure
SET
    p_result = 0;

END IF;

END $ $ DELIMITER;

-- When a cancellation takes place, update unavailability in Calendar
DELIMITER $ $ CREATE TRIGGER UpdateAvailabilityOnCancelTrigger
AFTER
UPDATE
    ON Booking FOR EACH ROW BEGIN IF NEW.cancelledBy IS NOT NULL THEN
UPDATE
    Calendar
SET
    isAvailable = TRUE,
    bookingId = NULL
WHERE
    listingId = NEW.listingId
    AND availabilityDate BETWEEN NEW.startDate
    AND NEW.endDate;

END IF;

END;

$ $ DELIMITER;

-- When a UNDO cancellation takes place, update unavailability in Calendar
DELIMITER $ $ CREATE TRIGGER UpdateAvailabilityOnUNDOCancelTrigger
AFTER
UPDATE
    ON Booking FOR EACH ROW BEGIN IF NEW.cancelledBy IS NULL THEN
UPDATE
    Calendar
SET
    isAvailable = FALSE,
    bookingId = NEW.bookingId
WHERE
    listingId = NEW.listingId
    AND availabilityDate BETWEEN NEW.startDate
    AND NEW.endDate;

END IF;

END;

$ $ DELIMITER;

-- Trigger for when a listing is deleted, Cancel all future bookings (By Host)
DELIMITER $ $ CREATE TRIGGER CancelFutureBookingsOnListingDeletionTrigger
AFTER
    DELETE ON Listing FOR EACH ROW BEGIN
UPDATE
    Booking
SET
    cancelledBy = 'Host'
WHERE
    listingId = OLD.listingId
    AND startDate > CURDATE();

END;

$ $ DELIMITER;

-------------------------CONFIGS-------------------------
INSERT INTO
    Amenities (amenityId, amenityName)
VALUES
    (1, 'Wifi'),
    (2, 'Kitchen'),
    (3, 'Washer'),
    (4, 'Dryer'),
    (5, 'Air conditioning'),
    (6, 'Heating'),
    (7, 'Dedicated workspace'),
    (8, 'TV'),
    (9, 'Hair dryer'),
    (10, 'Iron'),
    (11, 'Pool'),
    (12, 'Hot tub'),
    (13, 'Free parking'),
    (14, 'EV charger'),
    (15, 'Crib'),
    (16, 'Gym'),
    (17, 'BBQ grill'),
    (18, 'Breakfast'),
    (19, 'Indoor fireplace'),
    (20, 'Smoking allowed'),
    (21, 'Beachfront'),
    (22, 'Waterfront'),
    (23, 'Smoke alarm'),
    (24, 'Carbon monoxide alarm');