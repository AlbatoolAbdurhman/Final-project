package org.example.atharvolunteeringplatform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
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

public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotEmpty(message = "Description is required")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

//    @NotEmpty(message = "Criteria is required")
    private Integer criteria;

    @NotEmpty(message = "Image path is required")
    private String imagePath;




    @ManyToMany(mappedBy = "badges")
    @JsonIgnore
    private Set<Student> students;

}
