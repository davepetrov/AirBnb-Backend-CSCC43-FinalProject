package tests;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import model.constant.UserType;
import model.entity.Booking;
import service.BookingService;

public class BookingServiceTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        BookingService bookingService;
        try {
            bookingService = new BookingService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Booking Service =====");
            System.out.println("1. Create Booking");
            System.out.println("2. Host Cancel Booking");
            System.out.println("3. Renter Cancel Booking");
            System.out.println("4. Get Bookings by Listing ID");
            System.out.println("5. Exit");
            System.out.println("6. > Switch to CalendarService");
            System.out.println("7. < Switch to ListingService");


            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    createBooking(scanner, bookingService);
                    break;
                case 2:
                    cancelBooking(scanner, bookingService, UserType.Host);
                    break;
                case 3:
                    cancelBooking(scanner, bookingService, UserType.Renter);
                    break;
                case 4:
                    getBookingsByListingId(scanner, bookingService);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 6:
                    CalendarServiceTest.main(args);
                    break;
                case 7:
                    ListingServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createBooking(Scanner scanner, BookingService bookingService) {
        int listingId;
        int renterId;
        Date startDate;
        Date endDate;

        System.out.println("\nEnter ID of Listing (Required):");
        listingId = scanner.nextInt();

        System.out.println("\nEnter ID of Renter (Required):");
        renterId = scanner.nextInt();

        System.out.println("\nEnter start date (yyyy-mm-dd)(Required):");
        startDate = Date.valueOf(scanner.next());

        System.out.println("\nEnter end date (yyyy-mm-dd)(Required):");
        endDate = Date.valueOf(scanner.next());

        bookingService.createBooking(listingId, renterId, startDate, endDate);
    }

    private static void cancelBooking(Scanner scanner, BookingService bookingService, UserType userType) {
        int bookingId;
        System.out.println("\nEnter bookingId (Required):");
        bookingId = scanner.nextInt();

        if (userType == UserType.Host) {
            bookingService.hostCancelBooking(bookingId);
        } else {
            bookingService.renterCancelBooking(bookingId);
        }

    }

    private static void getBookingsByListingId(Scanner scanner, BookingService bookingService) {
        System.out.println("\nEnter ID of Listing:");
        int listingId = scanner.nextInt();

        List<Booking> bookings = bookingService.getBookingsByListingId(listingId);
        if (bookings != null && !bookings.isEmpty()) {
            System.out.println("\nBookings for Listing ID " + listingId + "\n--------------------------------------------------------------------------");
            for (Booking booking : bookings) {
                System.out.println(booking.toString() +"\n");
                    
            }
        } else {
            System.out.println("No bookings found for Listing ID " + listingId);
        }
    }
}