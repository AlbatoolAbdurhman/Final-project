package org.example.atharvolunteeringplatform.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.Model.Badge;
import org.example.atharvolunteeringplatform.Service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/badge")
@RequiredArgsConstructor

public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllBadges() {
        badgeService.findBadges();
        return ResponseEntity.status(HttpStatus.OK).body(badgeService.findBadges());
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addBadge(@RequestPart("badge") String badgeJson, @RequestPart("image") MultipartFile image) {
        ObjectMapper objectMapper = new ObjectMapper();
        Badge badge;
        try {
            badge = objectMapper.readValue(badgeJson, Badge.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format in 'badge'");
        }

        badgeService.createBadge(badge, image);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Badge created successfully"));
    }


    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateBadge(
            @PathVariable Integer id,
            @RequestPart("badge") String badgeJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        ObjectMapper objectMapper = new ObjectMapper();
        Badge badge;
        try {
            badge = objectMapper.readValue(badgeJson, Badge.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid badge data");
        }

        badgeService.updateBadge(id, badge, image);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Badge updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBadge(@PathVariable Integer id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Badge deleted successfully"));
    }

    @GetMapping("/badgeDetails/{badgeId}")
    public ResponseEntity<?> getBadgeDetails(@PathVariable Integer badgeId) {
        Badge badge = badgeService.getBadgeDetails(badgeId);
        return ResponseEntity.status(HttpStatus.OK).body(badge);
    }



}
