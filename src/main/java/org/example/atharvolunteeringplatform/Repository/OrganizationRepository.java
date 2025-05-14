package org.example.atharvolunteeringplatform.Repository;

import org.example.atharvolunteeringplatform.Model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    Organization findOrganizationById(Integer id);


}
