package org.example.atharvolunteeringplatform.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Column(nullable = false)
    @NotEmpty(message = "comment cannot be Empty")
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    private School school;

    @ManyToOne
    private Opportunity opportunity;

}
