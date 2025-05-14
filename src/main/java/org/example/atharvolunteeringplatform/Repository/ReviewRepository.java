package org.example.atharvolunteeringplatform.Repository;

import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Review findReviewById(Integer id);

    //36
    List<Review> findByOpportunityId(Integer opportunityId);
    @Query("SELECT r FROM Review r WHERE r.opportunity.organization.id = ?1")
    List<Review> findAllByOrganizationId(Integer organizationId);

    List<Review> findAllByOpportunity(Opportunity opportunity);

    int countByOpportunity(Opportunity opportunity);


    @Query("SELECT r FROM Review r WHERE r.opportunity.id = ?1 AND r.opportunity.organization.user.id = ?2")
    List<Review> findReviewsByOpportunityIdAndOrganizationId(Integer opportunityId, Integer organizationId);
}
