package tests;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import model.constant.UserType;
import model.entity.Booking;
import service.BookingService;

public class BookingServiceTest {

    public static void main(String[] args) {
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
            System.out.print("Enter your choice: ");

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
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createBooking(Scanner scanner, BookingService bookingService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter renterId:");
        int renterId = scanner.nextInt();

        System.out.println("Enter start date (yyyy-mm-dd):");
        Date startDate = Date.valueOf(scanner.next());

        System.out.println("Enter end date (yyyy-mm-dd):");
        Date endDate = Date.valueOf(scanner.next());

        bookingService.createBooking(listingId, renterId, startDate, endDate);
    }

    private static void cancelBooking(Scanner scanner, BookingService bookingService, UserType userType) {
        System.out.println("Enter bookingId:");
        int bookingId = scanner.nextInt();

        boolean result;
        if (userType == UserType.Host) {
            result = bookingService.hostCancelBooking(bookingId);
        } else {
            result = bookingService.renterCancelBooking(bookingId);
        }

        if (result) {
            System.out.println("Booking canceled successfully!");
        } else {
            System.out.println("Booking cancellation failed.");
        }
    }

    private static void getBookingsByListingId(Scanner scanner, BookingService bookingService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        List<Booking> bookings = bookingService.getBookingsByListingId(listingId);
        if (bookings != null && !bookings.isEmpty()) {
            System.out.println("\nBookings for Listing ID " + listingId + "\n--------------------------------------------------------------------------");
            for (Booking booking : bookings) {
                if (booking.getCancelledBy() != null) {
                    System.out.println("Booking ID: " + booking.getBookingId() +
                            ", Renter ID: " + booking.getRenter_userId() +
                            ", startDate: " + booking.getStartDate().toString() +
                            ", endDate: " + booking.getEndDate().toString()+
                            ", Cancelled By: " + booking.getCancelledBy());
                } else {
                     System.out.println("Booking ID: " + booking.getBookingId() +
                            ", Renter ID: " + booking.getRenter_userId() +
                            ", startDate: " + booking.getStartDate().toString() +
                            ", endDate: " + booking.getEndDate().toString());
                }
                    
            }
        } else {
            System.out.println("No bookings found for Listing ID " + listingId);
        }
    }
}