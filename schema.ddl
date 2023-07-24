-------------------------ENTITIES-------------------------
CREATE TABLE BNBUser (
    userId INT NOT NULL AUTO INCREMENT,
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
    listingId INT NOT NULL AUTO INCREMENT,
    host_userId INT,
    listingType ENUM ('House', 'Apartment', 'Guesthouse', 'Hotel'),
    isActive NUMBER(1),
    locationLat FLOAT,
    locationLong FLOAT,
    postalCode VARCHAR(10),
    city VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP -- trigger

    PRIMARY KEY (listingId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE

    CONSTRAINT isAvailable CHECK (is_checked IN (1,0))
);

CREATE TABLE Amenities (
    amenityId INT NOT NULL,
    amenityName VARCHAR(255) UNIQUE,

    PRIMARY KEY(amenityId)
);

CREATE TABLE Booking (
    bookingId INT NOT NULL AUTO INCREMENT,
    listingId INT,
    renter_userId INT,
    cancelledBy ENUM ('Host', 'Renter'),
    startDate Date NOT NULL,
    endDate Date NOT NULL,
    created_at TIMEtSTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP -- trigger

    PRIMARY KEY (bookingId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId)
        ON DELETE DELETE
        ON UPDATE CASCADE
);

CREATE TABLE Calendar (
    listingId INT,
    availabilityDate DATE,
    price DECIMAL(10, 2),
    isAvailable NUMBER(1),

    PRIMARY KEY (listingId, availabilityDate),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT isAvailable CHECK (is_checked IN (1,0))
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
                                   ON UPDATE CURRENT_TIMESTAMP -- trigger

    PRIMARY KEY (hostUserId, renterUserId),
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT CheckRating CHECK (rating BETWEEN 1 and 5),
);

CREATE TABLE Renter_Review_Host (
    renterUserId INT,
    hostUserId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP -- trigger
    PRIMARY KEY (renterUserId, hostUserId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (hostUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

        ON UPDATE CASCADE,
    CONSTRAINT CheckRating CHECK (rating BETWEEN 1 and 5)
);

CREATE TABLE Renter_Review_Listing (
    renterUserId INT,
    listingId INT,
    comment TEXT,
    rating INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP -- trigger

    PRIMARY KEY (renterUserId, bookingId),
    FOREIGN KEY (renterUserId) REFERENCES BNBUser (userId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (listingId) REFERENCES Booking (listingId)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT CheckRating CHECK (rating BETWEEN 1 and 5)
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

// Trigger for when a booking is deleted, update the calendar availability
CREATE TRIGGER ToDeleteAvailabilityTrigger
    AFTER DELETE ON Booking
    FOR EACH ROW
    BEGIN
        UPDATE Calendar
        SET isAvailable = 1
        WHERE listingId = OLD.listingId AND availabilityDate BETWEEN OLD.startDate AND OLD.endDate;
    END

// Trigger for when a listing is deleted, Cancel all future bookings (By Host)
CREATE TRIGGER ToCancelBookingOnListingDeletion
    AFTER DELETE ON Listing
    FOR EACH ROW
    BEGIN
        UPDATE Booking
        SET cancelledBy = "Host"
        WHERE listingId = OLD.listingId AND availabilityDate > curdate();
    END


// Trigger for when a host cancels the day of a booking, the availability of the listing is set to false for that current day
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

// Trigger for when a booking is cancelled, the availability of the listing is set to true for the days of the cancelled booking
CREATE TRIGGER ToCancelBookingsAfter
    AFTER UPDATE ON Booking
    FOR EACH ROW
    BEGIN
        IF (NEW.startDate > curdate()) THEN
            UPDATE Calendar
            SET isAvailable = 1
            WHERE listingId = OLD.listingId AND availabilityDate BETWEEN OLD.startDate AND OLD.endDate;
        END IF;
    END


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

