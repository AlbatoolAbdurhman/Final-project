package org.example.atharvolunteeringplatform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Student {

    @Id
    private Integer id;


    @NotEmpty(message = "Please enter the student name")
    @Size(min = 2, max = 50, message = "Student name must be between 2 and 50 characters")
    private String name;

    @NotEmpty(message = "Please enter the school name")
    @Size(min = 2, max = 100, message = "School name must be between 2 and 100 characters")
    @Column(columnDefinition = "varchar(100) not null")
    private String school_name;

    @NotNull(message = "Please enter the age")
    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 21, message = "Age must be less than or equal to 21")
    @Column(nullable = false)
    private Integer age;

    @NotEmpty(message = "Please enter the grade level")
    @Pattern(regexp = "^(First Secondary|Second Secondary|Third Secondary)$", message = "Grade level must be one of: First Secondary, Second Secondary, Third Secondary")
    @Column(columnDefinition = "varchar(30) not null")
    private String grade_level;


    @NotEmpty(message = "Please enter the gender")
    @Pattern(regexp = "^(Male|Female)$", message = "Gender must be either Male or Female")
    @Column(columnDefinition = "varchar(10) not null")
    private String gender;


    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be one of: Active, Inactive")
    @Column(columnDefinition = "varchar(10)")
    private String status;

    private Integer total_hours = 0;


    private Integer badges_count = 0;


    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    @JsonIgnore
    MyUser userStudent;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "student")
    Set<StudentOpportunityRequest> studentOpportunityRequests;


    @ManyToMany
    private Set<Badge> badges;

    @ManyToOne
    private School school;

    //**
    @OneToMany(mappedBy = "students", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Complaint> complaints;


}