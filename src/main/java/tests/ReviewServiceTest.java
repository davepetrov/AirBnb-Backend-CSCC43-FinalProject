package tests;
import java.sql.SQLException;
import java.util.Scanner;

import service.ReviewService;

public class ReviewServiceTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ReviewService reviewService;
        try {
            reviewService = new ReviewService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Review Service =====");
            System.out.println("1. Renter Review Listing");
            System.out.println("2. Host Review Renter");
            System.out.println("3. Renter Review Host");
            System.out.println("4. Exit");
            System.out.println("5. > Switch to Search Service");
            System.out.println("6. < Switch to Calendar Service");


            System.out.print("\nEnter your choice: ");

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
                    break;
            case 5:
                    SearchServiceTest.main(args);
                    break;
                case 6:
                    CalendarServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void renterReviewListing(Scanner scanner, ReviewService reviewService) {
        System.out.println("\nEnter ID of Renter:");
        int renterUserId = scanner.nextInt();

        System.out.println("\nEnter ID of Listing:");
        int listingId = scanner.nextInt();

        System.out.println("\nEnter Rating (1 to 5):");
        int rating = scanner.nextInt();

        scanner.nextLine();

        System.out.println("\nEnter Comment (Could be empty):");
        String comment = scanner.nextLine();
        if (comment.isEmpty()){
            comment = null;
        }

        reviewService.renterReviewListing(renterUserId, listingId, rating, comment);
    }

    private static void hostReviewRenter(Scanner scanner, ReviewService reviewService) {
        int hostUserId;
        int renterUserId;
        int rating;
        String comment;

        System.out.println("\nEnter ID of Host:");
        hostUserId = scanner.nextInt();

        System.out.println("\nEnter ID of Renter:");
        renterUserId = scanner.nextInt();

        System.out.println("\nEnter Rating (1 to 5):");
        rating = scanner.nextInt();

        scanner.nextLine(); 

        System.out.println("\nEnter Comment (Could be empty):");
        comment = scanner.nextLine();
        if (comment.isEmpty()){
            comment = null;
        }

        reviewService.hostReviewRenter(hostUserId, renterUserId, rating, comment);
    }

    private static void renterReviewHost(Scanner scanner, ReviewService reviewService) {
        int renterUserId;
        int hostUserId;
        int rating;
        String comment;

        System.out.println("\nEnter ID of Renter:");
        renterUserId = scanner.nextInt();

        System.out.println("\nEnter ID of Host:");
        hostUserId = scanner.nextInt();

        System.out.println("\nEnter Rating (1 to 5):");
        rating = scanner.nextInt();

        scanner.nextLine();

        System.out.println("\nEnter Comment (Could be empty):");
        comment = scanner.nextLine();
        if (comment.isEmpty()){
            comment = null;
        }

        reviewService.renterReviewHost(renterUserId, hostUserId, rating, comment);
    }
}