package org.example.atharvolunteeringplatform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StudentOpportunityRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @Pattern(regexp = "^(pending|approved|rejected)$", message = "Supervisor status must be one of: pending, approved, rejected")
    @Column(columnDefinition = "varchar(20)")
    private String supervisor_status;


    @Pattern(regexp = "^(pending|approved|rejected)$", message = "Organization status must be one of: pending, approved, rejected")
    @Column(columnDefinition = "varchar(20)")
    private String organization_status;


    @Pattern(regexp = "^(pending|rejected|approved|progress|completed|incomplete)$", message = "Status must be one of: pending, rejected, progress,approved, complete, incomplete")
    @Column(columnDefinition = "varchar(20)")
    private String status;


    private LocalDateTime applied_at;


    private LocalDateTime approved_at;


    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @JsonIgnore
    private Student student;

    @ManyToOne
    @JsonIgnore
    private Opportunity opportunity;
}