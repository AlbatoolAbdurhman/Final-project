package org.example.atharvolunteeringplatform.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "Title cannot be Empty")
    @Size(max = 30, message = "Title cannot exceed 30 characters")
    private String title;

    @Column(nullable = false)
    @NotEmpty(message = "description cannot be Empty")
    @Size(max = 300, message = "Description cannot exceed 300 characters")
    private String description;

    @Pattern(regexp = "^(In Progress|Resolved|Closed)$")
    private String status;

    private LocalDateTime createAt;

    @ManyToOne
    private Student students;

    @ManyToOne
    private Opportunity opportunity ;



}
