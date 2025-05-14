package org.example.atharvolunteeringplatform.Service;

import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.Model.*;
import org.example.atharvolunteeringplatform.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentRepository studentRepository;
    private final StudentOpportunityRequestRepository studentOpportunityRequestRepository;
    private final MyUserRepository myUserRepository;
    private final SchoolRepository schoolRepository;
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    //Integer supervisorId
    public void createReview(Review review, Integer schoolId, Integer opportunityId) {

        School school = schoolRepository.findSchoolById(schoolId);
        if (school == null) {
            throw new ApiException("School not found");
        }

        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);
        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }

        List<StudentOpportunityRequest> completedRequests = studentOpportunityRequestRepository.findCompletedRequestsForOpportunity(opportunityId, schoolId);


        if (completedRequests.isEmpty()) {
            throw new ApiException("This school is not allowed to review this opportunity");
        }

        review.setRating(review.getRating());
        review.setComment(review.getComment());
        review.setCreatedAt(LocalDateTime.now());
        review.setSchool(school);
        review.setOpportunity(opportunity);

        reviewRepository.save(review);
    }


    public void deleteReview(Integer reviewId) {

        Review oldReview = reviewRepository.findReviewById(reviewId);
        if (oldReview == null) {
            throw new ApiException("Review not found");
        }

        reviewRepository.delete(oldReview);
    }

    public void schoolDeleteReview(Integer reviewId, Integer schoolId) {

        Review oldReview = reviewRepository.findReviewById(reviewId);
        if (oldReview == null) {
            throw new ApiException("Review not found");
        }

        if (!oldReview.getSchool().getId().equals(schoolId)) {
            throw new ApiException("You are not authorized to delete this review");
        }

        reviewRepository.delete(oldReview);
    }

    //36
    public List<Review> getReviewsForOpportunity(Integer opportunityId, Integer organizationId) {
        List<Review> reviews = reviewRepository.findReviewsByOpportunityIdAndOrganizationId(opportunityId, organizationId);
        if (reviews == null) {
            throw new ApiException("reviews not found");
        }
        return reviews;
    }



    public Double getAverageRating(Integer organizationId) {
        List<Review> reviews = reviewRepository.findAllByOrganizationId(organizationId);

        if (reviews == null) {
            throw new ApiException("No reviews found for this organization");
        }

        double total = 0;
        for (Review review : reviews) {
            total += review.getRating();
        }

        return total / reviews.size();
    }

    public Integer getReviewCount(Integer organizationId) {
        List<Review> reviews = reviewRepository.findAllByOrganizationId(organizationId);
        return reviews.size();
    }

    public Double getOpportunityAverageRating(Integer opportunityId, Integer organizationId) {
        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);

        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }

        if (!opportunity.getOrganization().getId().equals(organizationId)) {
            throw new ApiException("Access denied: not your opportunity");
        }

        List<Review> reviews = reviewRepository.findAllByOpportunity(opportunity);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Review review : reviews) {
            sum += review.getRating();
        }

        return sum / reviews.size();
    }

    public Integer getReviewCountForOpportunity(Integer opportunityId, Integer organizationId) {
        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);

        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }

        if (!opportunity.getOrganization().getId().equals(organizationId)) {
            throw new ApiException("Access denied: not your opportunity");
        }

        return reviewRepository.countByOpportunity(opportunity);
    }



}