package org.example.atharvolunteeringplatform.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiResponse;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Organization;
import org.example.atharvolunteeringplatform.Model.Review;
import org.example.atharvolunteeringplatform.Service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    //admin
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    //supervisor
    @PostMapping("/add-review/opportunity/{opportunityId}")
    public ResponseEntity<?> addReview(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser, @RequestBody @Valid Review review) {
        reviewService.createReview(review, myUser.getId(), opportunityId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Review added successfully"));
    }

    //admin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Review deleted successfully"));
    }
    //supervisor
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> schoolDeleteReview(@PathVariable Integer reviewId, @AuthenticationPrincipal MyUser myUser) {
        reviewService.schoolDeleteReview(reviewId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Review deleted successfully"));
    }

    //organization
    @GetMapping("/get-review/{opportunityId}")
    public ResponseEntity<?> getReviewForOpportunity(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser) {
        List<Review> review = reviewService.getReviewsForOpportunity(opportunityId, myUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    //organization
    @GetMapping("/organization/average-rating")
    public ResponseEntity<?> getOrganizationAverageRating(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.ok(reviewService.getAverageRating(myUser.getId()));
    }
    //organization
    @GetMapping("/organization/review-count")
    public ResponseEntity<?> getOrganizationReviewCount(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.ok(reviewService.getReviewCount(myUser.getId()));
    }

    //organization
    @GetMapping("/opportunity/{opportunityId}/average-rating")
    public ResponseEntity<?> getOpportunityAverageRating(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.ok(reviewService.getOpportunityAverageRating(opportunityId, myUser.getId()));
    }

    //organization
    @GetMapping("/opportunity/{opportunityId}/review-count")
    public ResponseEntity<?> getOpportunityReviewCount(@PathVariable Integer opportunityId, @AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.ok(reviewService.getReviewCountForOpportunity(opportunityId, myUser.getId()));
    }

}