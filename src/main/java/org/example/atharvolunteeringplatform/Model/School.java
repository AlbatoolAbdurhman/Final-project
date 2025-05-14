package org.example.atharvolunteeringplatform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "Name cannot be Empty")
    private String name;

    @Column(nullable = false)
    @NotEmpty(message = "Region cannot be Empty")
    private String region;

    @Column(nullable = false)
    @NotEmpty(message = "gender cannot be Empty")
    @Pattern(regexp = "^(Male|female)$")
    private String gender;

    @Column(nullable = false)
    @NotEmpty(message = "City cannot be Empty")
    private String city;

    @Column(nullable = false)
    @NotEmpty(message = "supervisor Name cannot be Empty")
    private String supervisorName;

    @Pattern(regexp = "^(Active|Inactive|Pending)$")
    private String status;

    @OneToOne
    @MapsId
    @JsonIgnore
    private MyUser myUser;


    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Student> students;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Review> reviews;

}
