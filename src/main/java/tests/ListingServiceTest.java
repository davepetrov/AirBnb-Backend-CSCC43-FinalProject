package tests;

import java.sql.SQLException;
import java.util.Scanner;

import model.constant.ListingType;
import service.ListingService;

public class ListingServiceTest {

    public static void main(String[] args) {
        ListingService listingService;
        try {
            listingService = new ListingService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Listing Service =====");
            System.out.println("1. Create Listing");
            System.out.println("2. Delete Listing");
            System.out.println("3. Exit");
            System.out.println("4. > Switch to Booking Service");
            System.out.println("5. < Switch to User Service");

            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    createListing(scanner, listingService);
                    break;
                case 2:
                    deleteListing(scanner, listingService);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 4:
                    BookingServiceTest.main(args);
                    break;
                case 5:
                    UserServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    private static void createListing(Scanner scanner, ListingService listingService) {
        System.out.println("Enter hostUserId:");
        int hostUserId = scanner.nextInt();
        scanner.nextLine();

        String typeString = null; 
        while (true){
            System.out.println("Enter listingType (House, Apartment, Guesthouse, Hotel):");
            typeString = scanner.nextLine();
            if (typeString.equals("House") || typeString.equals("Apartment") || typeString.equals("Guesthouse") || typeString.equals("Hotel")){
                break;
            }
            System.out.println("Invalid ListingType. Please try again.\n");
        }
        ListingType type = ListingType.valueOf(typeString);

        System.out.println("Enter locationLat:");
        float locationLat = scanner.nextFloat();

        System.out.println("Enter locationLong:");
        float locationLong = scanner.nextFloat();
        scanner.nextLine();

        System.out.println("Enter postalCode:");
        String postalCode = scanner.nextLine();

        System.out.println("Enter city:");
        String city = scanner.nextLine();

        System.out.println("Enter country:");
        String country = scanner.nextLine();

        listingService.createListing(hostUserId, type, locationLat, locationLong, postalCode, city, country);
    }

    private static void deleteListing(Scanner scanner, ListingService listingService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        listingService.deleteListing(listingId);
    }
}