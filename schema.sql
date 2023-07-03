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
    isActive BOOLEAN,
    locationLat FLOAT,
    locationLong FLOAT,
    postalCode VARCHAR(10),
    city VARCHAR(255),
    country VARCHAR(255),
    PRIMARY KEY (listingId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId)
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
    PRIMARY KEY (bookingId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId),
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId)
);

CREATE TABLE Calendar (
    listingId INT,
    availabilityDate DATE,
    price DECIMAL(10, 2),
    isAvailable BOOLEAN,
    PRIMARY KEY (listingId, date),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId)
);

CREATE TABLE Review (
    reviewId INT  NOT NULL AUTO INCREMENT,
    comment TEXT,
    rating INT,
    PRIMARY KEY (reviewId)
    CONSTRAINT CheckRating CHECK (rating BETWEEN 1 and 5))
);


-------------------------RELATIONS-------------------------

CREATE TABLE Listing_Offers_Amenities (
    listingId INT,
    amenityId INT,
    PRIMARY KEY (listingId, amenityId),
    FOREIGN KEY (listingId) REFERENCES Listing (listingId),
    FOREIGN KEY (amenityId) REFERENCES Amenities (amenityId)
);

CREATE TABLE Host_Cancels_Booking (
    bookingId INT,
    Timestamp TIMESTAMP,
    Reason TEXT,
    PRIMARY KEY (bookingId),
    FOREIGN KEY (bookingId) REFERENCES Booking (bookingId)
);

CREATE TABLE Renter_Cancels_Booking (
    bookingId INT,
    Timestamp TIMESTAMP,
    Reason TEXT,
    PRIMARY KEY (bookingId),
    FOREIGN KEY (bookingId) REFERENCES Booking (bookingId)
);

CREATE TABLE Host_Review_Renter (
    host_userId INT,
    renter_userId INT,
    reviewId INT,
    Timestamp TIMESTAMP,
    PRIMARY KEY (host_userId, renter_userId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId),
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId),
    FOREIGN KEY (reviewId) REFERENCES Review (reviewId)
);

CREATE TABLE Renter_Review_Host (
    renter_userId INT,
    host_userId INT,
    reviewId INT,
    Timestamp TIMESTAMP,
    PRIMARY KEY (renter_userId, host_userId),
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId),
    FOREIGN KEY (host_userId) REFERENCES BNBUser (userId),
    FOREIGN KEY (reviewId) REFERENCES Review (reviewId)
);

CREATE TABLE Renter_Review_Listing (
    renter_userId INT,
    listingId INT,
    reviewId INT,
    Timestamp TIMESTAMP,
    PRIMARY KEY (renter_userId, bookingId),
    FOREIGN KEY (renter_userId) REFERENCES BNBUser (userId),
    FOREIGN KEY (listingId) REFERENCES Booking (listingId),
    FOREIGN KEY (reviewId) REFERENCES Review (reviewId)
);

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