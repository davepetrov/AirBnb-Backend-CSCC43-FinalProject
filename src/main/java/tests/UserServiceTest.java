package tests;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

import service.UserService;

public class UserServiceTest {

    public static void main(String[] args) {
        UserService userService;
        try {
            userService = new UserService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== User Service =====");
            System.out.println("1. Create User");
            System.out.println("2. Delete User");
            System.out.println("3. Update Credit Card");
            System.out.println("4. Check if User is Renter");
            System.out.println("5. Exit");
            System.out.println("6. > Switch to Listing Service ");

            System.out.print("\nEnter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1:
                    createUser(scanner, userService);
                    break;
                case 2:
                    deleteUser(scanner, userService);
                    break;
                case 3:
                    updateCreditCard(scanner, userService);
                    break;
                case 4:
                    isRenter(scanner, userService);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                case 6:
                    ListingServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createUser(Scanner scanner, UserService userService) {
        String firstname;
        String surname;
        Date dob;
        String occupation;
        String sin;
        String postalCode;
        String city;
        String country;
        String creditcard;

        System.out.println("Enter firstname:");
        firstname = scanner.nextLine();

        System.out.println("Enter lastname:");
        surname = scanner.nextLine();

        System.out.println("Enter date of birth (yyyy-mm-dd):");
        dob = Date.valueOf(scanner.nextLine());

        System.out.println("Enter occupation:");
        occupation = scanner.nextLine();

        System.out.println("Enter SIN:");
        sin = scanner.nextLine();

        System.out.println("Enter postalcode:");
        postalCode = scanner.nextLine();

        System.out.println("Enter City:");
        city = scanner.nextLine();

        System.out.println("Enter Country:");
        country = scanner.nextLine();

        System.out.println("Enter Creditcard # (or leave empty if you dont want to add right now. ** Note **: Must have Creditcard added to rent):");
        creditcard = scanner.nextLine();
        if (creditcard.isEmpty()) {
            creditcard = null;
        }

        userService.createUser(firstname, surname, dob, occupation, sin, postalCode, city, country, creditcard);
    }

    private static void deleteUser(Scanner scanner, UserService userService) {
        int userId;

        System.out.println("Enter userId:");
        userId = scanner.nextInt();
        scanner.nextLine();

        userService.deleteUser(userId);
    }

    private static void updateCreditCard(Scanner scanner, UserService userService) {
        int userId;
        String creditcard;

        System.out.println("Enter userId:");
        userId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Creditcard # (or leave empty if you want to remove):");
        creditcard = scanner.nextLine();
        if (creditcard.isEmpty()) {
            creditcard = null;
        }

        userService.updateCreditcard(userId, creditcard);
    }

    private static void isRenter(Scanner scanner, UserService userService) {
        System.out.println("Enter userId:");
        int userId = scanner.nextInt();
        scanner.nextLine();

        userService.isRenter(userId);
    }
}