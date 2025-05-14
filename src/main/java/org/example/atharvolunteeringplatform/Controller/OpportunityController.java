package org.example.atharvolunteeringplatform.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.atharvolunteeringplatform.DTO.OpportunityDTO;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Repository.OpportunityRepository;
import org.example.atharvolunteeringplatform.Service.OpportunityService;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunity")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final OpportunityRepository opportunityRepository;

    //admin
    @GetMapping("/get-all")
    public ResponseEntity getAllOpportunities() {
        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.findAllOpportunities());
    }

    //organization
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> createOpportunity(@RequestPart("opportunity") String opportunityJson,
                                               @RequestPart("image") MultipartFile image,
                                               @AuthenticationPrincipal MyUser myUser) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Opportunity opportunity;
        try {
            opportunity = objectMapper.readValue(opportunityJson, Opportunity.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format in 'opportunity'");
        }


        // Automatically use the logged-in organization user's ID
        Integer organizationId = myUser.getId();

        opportunityService.createOpportunity(opportunity, organizationId, image);
        return ResponseEntity.status(HttpStatus.CREATED).body("Opportunity created successfully");
    }

    //admin
    @GetMapping("/opportunities/image/{id}")
    public ResponseEntity<Resource> getOpportunityImage(@PathVariable Integer id) throws IOException {
        Opportunity opportunity = opportunityRepository.findOpportunityById(id);
        if (opportunity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }

        String imagePath = opportunity.getImagePath(); // e.g., "/uploads/opportunities/uuid_image.jpg"
        Path filePath = Paths.get("src/main/resources/static" + imagePath);

        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image file not found");
        }

        Resource imageResource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream")).body(imageResource);
    }



    //organization
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateOpportunity(@RequestParam("opportunityId") Integer opportunityId,
                                               @RequestPart("opportunity") String opportunityJson,
                                               @RequestPart(value = "image", required = false) MultipartFile imageFile,
                                               @AuthenticationPrincipal MyUser myUser) {
        ObjectMapper objectMapper = new ObjectMapper();
        Opportunity updatedOpportunity;

        try {
            updatedOpportunity = objectMapper.readValue(opportunityJson, Opportunity.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format for 'opportunity'");
        }

        Integer organizationId = myUser.getId(); // Or getOrganizationId() if you store it separately

        opportunityService.updateOpportunity(opportunityId, organizationId, updatedOpportunity, imageFile);

        return ResponseEntity.ok("Opportunity updated successfully");
    }


    //admin
    @DeleteMapping("/delete/{opportunityId}")
    public ResponseEntity<?> deleteOpportunity(@PathVariable Integer opportunityId) {
        opportunityService.deleteOpportunity(opportunityId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity deleted successfully"));
    }





    //organization
    @GetMapping("/sorted-by-capacity")
    public ResponseEntity<?> getOpportunitiesSorted(@AuthenticationPrincipal MyUser myUser) {
        List<Opportunity> sorted = opportunityService.getOpportunitiesSortedByCapacity(myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(sorted);
    }

    //organization
    @GetMapping("/count/open")
    public ResponseEntity<?> getOpenCount(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.countByStatus(myUser.getId(), "Open"));
    }

    //organization
    @GetMapping("/count/pending")
    public ResponseEntity<?> getPendingCount(@AuthenticationPrincipal MyUser myUser) {

        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.countByStatus(myUser.getId(), "pending"));
    }

    //organization
    @GetMapping("/count/rejected")
    public ResponseEntity<?> getRejectedCount(@AuthenticationPrincipal MyUser myUser) {

        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.countByStatus(myUser.getId(), "rejected"));
    }

    //organization
    @GetMapping("/count/closed")
    public ResponseEntity<?> getClosedCount(@AuthenticationPrincipal MyUser myUser) {

        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.countByStatus(myUser.getId(), "closed"));
    }

    //organization
    @GetMapping("/count/total")
    public ResponseEntity<?> getTotalCount(@AuthenticationPrincipal MyUser myUser) {

        return ResponseEntity.status(HttpStatus.OK).body(opportunityService.countTotalOpportunities(myUser.getId()));

    }

    //all
    @GetMapping("/get-Open-Opportunities")
    public ResponseEntity<?> getOpportunitiesByStatus() {
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByStatus();
        return ResponseEntity.status(HttpStatus.OK).body(opportunities);
    }

    //admin
    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptOpportunity(@PathVariable Integer id) {
        opportunityService.acceptOpportunity(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity accepted and email sent."));
    }

    //admin
    @PostMapping("/accept-edit/{id}")
    public ResponseEntity<?> acceptOpportunityEdit(@PathVariable Integer id) {
        opportunityService.acceptOpportunityEdit(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity edit accepted and email sent."));
    }

    //admin
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectOpportunity(@PathVariable Integer id) {
        opportunityService.rejectOpportunity(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity rejected and email sent."));
    }

    //admin
    @PostMapping("/reject-edit/{id}")
    public ResponseEntity<?> rejectOpportunityEdit(@PathVariable Integer id) {
        opportunityService.rejectOpportunityEdit(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Opportunity edit rejected and email sent."));
    }

    //admin
    @PutMapping("/admin/change-opportunity-status/{opportunityId}/{newStatus}")
    public ResponseEntity<?> changeOpportunityStatus(@PathVariable Integer opportunityId, @PathVariable String newStatus) {
        opportunityService.changeOpportunityStatus(opportunityId, newStatus);
        return ResponseEntity.ok(new ApiResponse("Status updated successfully"));
    }

    @GetMapping("/open")
    public ResponseEntity<List<OpportunityDTO>> getOpenOpportunitiesByLocation(@RequestParam String location) {
        List<OpportunityDTO> opportunities = opportunityService.getOpenOpportunitiesByLocation(location);
        return ResponseEntity.ok(opportunities);
    }

    @PutMapping("/approve/{opportunityId}")
    public ResponseEntity<String> approveOpportunity(@PathVariable Integer opportunityId) {
        opportunityService.acceptOpportunity(opportunityId);
        return ResponseEntity.ok("Opportunity approved successfully");
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Opportunity>> getLatestOpportunitiesByOrganization(@RequestParam Integer organizationId) {
        List<Opportunity> opportunities = opportunityService.getLatestOpportunitiesByOrganization(organizationId);
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/organization")
    public ResponseEntity<List<Opportunity>> getOpportunitiesByOrganization(@RequestParam Integer organizationId) {
        List<Opportunity> opportunities = opportunityService.getOpportunitiesByOrganization(organizationId);
        return ResponseEntity.ok(opportunities);
    }





}