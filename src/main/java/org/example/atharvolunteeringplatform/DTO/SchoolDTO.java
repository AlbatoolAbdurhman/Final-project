package org.example.atharvolunteeringplatform.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SchoolDTO {

//User

    @NotNull(message = "Username cannot be null")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 10 characters")
    private String username;


    @Column(nullable = false)
    @NotEmpty(message = "Name cannot be Empty")
    private String name;

    @NotEmpty(message = "Please enter an email")
    @Email(message = "Invalid email format")
    @Column(columnDefinition = "varchar(100) not null unique")
    private String email;


    @NotEmpty(message = "Please enter a phone number")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and be 10 digits long")
    @Column(columnDefinition = "varchar(10) not null unique")
    private String phoneNumber;

    @NotEmpty(message = "Please enter a password")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 9 characters and include letters and numbers")
    @Column(columnDefinition = "varchar(255) not null")
    private String password;

    //school
    @Column(nullable = false)
    @NotEmpty(message = "Region cannot be Empty")
    private String region;

    @Column(nullable = false)
    @NotEmpty(message = "gender cannot be Empty")
    @Pattern(regexp = "Male|female")
    private String gender;

    @Column(nullable = false)
    @NotEmpty(message = "City cannot be Empty")
    private String city;

    @Column(nullable = false)
    @NotEmpty(message = "supervisor Name cannot be Empty")
    private String supervisorName;

    @Pattern(regexp = "^(Active|Inactive|Pending)$")
    private String status;


}
