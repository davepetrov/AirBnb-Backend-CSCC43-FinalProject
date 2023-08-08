##  Database Project: MyBNB README.MD
**Author:** David Petrov
### Purpose
This project was made for a final project for CSCC43 @ University Of Toronto.

The main objective of this project was to create a scalable (backend) mock-application for a popular Rental website, namely AirBnb, which is a popular platform for people to rent out their homes or apartments for short-term stays. This application knock off is named MyBNB. To achieve this objective, I have chosen to develop a Command Line Interface (CLI) for my implementation. This approach has the advantage of saving time on the development of a front-end application, thus allowing me to allocate more time towards designing and implementing the backend. With this in mind, I am able to create a solid and robust back-end application that can easily be integrated with a front-end application in the future.

### Database Schema
![ER Diagram - David](https://documents.lucid.app/documents/1cb6cd87-42e5-4e24-b09f-392ba820e687/pages/0_0?a=2554&x=43&y=-262&w=1862&h=1309&store=1&accept=image%2F*&auth=LCA%20ae69e1fb69b34fb8c224075d1690f8b274fd7e8c2277725a5f62d40817e53c8e-ts%3D1691426011)


## Getting Started

### Prerequisites

- MySQL 8.1.0+ Server
- Maven 3.6.3+
- Java 8+

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/davepetrov/AirBnb-Backend-CSCC43-FinalProject
2. run `Maven Build`
3. Run any of `tests/{User/Listing/Booking/Calendar/Reports/Review/Search/}ServiceTest.java` files to enter CLI menu. (UserServiceTest.java is recommended to begin the flow)
   - Each of these services performs a specific action
     - **User**: Create a user, delete the user, update creditcard (Remove + update + Add), check if user is eligible to be a renter
     - **Listing**: Create a listing, remove amenities, add amenities, delete listing
     - **Booking**: Create a booking, Host cancel booking, Renter cancel booking, Get bookings by listing ID
     - **Search**: (1) Search availabile listings by latitude + longitude and distance, (2) by exact addreess (postalcode, city, country), (3) by filters
     - **Calendar**: Update listing availability, price, checking availability of a listing
     - **Review**: Renter reviews host, Renter reviews listing, Host Reviews Renter (All require a booking in the past week), 
     - **Reports**: 1-6 (Specified in the assignment handout)
4. You can transition between any of the tests to test different functionalities of the application.
5. Ensure for all services, you setup the database with the following credentials:
   
```sh
    username: root
    password: ZQyRhifF&8t`4L*0
    database: mybnb
    port: 3306
    host: 34.130.232.208
```
\
   *(<u>Note:</u> The database host `34.130.232.208` is run using GoogleCloud and for security, only allows access to my personal network. If you choose to run the database, you can PM me your IP and I will create a user for you to access the database.  If you choose to use your own DB host, you can do so by replacing the `host` value with your own)*
1. Run the following files (In order) in the database:
   1. Dropping all existing data: `/src/man/java/reset.dll` 
   2. Creating db schema (tables, procedures, triggers)  `/src/man/java/schema.dll` 
*(<u>Note:</u> You can find the DB Schema here)*
   3.  Insert sample (dummy) data:  `/src/man/java/data.dll`

### Possibilities on Improvement
- Optimize: Make the database more efficient, in terms of complexity (add indexing, query optimization, caching, etc. )
- Replace Command Line Interface (CLI) with Frontend interface
- Implement more security for invalid inputs (Postalcode, Occupations, Country, etc. )
- Replace with in-code DB configuration with `.env` configuration, for security
- Replace JDPC with JPA (For ORM, Reduced boilerplate code, caching, more advanced features, etc)
- Hash the creditcard, SIN information in the database for security
- Use external APIs for searching for nearby listings based off your postalcode
- Add "MyBNB Wallet" feature. Currently, host doesnt need a creditcard attached. All revenue would go into said wallet, and can be withdrawn to a bank account at a later time.

### Contact
David Petrov - dave.petrov@mail.utoronto.ca
Project Link - https://github.com/davepetrov/AirBnb-Backend-CSCC43-FinalProject