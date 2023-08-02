package tests;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import model.constant.ListingType;
import model.entity.Listing;
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
            System.out.println("2. Update Listing Active Status");
            System.out.println("3. Delete Listing");
            System.out.println("4. Get All Active Listings");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    createListing(scanner, listingService);
                    break;
                case 2:
                    updateListingActiveStatus(scanner, listingService);
                    break;
                case 3:
                    deleteListing(scanner, listingService);
                    break;
                case 4:
                    getAllActiveListings(listingService);
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
        System.out.println("Listing created successfully!");
    }

    private static void updateListingActiveStatus(Scanner scanner, ListingService listingService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter new status (T/F):");
        boolean isActive;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("t") || input.equals("true")) {
                isActive = true;
                break;
            } else if (input.equals("f") || input.equals("false")) {
                isActive = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'T' or 'F'.");
            }
        }

        listingService.updateListingActiveStatus(listingId, isActive);
    }

    private static void deleteListing(Scanner scanner, ListingService listingService) {
        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        listingService.deleteListing(listingId);
    }

    private static void getAllActiveListings(ListingService listingService) {
        List<Listing> activeListings = listingService.getAllActiveListings();
        if (activeListings != null && !activeListings.isEmpty()) {
            System.out.println("\nFollowing are active listings:\n------------------------------");
            
            int i=1;
            for (Listing listing : activeListings) {
                System.out.println(i+". "+listing);
                i++;
            }
        } else {
            System.out.println("No active listings found.\n");
        }
    }
}