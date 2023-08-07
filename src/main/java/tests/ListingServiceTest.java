package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.constant.ListingType;
import model.entity.Amenity;
import service.ListingService;
import service.Utils;

public class ListingServiceTest {
    private static Utils utils;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        utils = new Utils();
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
            System.out.println("2. Remove Amenities");
            System.out.println("3. Add Amenities");
            System.out.println("4. Delete Listing");
            System.out.println("5. Exit");
            System.out.println("6. > Switch to Booking Service");
            System.out.println("7. < Switch to User Service");

            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    createListing(scanner, listingService);
                    break;
                case 2:
                    removeAmenities(scanner, listingService);
                    break;
                case 3:
                    addAmenities(scanner, listingService);
                    break;
                case 4:
                    deleteListing(scanner, listingService);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 6:
                    BookingServiceTest.main(args);
                    break;
                case 7:
                    UserServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
    }

    private static void createListing(Scanner scanner, ListingService listingService) {
        int hostUserId;
        ListingType type;
        float locationLat;
        float locationLong;
        String postalCode;
        String city;
        String country;
        List<String> amenities = new ArrayList<>();

        System.out.println("\nEnter ID of Host:");
        hostUserId = scanner.nextInt();
        scanner.nextLine();

        String typeString = null; 
        while (true){
            System.out.println("\nEnter listingType (House, Apartment, Guesthouse, Hotel):");
            typeString = scanner.nextLine();
            if (typeString.equals("House") || typeString.equals("Apartment") || typeString.equals("Guesthouse") || typeString.equals("Hotel")){
                break;
            }
            System.out.println("Invalid ListingType. Please try again.\n");
        }
        type = ListingType.valueOf(typeString);

        System.out.println("\nAll possible Amenities\n--------------------------\n" + utils.getAllAmenities());
        while (true) {
            System.out.println("\nEnter amenities your listing offers, separated by commas and first letter capitalized\n(e.g. Wifi,Smoke alarm,Carbon monoxide alarm) (or leave empty for any):");

            String amenitiesInput = scanner.nextLine();
            String[] amenitiesArray = amenitiesInput.split(",");
        
            boolean allValid = true;
            if (!amenitiesInput.isEmpty()) {
                for (String a : amenitiesArray) {
                    if (!utils.isValidAmenity(a)) {
                        System.out.println("Invalid amenity: " + a);
                        allValid = false;
                        break;
                    }
                    amenities.add(a);
                }
        
                if (allValid) {
                    break;
                }
            }
            else{
                break;
            }
        }

        System.out.println("\nEnter Latitude:");
        locationLat = scanner.nextFloat();

        System.out.println("\nEnter Longitude:");
        locationLong = scanner.nextFloat();
        scanner.nextLine();

        System.out.println("\nEnter Postal Code:");
        postalCode = scanner.nextLine();

        System.out.println("\nEnter City:");
        city = scanner.nextLine();

        System.out.println("\nEnter Country:");
        country = scanner.nextLine();

        listingService.createListing(hostUserId, type, locationLat, locationLong, postalCode, city, country, amenities);
    }

    private static void removeAmenities(Scanner scanner, ListingService listingService) {
        System.out.println("\nEnter ID of Listing:");
        int listingId = scanner.nextInt();

        System.out.println("\nYour listing currently has The following Amenities\n---------------------------------------------");
        listingService.getAmenities(listingId);

        System.out.println("\nAll possible Amenities\n---------------------------------------------\n" + utils.getAllAmenities());

        scanner.nextLine();
        while (true) {
            System.out.println("\nEnter amenities you want to REMOVE, separated by commas and first letter capitalized\n(e.g. Wifi,Smoke alarm,Carbon monoxide alarm) (or leave empty for none):");

            String amenitiesInput = scanner.nextLine();
            String[] amenitiesArray = amenitiesInput.split(",");
        
            boolean allValid = true;
            if (!amenitiesInput.isEmpty()) {
                for (String a : amenitiesArray) {
                    if (!utils.isValidAmenity(a)) {
                        System.out.println("Invalid amenity: " + a);
                        allValid = false;
                        break;
                    }
                }
        
                if (allValid) {
                    List<String> amenities = new ArrayList<>();
                    for (String a : amenitiesArray) {
                        amenities.add(a);
                    }
                    listingService.removeAmenities(listingId, amenities);
                    break;
                }
            }
            else{
                break;
            }
        }

    }

    private static void addAmenities(Scanner scanner, ListingService listingService) {
        System.out.println("\nEnter ID of Listing:");
        int listingId = scanner.nextInt();

        System.out.println("\nYour listing currently has The following Amenities\n---------------------------------------------");
        listingService.getAmenities(listingId);

        System.out.println("\nAll possible Amenities\n---------------------------------------------\n" + utils.getAllAmenities());

        System.out.println("\n[Host toolkit]Here are some recommended Amenities\n---------------------------------------------");
        listingService.getRecommendedAmenities(listingId);

        scanner.nextLine();
        while (true) {
            System.out.println("\nEnter amenities you want to ADD, separated by commas and first letter capitalized\n(e.g. Wifi,Smoke alarm,Carbon monoxide alarm) (or leave empty for none):");

            String amenitiesInput = scanner.nextLine();
            String[] amenitiesArray = amenitiesInput.split(",");
        
            boolean allValid = true;
            if (!amenitiesInput.isEmpty()) {
                for (String a : amenitiesArray) {
                    if (!utils.isValidAmenity(a)) {
                        System.out.println("Invalid amenity: " + a);
                        allValid = false;
                        break;
                    }
                }
        
                if (allValid) {
                    List<String> amenities = new ArrayList<>();
                    for (String a : amenitiesArray) {
                        amenities.add(a);
                    }
                    listingService.addAmenities(listingId, amenities);
                    break;
                }
            }
            else{
                break;
            }
        }
    }

    private static void deleteListing(Scanner scanner, ListingService listingService) {
        System.out.println("\nEnter ID of Listing:");
        int listingId = scanner.nextInt();

        listingService.deleteListing(listingId);
    }
}