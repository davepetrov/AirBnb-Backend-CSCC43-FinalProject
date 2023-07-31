import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchServiceTest {

    public static void main(String[] args) {
        SearchService searchService;
        try {
            searchService = new SearchService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Search Service =====");
            System.out.println("1. Find Listings by Latitude and Longitude with Distance (Sort by Price)");
            System.out.println("2. Find Listings by Latitude and Longitude with Distance (Sort by Distance)");
            System.out.println("3. Find Listings by Exact Address (Sort by Price)");
            System.out.println("4. Search Listings by Filters (Sort by Price)");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    findListingsByLatitudeLongitudeWithDistanceSortByPrice(scanner, searchService);
                    break;
                case 2:
                    findListingsByLatitudeLongitudeWithDistanceSortByDistance(scanner, searchService);
                    break;
                case 3:
                    findListingSearchByExactAddressSortByPrice(scanner, searchService);
                    break;
                case 4:
                    searchListingsByFiltersSortByPrice(scanner, searchService);
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

    private static void findListingsByLatitudeLongitudeWithDistanceSortByPrice(Scanner scanner, SearchService searchService) {
        System.out.println("Enter start date (yyyy-MM-dd):");
        Date startDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter end date (yyyy-MM-dd):");
        Date endDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter latitude:");
        double latitude = scanner.nextDouble();

        System.out.println("Enter longitude:");
        double longitude = scanner.nextDouble();

        System.out.println("Enter radius in kilometers (Default: 10):");
        int radiusKm = scanner.nextInt();

        System.out.println("Sort by price (true/false):");
        boolean isAscending = scanner.nextBoolean();

        List<ListingSearch> results = searchService.findListingsByLatitudeLongitudeWithDistanceSortByPrice(startDate, endDate, latitude, longitude, radiusKm, isAscending);
        displayListingSearchResults(results);
    }

    private static void findListingsByLatitudeLongitudeWithDistanceSortByDistance(Scanner scanner, SearchService searchService) {
        System.out.println("Enter start date (yyyy-MM-dd):");
        Date startDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter end date (yyyy-MM-dd):");
        Date endDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter latitude:");
        double latitude = scanner.nextDouble();

        System.out.println("Enter longitude:");
        double longitude = scanner.nextDouble();

        System.out.println("Enter radius in kilometers (Default: 10):");
        int radiusKm = scanner.nextInt();

        List<ListingSearch> results = searchService.findListingsByLatitudeLongitudeWithDistanceSortByDistance(startDate, endDate, latitude, longitude, radiusKm);
        displayListingSearchResults(results);
    }

    private static void findListingSearchByExactAddressSortByPrice(Scanner scanner, SearchService searchService) {
        System.out.println("Enter start date (yyyy-MM-dd):");
        Date startDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter end date (yyyy-MM-dd):");
        Date endDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter postal code:");
        String postalCode = scanner.nextLine();

        System.out.println("Enter city:");
        String city = scanner.nextLine();

        System.out.println("Enter country:");
        String country = scanner.nextLine();

        System.out.println("Sort by price (true/false):");
        boolean isAscending = scanner.nextBoolean();

        List<ListingSearch> results = searchService.findListingSearchByExactAddressSortByPrice(startDate, endDate, postalCode, city, country, isAscending);
        displayListingSearchResults(results);
    }

    private static void searchListingsByFiltersSortByPrice(Scanner scanner, SearchService searchService) {
        System.out.println("Enter start date (yyyy-MM-dd):");
        Date startDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter end date (yyyy-MM-dd):");
        Date endDate = Date.valueOf(scanner.nextLine());

        System.out.println("Enter postal code (or leave empty for any):");
        String postalCode = scanner.nextLine();

        System.out.println("Enter amenities separated by commas (e.g., wifi,pool):");
        String amenitiesInput = scanner.nextLine();
        List<String> amenities = new ArrayList<>();
        if (!amenitiesInput.isEmpty()) {
            amenities = List.of(amenitiesInput.split(","));
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

        System.out.println("Sort by price (true/false):");
        boolean isAscending = scanner.nextBoolean();

        List<ListingSearch> results = searchService.searchListingsByFiltersSortByPrice(postalCode, amenities, startDate, endDate, minPrice, maxPrice, isAscending);
        displayListingSearchResults(results);
    }

    private static void displayListingSearchResults(List<ListingSearch> results) {
        if (results != null && !results.isEmpty()) {
            for (ListingSearch listing : results) {
                System.out.println(listing);
            }
        } else {
            System.out.println("No listings found.");
        }
    }
}