package org.example.atharvolunteeringplatform.Repository;


import org.example.atharvolunteeringplatform.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {

    School findSchoolById(Integer id);
}
