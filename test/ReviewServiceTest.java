import java.sql.SQLException;
import java.util.Scanner;

public class ReviewServiceTest {

    public static void main(String[] args) {
        ReviewService reviewService;
        try {
            reviewService = new ReviewService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Review Service =====");
            System.out.println("1. Renter Review Listing");
            System.out.println("2. Host Review Renter");
            System.out.println("3. Renter Review Host");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    renterReviewListing(scanner, reviewService);
                    break;
                case 2:
                    hostReviewRenter(scanner, reviewService);
                    break;
                case 3:
                    renterReviewHost(scanner, reviewService);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void renterReviewListing(Scanner scanner, ReviewService reviewService) {
        System.out.println("Enter renterUserId:");
        int renterUserId = scanner.nextInt();

        System.out.println("Enter listingId:");
        int listingId = scanner.nextInt();

        System.out.println("Enter rating (1 to 5):");
        int rating = scanner.nextInt();

        scanner.nextLine(); // Consume the newline character after reading the int.

        System.out.println("Enter comment:");
        String comment = scanner.nextLine();

        boolean result = reviewService.renterReviewListing(renterUserId, listingId, rating, comment);
        if (result) {
            System.out.println("Listing reviewed successfully!");
        } else {
            System.out.println("Listing review failed.");
        }
    }

    private static void hostReviewRenter(Scanner scanner, ReviewService reviewService) {
        System.out.println("Enter hostUserId:");
        int hostUserId = scanner.nextInt();

        System.out.println("Enter renterUserId:");
        int renterUserId = scanner.nextInt();

        System.out.println("Enter rating (1 to 5):");
        int rating = scanner.nextInt();

        scanner.nextLine(); // Consume the newline character after reading the int.

        System.out.println("Enter comment:");
        String comment = scanner.nextLine();

        boolean result = reviewService.hostReviewRenter(hostUserId, renterUserId, rating, comment);
        if (result) {
            System.out.println("Renter reviewed successfully!");
        } else {
            System.out.println("Renter review failed.");
        }
    }

    private static void renterReviewHost(Scanner scanner, ReviewService reviewService) {
        System.out.println("Enter renterUserId:");
        int renterUserId = scanner.nextInt();

        System.out.println("Enter hostUserId:");
        int hostUserId = scanner.nextInt();

        System.out.println("Enter rating (1 to 5):");
        int rating = scanner.nextInt();

        scanner.nextLine(); // Consume the newline character after reading the int.

        System.out.println("Enter comment:");
        String comment = scanner.nextLine();

        boolean result = reviewService.renterReviewHost(renterUserId, hostUserId, rating, comment);
        if (result) {
            System.out.println("Host reviewed successfully!");
        } else {
            System.out.println("Host review failed.");
        }
    }
}