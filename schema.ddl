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
    isActive BOOLEAN,
    locationLat FLOAT,
    locationLong FLOAT,
    postalCode VARCHAR(10),
    city VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP, -- trigger

    PRIMARY KEY (listingId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP, -- trigger

    PRIMARY KEY (bookingId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE Calendar (
    listingId INT,
    bookingId INT,
    availabilityDate DATE,
    price DECIMAL(10, 2),
    isAvailable BOOLEAN,

    PRIMARY KEY (listingId, availabilityDate),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (bookingId) REFERENCES Booking (bookingId)
        ON DELETE CASCADE
        ON UPDATE SET NULL
);

-------------------------RELATIONS-------------------------

CREATE TABLE Listing_Offers_Amenities (
    listingId INT,
    amenityId INT,
    PRIMARY KEY (listingId, amenityId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (amenityId) REFERENCES Amenities (amenityId)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Host_Review_Renter (
    hostUserId INT,
    renterUserId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP, -- trigger

    PRIMARY KEY (hostUserId, renterUserId),
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT CheckRating1 CHECK (rating BETWEEN 1 and 5)
);

CREATE TABLE Renter_Review_Host (
    renterUserId INT,
    hostUserId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP, -- trigger
    PRIMARY KEY (renterUserId, hostUserId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT CheckRating2 CHECK (rating BETWEEN 1 and 5)
);

CREATE TABLE Renter_Review_Listing (
    renterUserId INT,
    listingId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP, -- trigger

    PRIMARY KEY (renterUserId, listingId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT CheckRating3 CHECK (rating BETWEEN 1 and 5)
);

-------------------------TRIGGERS-------------------------

-- Trigger for when a booking created, update the calednar availability
CREATE TRIGGER ToBookAvailabilityTrigger
    AFTER INSERT ON Booking
    FOR EACH ROW
    BEGIN
        UPDATE Calendar
        SET isAvailable = 0
        WHERE listingId = NEW.listingId AND availabilityDate BETWEEN NEW.startDate AND NEW.endDate;
    END

-- Trigger for when a listing is deleted, Cancel all future bookings (By Host)
CREATE TRIGGER ToCancelBookingOnListingDeletion
    AFTER DELETE ON Listing
    FOR EACH ROW
    BEGIN
        UPDATE Booking
        SET cancelledBy = "Host"
        WHERE listingId = OLD.listingId AND availabilityDate > curdate();
    END


-- Trigger for when a host cancels the day of a booking, the availability of the listing is set to false for that current day
CREATE TRIGGER ToCancelBookingOnDayOf
    AFTER UPDATE ON Booking
    FOR EACH ROW
    BEGIN
        IF (NEW.startDate = curdate()) THEN
            UPDATE Calendar
            SET isAvailable = 0
            WHERE listingId = OLD.listingId AND availabilityDate = OLD.startDate;
        END IF;
    END

-- Trigger for when a booking is cancelled, the availability of the listing is set to true for the days of the cancelled booking
CREATE TRIGGER trg_booking_cancelled
AFTER UPDATE ON Booking
FOR EACH ROW
BEGIN
    -- Check if the 'cancelledBy' column has been updated to 'Host' or 'Renter'
    IF NEW.cancelledBy IS NOT NULL AND NEW.cancelledBy <> OLD.cancelledBy THEN
        -- Retrieve the bookingId of the cancelled booking
        DECLARE cancelledBookingId INT;
        SET cancelledBookingId = NEW.bookingId;

        -- Set the availability of the listing to true for the days of the cancelled booking
        UPDATE Calendar
        SET isAvailable = TRUE
        WHERE bookingId = cancelledBookingId;

    END IF;
END;

-------------------------CONFIGS-------------------------
INSERT INTO Amenities (amenityId, amenityName) VALUES 
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


