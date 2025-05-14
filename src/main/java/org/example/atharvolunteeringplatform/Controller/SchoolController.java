package org.example.atharvolunteeringplatform.Controller;

//import com.example.final_project.DTO.SchoolDTO;
//import com.example.final_project.Model.School;
//import com.example.final_project.Service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.DTO.SchoolDTO;
import org.example.atharvolunteeringplatform.DTO.StudentDetailsDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.School;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Service.OrganizationService;
import org.example.atharvolunteeringplatform.Service.SchoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/school")
@RequiredArgsConstructor
public class SchoolController {


    private final SchoolService schoolService;

    //admin
    @GetMapping("/all")
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> schools = schoolService.getAllSchool();
        return ResponseEntity.ok(schools);
    }

    //permitAll
    @PostMapping("/add")
    public ResponseEntity<String> addSchool(@RequestBody @Valid SchoolDTO schoolDTO) {
        schoolService.addSchool(schoolDTO);
        return ResponseEntity.ok("School added successfully");
    }

    //supervisor
    @PutMapping("/update")
    public ResponseEntity<String> updateSchool(@AuthenticationPrincipal MyUser myUser, @RequestBody @Valid SchoolDTO schoolDTO) {
        schoolService.updateSchool(myUser.getId(), schoolDTO);
        return ResponseEntity.ok("School updated successfully");
    }

    //admin
    @DeleteMapping("/delete/{schoolId}")
    public ResponseEntity<String> deleteSchool(@PathVariable Integer schoolId) {
        schoolService.deleteSchool(schoolId);
        return ResponseEntity.ok("School deleted successfully");
    }

    //38
    //supervisor
    @GetMapping("/volunteers/{grade}")
    public ResponseEntity<?> getVolunteeringStudents(@PathVariable String grade, @AuthenticationPrincipal MyUser user) {
        List<Student> students = schoolService.getVolunteeringStudentsByGrade(grade, user.getId());
        return ResponseEntity.ok(students);
    }

    //supervisor
    @GetMapping("/students/non-volunteers/{gradeLevel}")
    public ResponseEntity<?> getNonVolunteers(@PathVariable String gradeLevel, @AuthenticationPrincipal MyUser myUser) {
        List<Student> students = schoolService.getNonVolunteersByGradeForSchool(gradeLevel, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(students);
    }

    //supervisor
    @GetMapping("/student-opportunities/{studentId}")
    public ResponseEntity<?> getAllRequestsForStudent(@PathVariable Integer studentId,@AuthenticationPrincipal MyUser myUser) {
        List<StudentOpportunityRequest> requests = schoolService.getAllRequestsForStudent(studentId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }


    //supervisor
//    @PutMapping("/opportunity-request/{requestId}/status/{status}")
//    public ResponseEntity<?> updateRequestStatus( @PathVariable Integer requestId, @PathVariable String status ,@AuthenticationPrincipal MyUser user) {
//
//        schoolService.updateRequestStatus(user.getId(), requestId, status);
//        return ResponseEntity.ok("Request status updated to: " + status);
//    }


    //supervisor
    @PutMapping("/opportunity-request/{requestId}/complete")
    public ResponseEntity<?> markRequestAsCompleted(@PathVariable Integer requestId,
                                                    @AuthenticationPrincipal MyUser user) {
        schoolService.markRequestAsCompleted(user.getId(), requestId);
        return ResponseEntity.ok("Request status updated to: completed");
    }
    @PutMapping("/opportunity-request/{requestId}/incomplete")
    public ResponseEntity<?> markRequestAsIncomplete(@PathVariable Integer requestId,
                                                     @AuthenticationPrincipal MyUser user) {
        schoolService.markRequestAsIncomplete(user.getId(), requestId);
        return ResponseEntity.ok("Request status updated to: incomplete");
    }



    //supervisor
    @GetMapping("/requests")
    public ResponseEntity<List<StudentOpportunityRequest>> getStudentsRequestsBySchool(@AuthenticationPrincipal MyUser user) {

        List<StudentOpportunityRequest> studentRequests = schoolService.getStudentRequestsBySchoolUser(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(studentRequests);
    }

    //supervisor
    @PutMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptRequest(@AuthenticationPrincipal MyUser user, @PathVariable Integer requestId) {
        schoolService.acceptOrRejectRequest(user.getId(), requestId, "approved");
        return ResponseEntity.ok("Request accepted successfully");
    }

    //supervisor
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectRequest(@AuthenticationPrincipal MyUser user, @PathVariable Integer requestId) {
        schoolService.acceptOrRejectRequest(user.getId(), requestId, "rejected");
        return ResponseEntity.ok("Request rejected successfully");
    }


    // supervisor
    @PutMapping("/students/{studentId}/active")
    public ResponseEntity<String> approveStudent( @PathVariable Integer studentId, @AuthenticationPrincipal MyUser user) {
        schoolService.approveStudentAccount(studentId, user.getId());
        return ResponseEntity.ok("Student account Activated successfully");
    }


    //43
    //supervisor
    @PostMapping("/notify-non-volunteering/{studentId}")
    public ResponseEntity<String> notifyNonVolunteeringStudent(@PathVariable Integer studentId, @AuthenticationPrincipal MyUser user) {
        schoolService.notifyNonVolunteeringStudent(studentId, user.getId());
        return ResponseEntity.ok("Email sent successfully");
    }



    //46
    //supervisor
    @PutMapping("/students/reject/{studentId}")
    public ResponseEntity<String> rejectStudent(@PathVariable Integer studentId, @AuthenticationPrincipal MyUser user) {
        schoolService.rejectStudentAccount(studentId, user.getId());
        return ResponseEntity.ok("Student account has been rejected.");
    }



    //50
    //supervisor
    @GetMapping("/students/{studentId}/details")
    public ResponseEntity<StudentDetailsDTO> getStudentDetails(@PathVariable Integer studentId) {
        StudentDetailsDTO dto = schoolService.getStudentDetailsResponse(studentId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/approve-activation/{studentId}")
    public ResponseEntity<String> approveStudentActivation(@PathVariable Integer studentId) {
        schoolService.approveStudentActivation(studentId);
        return ResponseEntity.ok("Student account activated and email sent successfully");
    }

    //admin
    @PutMapping("/activate/{id}")
    public ResponseEntity<String> activateSchool(@PathVariable Integer id) {
        schoolService.activateSchool(id);
        return ResponseEntity.ok("School account activated successfully");
    }


}