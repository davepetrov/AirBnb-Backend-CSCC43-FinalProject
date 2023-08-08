package tests;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import service.CalendarService;
import service.Utils;

public class CalendarServiceTest {
    private static Utils utils;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        utils = new Utils();
        CalendarService calendarService;
        try {
            calendarService = new CalendarService();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Calendar Service =====");
            System.out.println("1. Update Listing Availability & Price (Cant update availability of a booked date, need to cancel first if you want to)");
            System.out.println("2. Update Listing Price (Cant update price of a booked date, need to cancel first if you want to)");
            System.out.println("3. Check Listing Availability on a Date");
            System.out.println("4. Check Listing Availability between Dates");
            System.out.println("5. Exit");
            System.out.println("6. > Switch to Review Service");
            System.out.println("7. < Switch to Booking Service");


            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after reading the int.

            switch (choice) {
                case 1:
                    updateListingAvailability(scanner, calendarService);
                    break;
                case 2:
                    updateListingPrice(scanner, calendarService);
                    break;
                case 3:
                    checkListingAvailabilityOnDate(scanner, calendarService);
                    break;
                case 4:
                    checkListingAvailabilityBetweenDates(scanner, calendarService);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                case 6:
                    ReviewServiceTest.main(args);
                    break;
                case 7:
                    BookingServiceTest.main(args);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void updateListingAvailability(Scanner scanner, CalendarService calendarService) {
        int listingId;
        String availabilityChoice;
        boolean makeAvailable;
        String multipleDays;
        Date startDate;
        Date endDate;
        Map<Date, Double> datesPrices;

        System.out.println("\nEnter ID of Listing:");
        listingId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\nDo you want to make the days available or unavailable? (A:= Available / U:= Unavailable):");
        availabilityChoice = scanner.nextLine().trim().toLowerCase();

        makeAvailable = false;
        while(true){
            if (availabilityChoice.equals("a") || availabilityChoice.equals("available")) {
                makeAvailable = true;
                break;
            } else if (availabilityChoice.equals("u") || availabilityChoice.equals("unavailable")) {
                makeAvailable = false;
                break;
            } else {
                System.out.println("Invalid input. Try again");
            }
        }

        System.out.println("\nDo you want to update multiple days or just one? (Y:= Multiple Days / N:= Just one Day):");
        multipleDays = scanner.nextLine().trim().toLowerCase();    

        boolean isFirstTimeHost = calendarService.isFirstTimeHost(listingId);
        if (isFirstTimeHost){
            Double recommendedPrice = calendarService.getRecommendedPrice(listingId);
            if (recommendedPrice != null && recommendedPrice > 0.0){
                System.out.println("\n[=-=--=-=-=Host toolkit=-=--=-=-=]\nWe see that this is your first time hosting this listing on MyBNB!\nMyBNB recommends you a price for your area: $"+ utils.round(recommendedPrice,2));
            }
            else{
            System.out.println("\nWe see that this is your first time hosting this listing on MyBNB!\n Congratulations, you're the first Host in your area! (We don't have enough data to recommend you a price in your area ;-; )) \n"+
                "** Note **: In North America, the average price is $208/night, Europe is $114/night, Asia Pacific is $104/night, Africa is $84/night, Latin America is $81/night (According to www.alltherooms.com, 2021)");
            }
        }

        if (multipleDays.equals("y") || multipleDays.equals("yes")) {
            
            // validation for start and end date
            while (true){
                System.out.println("\nEnter start date (yyyy-mm-dd)(Required):");
                startDate = Date.valueOf(scanner.next());

                System.out.println("\nEnter end date (yyyy-mm-dd)(Required):");
                endDate = Date.valueOf(scanner.next());

                // ensure startDate is before endDate
                if (startDate.compareTo(endDate) > 0) {
                    System.out.println("Start date must be before end date...Try again dummy");
                }
                else{
                    break;
                }
            }

            datesPrices = new HashMap<>();
            for (LocalDate date = startDate.toLocalDate(); !date.isAfter(endDate.toLocalDate()); date = date.plusDays(1)) {
                if (makeAvailable){
                    System.out.println("\nEnter price for date " + date + ":");
                    Double price = scanner.nextDouble();
                    scanner.nextLine();
                    datesPrices.put(Date.valueOf(date), price);         
                }
                else{                
                    datesPrices.put(Date.valueOf(date), null);         
                }       
            }
            if (makeAvailable){
                calendarService.updateListingAvailabilityAndPrice(listingId, datesPrices);
            }
            else{
                calendarService.updateListingMakeUnavailable(listingId, datesPrices);
            }            

        } else {
            System.out.println("\nEnter availabilityDate (yyyy-mm-dd):");
            Date availabilityDate = Date.valueOf(scanner.nextLine());
    
            if (!makeAvailable){
                calendarService.updateListingMakeUnavailable(listingId, availabilityDate);
            }
            else{
                System.out.println("\nEnter price you want for this date:");
                Double price = scanner.nextDouble();
                scanner.nextLine(); // Consume newline left-over            
                calendarService.updateListingAvailabilityAndPrice(listingId, availabilityDate, price);
            }
        }
    }

    private static void updateListingPrice(Scanner scanner, CalendarService calendarService) {
        int listingId;
        Date startDate;
        Date endDate;

        System.out.println("\nEnter ID of Listing:");
        listingId = scanner.nextInt();
        scanner.nextLine();  // Consume newline left-over

        boolean isFirstTimeHost = calendarService.isFirstTimeHost(listingId);
        if (isFirstTimeHost){
            Double recommendedPrice = calendarService.getRecommendedPrice(listingId);
            if (recommendedPrice != null && recommendedPrice > 0.0){
                System.out.println("\nWe see that this is your first time hosting this listing on MyBNB!\n Let MyBNB recommend you a price for your area: "+ utils.round(recommendedPrice,2));
            }
            else{
                System.out.println("\nWe see that this is your first time hosting this listing on MyBNB!\n Congratulations, you're the first Host in your area! (We don't have enough data to recommend you a price in your area ;-; )) \n"+
                "** Note **: In N America, the average price is $208/night, Europe is $114/night, Asia Pacific is $104/night, Africa is $84/night, Latin America is $81/night (According to www.alltherooms.com, 2021)");
            }
        }
        
        boolean validInput = false;
        while (!validInput) {
            System.out.println("\nDo you want to update prices for a continuous date range? (Y/N):");
            String updateRangeAnswer = scanner.nextLine().toLowerCase();
    
            if(updateRangeAnswer.equals("y")) {
                System.out.println("\nEnter start date (yyyy-mm-dd):");
                String startDateString = scanner.nextLine();
    
                System.out.println("\nEnter end date (yyyy-mm-dd):");
                String endDateString = scanner.nextLine();
    
                try {
                    startDate = Date.valueOf(startDateString);
                    endDate = Date.valueOf(endDateString);

                    // validation for start and end date
                    while (true){
                        // ensure startDate is before endDate
                        if (startDate.compareTo(endDate) > 0) {
                            System.out.println("[Error] Start date must be before end date...");
                            return;
                        }
                        else{
                            break;
                        }
                    }
    
                    System.out.println("\nEnter price:");
                    float price = scanner.nextFloat();
                    scanner.nextLine();  // Consume newline left-over
    
                    calendarService.updateListingPrice(listingId, startDate, endDate, price);
                    validInput = true;


                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date format. Please try again.");
                }                
            } else if(updateRangeAnswer.equals("n")) {
                System.out.println("\nEnter date (yyyy-mm-dd):");
                String dateString = scanner.nextLine();
    
                try {
                    Date date = Date.valueOf(dateString);
    
                    System.out.println("\nEnter price:");
                    float price = scanner.nextFloat();
                    scanner.nextLine();  // Consume newline left-over
    
                    calendarService.updateListingPrice(listingId, date, price);
                    validInput = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date format. Please answer 'yes' or 'no'.");
                }
            } else {
                System.out.println("Invalid input. Please answer 'yes' or 'no'.");
            }
        }
    }

    private static void checkListingAvailabilityOnDate(Scanner scanner, CalendarService calendarService) {
        int listingId;
        Date date;

        System.out.println("\nEnter ID of Listing:");
        listingId = scanner.nextInt();

        System.out.println("\nEnter date (yyyy-mm-dd):");
        date = Date.valueOf(scanner.next());

        System.out.print("\nAvailability for Listing "+ listingId+": \n ------------------------\n");
        calendarService.getAvailabilityStatus(listingId, date);
    }

    private static void checkListingAvailabilityBetweenDates(Scanner scanner, CalendarService calendarService) {
        int listingId;
        Date startDate;
        Date endDate;
        Map<Date, String> dateAvailabilityMap;

        System.out.println("\nEnter ID of Listing:");
        listingId = scanner.nextInt();

        
        // validation for start and end date
        while (true){
            System.out.println("\nEnter start date (yyyy-mm-dd)(Required):");
            startDate = Date.valueOf(scanner.next());

            System.out.println("\nEnter end date (yyyy-mm-dd)(Required):");
            endDate = Date.valueOf(scanner.next());

            // ensure startDate is before endDate
            if (startDate.compareTo(endDate) > 0) {
                System.out.println("[Error] Start date must be before end date...");
                return;
            }
            else{
                break;
            }
        }

        dateAvailabilityMap = calendarService.getAvailabilityStatus(listingId, startDate, endDate);
        
        System.out.print("\nAvailability for Listing: "+ listingId+" \n ---------------------------\n");
        for (Map.Entry<Date, String> entry : dateAvailabilityMap.entrySet()) {
            LocalDate localDate = entry.getKey().toLocalDate();
            String dayOfWeek = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            System.out.printf("%-15s (%-9s) : %s\n", entry.getKey(), dayOfWeek, entry.getValue());
        }
    }
}