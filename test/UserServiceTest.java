import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

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
            System.out.println("===== User Service =====");
            System.out.println("1. Create User");
            System.out.println("2. Delete User");
            System.out.println("3. Update Credit Card");
            System.out.println("4. Check if User is Renter");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

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
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void createUser(Scanner scanner, UserService userService) {
        System.out.println("Enter firstname:");
        String firstname = scanner.nextLine();

        System.out.println("Enter lastname:");
        String lastname = scanner.nextLine();

        System.out.println("Enter date of birth (yyyy-mm-dd):");
        Date dob = Date.valueOf(scanner.nextLine());

        System.out.println("Enter occupation:");
        String occupation = scanner.nextLine();

        System.out.println("Enter SIN:");
        String sin = scanner.nextLine();

        System.out.println("Enter postalcode:");
        String postalcode = scanner.nextLine();

        System.out.println("Enter city:");
        String city = scanner.nextLine();

        System.out.println("Enter country:");
        String country = scanner.nextLine();

        System.out.println("Enter creditcard (N/A if not applicable):");
        String creditcard = scanner.nextLine();
        creditcard = creditcard.equals("N/A") ? null : creditcard;

        userService.createUser(firstname, lastname, dob, occupation, sin, postalcode, city, country, creditcard);
        System.out.println("User created successfully!");
    }

    private static void deleteUser(Scanner scanner, UserService userService) {
        System.out.println("Enter userId:");
        int userId = scanner.nextInt();
        scanner.nextLine();

        boolean result = userService.deleteUser(userId);
        if (result) {
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("User deletion failed.");
        }
    }

    private static void updateCreditCard(Scanner scanner, UserService userService) {
        System.out.println("Enter userId:");
        int userId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new creditcard:");
        String creditcard = scanner.nextLine();

        userService.updateCreditcard(userId, creditcard);
        System.out.println("Credit card updated successfully!");
    }

    private static void isRenter(Scanner scanner, UserService userService) {
        System.out.println("Enter userId:");
        int userId = scanner.nextInt();
        scanner.nextLine();

        userService.isRenter(userId);
    }
}