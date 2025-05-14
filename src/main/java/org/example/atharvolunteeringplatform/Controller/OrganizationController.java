package org.example.atharvolunteeringplatform.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.DTO.OrganizationDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    //admin
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllOrganizations() {
        return ResponseEntity.status(HttpStatus.OK).body(organizationService.findAll());
    }

    //organization
    @PostMapping("/add")
    public ResponseEntity<?> addOrganization(@RequestBody @Valid OrganizationDTO organizationDTO) {
        organizationService.createOrganization(organizationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Organization register successfully"));
    }

    //organization
    @PutMapping("/update")
    public ResponseEntity<?> updateOrganization(@AuthenticationPrincipal MyUser myUser, @RequestBody @Valid OrganizationDTO organizationDTO) {
        organizationService.updateOrganization(myUser.getId(), organizationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Organization updated successfully"));
    }

    //admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrganization( @PathVariable Integer id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Organization deleted successfully"));
    }

    //organization
    @GetMapping("/count/volunteers")
    public ResponseEntity<?> getVolunteersCount(@AuthenticationPrincipal MyUser myUser) {
        int count = organizationService.volunteersCount(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    //organization
    @GetMapping("/count/opportunities")
    public ResponseEntity<?> getOpportunitiesCount(@AuthenticationPrincipal MyUser myUser) {
        int count = organizationService.opportunitiesCount(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    //organization
    @GetMapping("/total-hours")
    public ResponseEntity<?> getTotalVolunteeringHours(@AuthenticationPrincipal MyUser myUser) {
        int totalHours = organizationService.getTotalVolunteeringHours(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(totalHours);
    }


    //organization
    @GetMapping("/organization/pending")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal MyUser myUser) {
        List<StudentOpportunityRequest> requests = organizationService.getPendingRequestsByOrganization(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    //organization
    @PutMapping("/reject-request/{requestId}")
    public ResponseEntity<?> rejectVolunteerRequest(@PathVariable Integer requestId,@AuthenticationPrincipal MyUser myUser ) {
        organizationService.rejectVolunteerRequest(requestId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Volunteer request rejected and email sent.");
    }

    //organization
    @PutMapping("/accept-request/{requestId}")
    public ResponseEntity<?> acceptVolunteerRequest(@PathVariable Integer requestId,@AuthenticationPrincipal MyUser myUser) {
        organizationService.acceptVolunteerRequest(myUser.getId(), requestId);
        return ResponseEntity.status(HttpStatus.OK).body("Volunteer request accepted and email sent.");
    }

    //organization
    @GetMapping("/organization/history")
    public ResponseEntity<?> getVolunteerRequestHistory(@AuthenticationPrincipal MyUser myUser) {
        List<StudentOpportunityRequest> history = organizationService.getVolunteerRequestHistory(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    //organization
    @PutMapping("/open/{opportunityId}")
    public ResponseEntity<?> openOpportunity(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser) {
        organizationService.openOpportunity(opportunityId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity status updated to open"));
    }

    //organization
    @PutMapping("/close/{opportunityId}")
    public ResponseEntity<?> closeOpportunity(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser) {
        organizationService.closeOpportunity(opportunityId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity status updated to closed"));
    }

    //admin
    @PutMapping("/activate-organization/{organizationId}")
    public ResponseEntity<?> activateOrganization(@PathVariable Integer organizationId) {
        organizationService.activateOrganization(organizationId);
        return ResponseEntity.ok(new ApiResponse("Organization has been activated successfully"));
    }




}