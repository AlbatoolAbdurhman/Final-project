package org.example.atharvolunteeringplatform.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentDetailsDTO {


        private Integer id;
        private String name;
        private String schoolName;
        private Integer age;
        private String gradeLevel;
        private String gender;
        private String status;
        private Integer totalHours;
        private Integer badgesCount;
    }

