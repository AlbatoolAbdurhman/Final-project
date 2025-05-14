package org.example.atharvolunteeringplatform.Controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.DTO.OpportunityDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Service.StudentOpportunityRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student-opportunity-request")
@RequiredArgsConstructor
public class StudentOpportunityRequestController {

    private final StudentOpportunityRequestService studentOpportunityRequestService;

    //admin
    @GetMapping("/get-all")
    public ResponseEntity getAllRequests() {
        return ResponseEntity.status(HttpStatus.OK).body(studentOpportunityRequestService.getAllRequests());
    }

    //student
    @PostMapping("/request/{opportunityId}")
    public ResponseEntity requestOpportunity(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser ,@Valid @RequestBody StudentOpportunityRequest studentOpportunityRequest) {
        studentOpportunityRequestService.RequestOpportunity(myUser.getId(), opportunityId, studentOpportunityRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Opportunity request submitted successfully"));
    }

    //admin
    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity deleteOpportunityRequest(@PathVariable Integer requestId) {
        studentOpportunityRequestService.deleteOpportunityRequest(requestId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity request deleted successfully"));
    }

    @GetMapping("/Opportunity-Requests-By-Organization")
    public ResponseEntity<?> getOrganizationOpportunities(@AuthenticationPrincipal MyUser myUser) {
        List<StudentOpportunityRequest> my = studentOpportunityRequestService.getOpportunityRequestsByOrganizationId(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(my);
    }

    //10
    //student
    @GetMapping("/student/opportunities/filter/{status}")
    public ResponseEntity<?> getStudentOpportunitiesByStatus(@PathVariable String status, @AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.ok(studentOpportunityRequestService.getOpportunitiesByRequestStatus(myUser.getId(), status));
    }


    //31
    //organization
    @PutMapping("/approve-request/{requestId}")
    public ResponseEntity<?> approveRequestByOrganization(@PathVariable Integer requestId, @AuthenticationPrincipal MyUser myUser) {
        studentOpportunityRequestService.approveOrRejectRequestByOrganization(requestId, myUser.getId(), "approved");
        return ResponseEntity.ok("Request approved successfully by organization");
    }

    //organization
    @PutMapping("/reject-request/{requestId}/")
    public ResponseEntity<?> rejectRequestByOrganization(@PathVariable Integer requestId, @AuthenticationPrincipal MyUser myUser) {
        studentOpportunityRequestService.approveOrRejectRequestByOrganization(requestId, myUser.getId(), "rejected");
        return ResponseEntity.ok("Request rejected successfully by organization");
    }





}