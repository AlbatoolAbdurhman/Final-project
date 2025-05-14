package org.example.atharvolunteeringplatform.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Organization {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Organization name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;


    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotEmpty(message = "License number is required")
    @Size(max = 50, message = "License must not exceed 50 characters")
    private String license;

    @NotEmpty(message = "Location is required")
    private String location;



    @Pattern(regexp = "^(Active|Inactive)$", message = "Status must be one of: Active, Inactive")
    @Column(columnDefinition = "varchar(10)")
    private String status;




    @OneToOne
    @MapsId
    @JsonIgnore
    private MyUser user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "organization")
    private Set<Opportunity> opportunities;
}
