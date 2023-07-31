import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class CalendarServiceTest {

    public static void main(String[] args) {
        CalendarService calendarService;
        try {
            calendarService = new CalendarService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Calendar Service =====");
            System.out.println("1. Update Listing Availability");
            System.out.println("2. Update Listing Price");
            System.out.println("3. Check Listing Availability on a Date");
            System.out.println("4. Check Listing Availability between Dates");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    updateListingAvailability(scanner, calendarService);
                    break;
                case 2:
                    updateListingPrice(scanner, calendarService);
                    break;
                case 3:
                    checkListingAvailabilityOnDate(scanner, calendarService);
                    break;
                case 4:
                    checkListingAvailabilityBetweenDates(scanner, calendarService);
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

    private static void updateListingAvailability(Scanner scanner, CalendarService calendarService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter availabilityDate (yyyy-mm-dd):");
        Date availabilityDate = Date.valueOf(scanner.next());

        System.out.println("Is the listing available on this date? (true/false):");
        boolean isAvailable = scanner.nextBoolean();

        calendarService.updateListingAvailability(listingId, availabilityDate, isAvailable);
        System.out.println("Listing availability updated successfully!");
    }

    private static void updateListingPrice(Scanner scanner, CalendarService calendarService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter date (yyyy-mm-dd):");
        Date date = Date.valueOf(scanner.next());

        System.out.println("Enter price:");
        float price = scanner.nextFloat();

        boolean result = calendarService.updateListingPrice(listingId, date, price);
        if (result) {
            System.out.println("Listing price updated successfully!");
        } else {
            System.out.println("Listing price update failed.");
        }
    }

    private static void checkListingAvailabilityOnDate(Scanner scanner, CalendarService calendarService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter date (yyyy-mm-dd):");
        Date date = Date.valueOf(scanner.next());

        boolean isAvailable = calendarService.isListingAvailable(listingId, date);
        if (isAvailable) {
            System.out.println("Listing is available on this date.");
        } else {
            System.out.println("Listing is not available on this date.");
        }
    }

    private static void checkListingAvailabilityBetweenDates(Scanner scanner, CalendarService calendarService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter start date (yyyy-mm-dd):");
        Date startDate = Date.valueOf(scanner.next());

        System.out.println("Enter end date (yyyy-mm-dd):");
        Date endDate = Date.valueOf(scanner.next());

        boolean isAvailable = calendarService.isListingAvailable(listingId, startDate, endDate);
        if (isAvailable) {
            System.out.println("Listing is available between the specified dates.");
        } else {
            System.out.println("Listing is not available between the specified dates.");
        }
    }
}