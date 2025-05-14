package org.example.atharvolunteeringplatform.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.DTO.StudentDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Repository.StudentOpportunityRequestRepository;
import org.example.atharvolunteeringplatform.Service.StudentOpportunityRequestService;
import org.example.atharvolunteeringplatform.Service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentOpportunityRequestService studentOpportunityRequestService;

   //admin
    @GetMapping("/get-all")
    public ResponseEntity getAllStudents() {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getAllStudents());
    }

    //student
    @PostMapping("/add")
    public ResponseEntity addStudent(@RequestBody @Valid StudentDTO studentDTO) {
        studentService.addStudent(studentDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Student added successfully"));
    }

    //student
    @PutMapping("/update")
    public ResponseEntity updateStudent(@AuthenticationPrincipal MyUser myUser, @RequestBody @Valid StudentDTO studentDTO) {
        studentService.updateStudent(myUser.getId(), studentDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Student updated successfully"));
    }

    //admin
    @DeleteMapping("/delete/{studentID}")
    public ResponseEntity deleteStudent(@PathVariable Integer studentID) {
        studentService.deleteStudent(studentID);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Student deleted successfully"));
    }

    //student
    @GetMapping("/opportunities-by-hours")
    public ResponseEntity getOpportunitiesSortedByHours() {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getOpportunitiesSortedByHours());
    }

    //student
    @GetMapping("/opportunities-by-date/{from}/{to}")
    public ResponseEntity getOpportunitiesByDateRange(@PathVariable LocalDate from, @PathVariable LocalDate to) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getOpportunitiesByDateRange(from, to));
    }

    //student
    @GetMapping("/my-requests")
    public ResponseEntity getMyRequests(@AuthenticationPrincipal MyUser user) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.getMyRequests(user.getId()));
    }



    //student
    @GetMapping("/completed-opportunities")
    public ResponseEntity getCompletedOpportunities(@AuthenticationPrincipal MyUser user) {

        return ResponseEntity.ok(studentOpportunityRequestService.getCompletedOpportunitiesByStudent(user.getId()));
    }


    //student
    @GetMapping("/hours-summary")
    public ResponseEntity<?> getHoursSummary(@AuthenticationPrincipal MyUser user) {
        return ResponseEntity.ok(studentService.getStudentHoursSummary(user.getId()));
    }


    //44
    //for supervisor
    @GetMapping("/students-Inactive")
    public ResponseEntity<List<Student>> getInactiveStudents(@AuthenticationPrincipal MyUser user) {
        List<Student> pendingStudents = studentService.getInactiveStudents(user.getId());
        return ResponseEntity.ok(pendingStudents);
    }
}