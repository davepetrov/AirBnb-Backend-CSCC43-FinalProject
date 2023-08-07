##  Database Project: [Your Project Name]

### Purpose

The main objective of this assignment is to create a scalable (backend) mock-application for a popular Rental website, namely AirBnb, which is a popular platform for people to rent out their homes or apartments for short-term stays. We name our application MyBNB. To achieve this objective, I have chosen to develop a Command Line Interface (CLI) for my implementation. This approach has the advantage of saving time on the development of a front-end application, thus allowing me to allocate more time towards designing and implementing the backend. With this in mind, I am able to create a solid and robust back-end application that can easily be integrated with a front-end application in the future.

### Database Schema
![ER Diagram - David](https://documents.lucid.app/documents/1cb6cd87-42e5-4e24-b09f-392ba820e687/pages/0_0?a=2494&x=43&y=-262&w=1862&h=1309&store=1&accept=image%2F*&auth=LCA%204b4e284eb2318ce8f369172a3e3553494532ceb9d7d5f654c7700d30f99a8a50-ts%3D1691280983)


## Getting Started

### Prerequisites

List any software, tools, or libraries that are required to run your database:

- MySQL Server
- Maven 3.6.3+
- Java 8+

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/davepetrov/AirBnb-Backend-CSCC43-FinalProject
2. run `Maven Build`
3. Run any of `tests/{User/Listing/Booking/Calendar/Reports/Review/Search/}ServiceTest.java` files to enter CLI menu. (UserServiceTest.java is recommended to begin the flow)
4. You can transition between any of the tests to test different functionalities of the application.
5. Ensure for all services, you setup the database with the following credentials:
```sh
    username: root
    password: ZQyRhifF&8t`4L*0
    database: mybnb
    port: 3306
    host: 34.130.232.208
```
1. Run the following files (In order) in the database:
   1. Dropping all existing data: `/src/man/java/reset.dll` 
   2. Creating db schema (tables, procedures, triggers)  `/src/man/java/schema.dll` 
   3.  Insert sample (dummy) data:  `/src/man/java/data.dll`

### Possibility on improvement
- Replace Command Line Interface (CLI) with Frontend interface
- Implement more security for invalid inputs (Postalcode, Occupations, Country, etc. )
- Replace with in-code DB configuration with `.env` configuration, for security
- Replace JDPC with JPA (For ORM, Reduced boilerplate code, caching, more advanced features, etc)


### Contact
David Petrov - dave.petrov@mail.utoronto.ca

Project Link: https://github.com/davepetrov/AirBnb-Backend-CSCC43-FinalProject