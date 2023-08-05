package tests;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;
import service.ReportsService;

public class ReportsServiceTest {

    public static void main(String[] args) {
        ReportsService reportsService;
        try {
            reportsService = new ReportsService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Reports Service =====");
            System.out.println("1. Generate Report 1 (Total Bookings in Specific Date Range by City or Postal Code)");
            System.out.println("2. Generate Report 2 (Total Number of Listings)");
            System.out.println("3. Generate Report 3 (Rank Hosts by Number of Listings)");
            System.out.println("4. Generate Report 4 (Identify Commercial Hosts By City)");
            System.out.println("5. Generate Report 5 (Rank Renters by Bookings)");
            System.out.println("6. Generate Report 6 (Find Max Cancellations)");
            System.out.println("7. Exit");
            System.out.println("<. Switch to Review Service");

            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    generateReport1(scanner, reportsService);
                    break;
                case 2:
                    generateReport2(scanner, reportsService);
                    break;
                case 3:
                    generateReport3(scanner, reportsService);
                    break;
                case 4:
                    generateReport4(scanner, reportsService);
                    break;
                case 5:
                    generateReport5(scanner, reportsService);
                    break;
                case 6:
                    generateReport6(scanner, reportsService);
                    break;
                case 7:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 8:
                    ReviewServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    private static void generateReport1(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 1: ");
        String city;
        String postalCode;
        Date startDate;
        Date endDate;

        System.out.println("\nEnter cCity (Required):");
        city = scanner.nextLine();

        System.out.println("\nEnter postal code (or leave empty for any):");
        postalCode = scanner.nextLine();

        System.out.println("\nEnter start date (YYYY-MM-DD)(Required):");
        startDate = Date.valueOf(scanner.nextLine());

        System.out.println("\nEnter end date (YYYY-MM-DD)(Required):");
        endDate = Date.valueOf(scanner.nextLine());
        
        reportsService.TotalBookingsInSpecificDateRangeByCityOrPostalCode(city, postalCode, startDate, endDate);
    }

    private static void generateReport2(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 2: ");
        String country = null;
        String city = null;
        String postalCode = null;

        System.out.println("\nEnter Country (Required):");
        country = scanner.nextLine();

        System.out.println("\nEnter City (or leave empty for any):");
        city = scanner.nextLine();

        System.out.println("\nEnter Postal code (or leave empty for any):");
        postalCode = scanner.nextLine();

        reportsService.TotalNumberOfListings(country, city, postalCode);
    }


    private static void generateReport3(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 3");
        String country;
        String city;

        System.out.println("\nEnter Country (Required):");
        country = scanner.nextLine();

        System.out.println("\nEnter city (or leave empty for any):");
        city = scanner.nextLine();

        reportsService.RankHostsByNumberOfListings(country, city);
    }

    private static void generateReport4(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 4");
        reportsService.IdentifyCommercialHosts();
    }

    private static void generateReport5(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 5");
        Date startDate;
        Date endDate;
        int year;

        System.out.println("\nEnter start date (YYYY-MM-DD)(Required):");
        startDate = Date.valueOf(scanner.nextLine());

        System.out.println("\nEnter end date (YYYY-MM-DD)(Required):");
        endDate = Date.valueOf(scanner.nextLine());

        System.out.println("\nEnter the year (Required):");
        year = scanner.nextInt();
        scanner.nextLine();

        reportsService.RankRentersByBookings(startDate, endDate, year);
    }

    private static void generateReport6(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 6");
        int year;

        System.out.println("\nEnter the year (Required):");
        year = scanner.nextInt();
        scanner.nextLine(); 

        reportsService.FindMaxCancellationsForTheYear(year);
    }

    private static void generateReport7(Scanner scanner, ReportsService reportsService) {
        System.out.println("Generate Report 7");
        reportsService.FindPopularNounPhrasesFromRenterOnListingComments();
    }
}