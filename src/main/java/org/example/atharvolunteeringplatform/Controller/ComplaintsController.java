package org.example.atharvolunteeringplatform.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.Model.Complaint;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Service.ComplaintsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/Complaints")
@RequiredArgsConstructor

public class ComplaintsController {

    private final ComplaintsService complaintsService;

    //admin
    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintsService.getAllComplaints());
    }

    //student
    @PostMapping("/add/{opportunityId}")
    public ResponseEntity<String> addComplaint(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser user, @RequestBody @Valid Complaint complaint) {
        complaintsService.addComplaint(complaint, user.getId(),opportunityId);
        return ResponseEntity.ok("Complaint added successfully");
    }

    //student
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateComplaint(@AuthenticationPrincipal MyUser user, @PathVariable Integer id, @RequestBody @Valid Complaint complaint) {
        complaintsService.updateComplaint(id, complaint, user.getId());
        return ResponseEntity.ok("Complaint updated successfully");
    }

    //student
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteComplaint(@AuthenticationPrincipal MyUser user, @PathVariable Integer id) {
        complaintsService.deleteComplaint(id, user.getId());
        return ResponseEntity.ok("Complaint deleted successfully");
    }




    //12
    //student
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getMyComplaints(@AuthenticationPrincipal MyUser user) {
        return ResponseEntity.status(HttpStatus.OK).body(complaintsService.getComplaintsByStudentId(user.getId()));

    }


    //student
    @GetMapping("/by-date/{from}/{to}")
    public ResponseEntity<?> getComplaintsByDate(@PathVariable LocalDateTime from, @PathVariable LocalDateTime to,@AuthenticationPrincipal MyUser user) {
        List<Complaint> complaints = complaintsService.getComplaintsByStudentAndDate(user.getId(), from, to);
        return ResponseEntity.status(HttpStatus.OK).body(complaints);

    }


    //student
    @GetMapping("/my-complaints/{status}")
    public ResponseEntity getMyComplaintsByStatus(@AuthenticationPrincipal MyUser user, @PathVariable String status) {
        return ResponseEntity.status(HttpStatus.OK).body(
                complaintsService.getMyComplaintsByStatus(user.getId(), status));
    }

    //Admin
    @PutMapping("/update-status/{id}/{status}")
    public ResponseEntity<String> updateComplaintStatus(@PathVariable Integer id, @PathVariable String status) {
        complaintsService.updateComplaintStatus(id, status);
        return ResponseEntity.ok("Complaint status updated successfully");

    }


}