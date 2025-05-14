package org.example.atharvolunteeringplatform.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    private Integer id;

    // User
    @NotNull(message = "Username cannot be null")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 10 characters")
    private String username;

    @NotEmpty(message = "Please enter the student name")
    @Size(min = 2, max = 50, message = "Student name must be between 2 and 50 characters")
    private String name;

    @NotEmpty(message = "Please enter an email")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Please enter a phone number")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and be 10 digits long")
    private String phone_number;

    @NotEmpty(message = "Please enter a password")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 9 characters and include letters and numbers")
    private String password;

    private LocalDateTime created_at;


    // Student
    @NotEmpty(message = "Please enter the school name")
    @Size(min = 2, max = 100, message = "School name must be between 2 and 100 characters")
    private String school_name;

    @NotNull(message = "Please enter the age")
    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 21, message = "Age must be less than or equal to 21")
    private Integer age;

    @NotEmpty(message = "Please enter the grade level")
    @Pattern(regexp = "^(First Secondary|Second Secondary|Third Secondary)$", message = "Grade level must be one of: First Secondary, Second Secondary, Third Secondary")
    private String grade_level;

    @NotEmpty(message = "Please enter the gender")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender must be either Male or Female")
    private String gender;

    @Pattern(regexp = "^(Pending|Active|Inactive)$", message = "Status must be one of: Pending, Active, Inactive")
    private String status;

    private Integer total_hours = 0;

    private Integer badges_count = 0;
}