package model.entity;

import lombok.Data;

@Data
public class User {
    private int userId;
    private String firstname;
    private String surname;
    private String dob;
    private String occupation;
    private String sin;
    private String postalcode;
    private String city;
    private String country;
    private String creditcard;    
}
