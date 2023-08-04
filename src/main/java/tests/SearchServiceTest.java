package tests;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import model.Dto.ListingSearch;
import model.constant.SortBy;
import service.SearchService;
import service.Utils;

public class SearchServiceTest {
    private static Utils utils;

    public static void main(String[] args) {
        utils = new Utils();

        SearchService searchService;
        try {
            searchService = new SearchService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Search Service =====");
            System.out.println("1. Search Available Listings by [Latitude and Longitude with Distance]");
            System.out.println("2. Search Available Listings by [Exact Address (postal code, city, country)]");
            System.out.println("3. Search Available Listings by [Filters]");
            System.out.println("4. Exit");
            System.out.println("5. > Switch to Reports Service");
            System.out.println("6. < Switch to Search Service");

            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    findListingsByLatitudeLongitudeWithDistance(scanner, searchService);
                    break;
                case 2:
                    findListingSearchByExactAddressSortByPrice(scanner, searchService);
                    break;
                case 3:
                    searchListingsByFilters(scanner, searchService);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 5:
                    //TODO: ReportsServiceTest.main(args);
                    break;
                case 6:
                    SearchServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void findListingsByLatitudeLongitudeWithDistance(Scanner scanner, SearchService searchService) {

        System.out.println("Include start and end dates? (Y/N):");
        Date startDate = null;
        Date endDate = null;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("y") || input.equals("yes")) {

                System.out.println("Enter start date (yyyy-MM-dd):");
                startDate = Date.valueOf(scanner.nextLine());

                System.out.println("Enter end date (yyyy-MM-dd):");
                endDate = Date.valueOf(scanner.nextLine());

                break;
            } else if (input.equals("n") || input.equals("no")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Y' or 'N'.");
            }
        }

        System.out.println("Enter latitude:");
        double latitude = scanner.nextDouble();

        System.out.println("Enter longitude:");
        double longitude = scanner.nextDouble();

        System.out.println("Enter radius in kilometers (Default: 10):");
        int radiusKm = scanner.nextInt();

        System.out.println("Sort by price or distance? (P/D)");
        SortBy sortBy;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("p") || input.equals("price")) {
                sortBy = SortBy.PRICE;
                break;
            } else if (input.equals("d") || input.equals("distance")) {
                sortBy = SortBy.DISTANCE;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'P' or 'D'.");
            }
        }

        System.out.println("Sort in what order Ascending or Descending? (asc/dsc):");
        boolean isAscending;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("asc") || input.equals("ascending")) {
                isAscending = true;
                break;
            } else if (input.equals("dsc") || input.equals("descending")) {
                isAscending = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'asc' or 'dsc'.");
            }
        }

        List<ListingSearch> results = searchService.findAvailableListingsByLatitudeLongitude(startDate, endDate, latitude, longitude, radiusKm, isAscending, sortBy);
        displayListingSearchResults(results);
    }


    private static void findListingSearchByExactAddressSortByPrice(Scanner scanner, SearchService searchService) {
        System.out.println("Include start and end dates? (Y/N):");
        Date startDate = null;
        Date endDate = null;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("y") || input.equals("yes")) {

                System.out.println("Enter start date (yyyy-MM-dd):");
                startDate = Date.valueOf(scanner.nextLine());

                System.out.println("Enter end date (yyyy-MM-dd):");
                endDate = Date.valueOf(scanner.nextLine());

                break;
            } else if (input.equals("n") || input.equals("no")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Y' or 'N'.");
            }
        }
        System.out.println("Enter postal code:");
        String postalCode = scanner.nextLine();

        System.out.println("Enter city:");
        String city = scanner.nextLine();

        System.out.println("Enter country:");
        String country = scanner.nextLine();

        List<ListingSearch> results = searchService.findAvailableListingSearchByExactAddress(startDate, endDate, postalCode, city, country);
        displayListingSearchResults(results);
    }

    private static void searchListingsByFilters(Scanner scanner, SearchService searchService) {
        System.out.println("Include start and end dates? (Y/N):");
        Date startDate = null;
        Date endDate = null;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("y") || input.equals("yes")) {

                System.out.println("Enter start date (yyyy-MM-dd):");
                startDate = Date.valueOf(scanner.nextLine());

                System.out.println("Enter end date (yyyy-MM-dd):");
                endDate = Date.valueOf(scanner.nextLine());

                break;
            } else if (input.equals("n") || input.equals("no")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Y' or 'N'.");
            }
        }

        System.out.println("Enter postal code (or leave empty for any):");
        String postalCode = scanner.nextLine();
        List<String> amenities = new ArrayList<>();
        System.out.println("\nAll possible Amenities\n--------------------------\n" + utils.getAllAmenities());
        while (true) {
            System.out.println("\nEnter amenities separated by commas and first letter capitalized\n(e.g., Wifi,Smoke alarm,Carbon monoxide alarm) (or leave empty for any):");

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

        System.out.println("Enter minimum price (or leave empty for any):");
        String minPriceStr = scanner.nextLine();
        Double minPrice = null;
        if (!minPriceStr.isEmpty()) {
            minPrice = Double.parseDouble(minPriceStr);
        }

        System.out.println("Enter maximum price (or leave empty for any):");
        String maxPriceStr = scanner.nextLine();
        Double maxPrice = null;
        if (!maxPriceStr.isEmpty()) {
            maxPrice = Double.parseDouble(maxPriceStr);
        }

        System.out.println("Sort by price or distance? (P/D)");
        SortBy sortBy;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("p") || input.equals("price")) {
                sortBy = SortBy.PRICE;
                break;
            } else if (input.equals("d") || input.equals("distance")) {
                sortBy = SortBy.DISTANCE;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'P' or 'D'.");
            }
        }

        System.out.println("Sort in what order Ascending or Descending? (asc/dsc):");
        boolean isAscending;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase(); // Read user input and normalize it

            if (input.equals("asc") || input.equals("ascending")) {
                isAscending = true;
                break;
            } else if (input.equals("dsc") || input.equals("descending")) {
                isAscending = false;
                break;
            } else {
                System.out.println("Invalid input. Please enter 'asc' or 'dsc'.");
            }
        }


        List<ListingSearch> results = searchService.searchAvailableListingsByFilters(postalCode, amenities, startDate, endDate, minPrice, maxPrice, isAscending, sortBy);
        displayListingSearchResults(results);
    }

    private static void displayListingSearchResults(List<ListingSearch> results) {
        if (results != null && !results.isEmpty()) {
            System.out.println("\nFollowing are available listings with your Search:\n-------------------------------------------------------");
            for (ListingSearch listing : results) {
                System.out.println(listing);
            }
        } else {
            System.out.println("\nNo listings found.");
        }
    }
}