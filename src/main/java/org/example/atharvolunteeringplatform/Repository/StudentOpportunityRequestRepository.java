package org.example.atharvolunteeringplatform.Repository;



import org.example.atharvolunteeringplatform.Model.Opportunity;

import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentOpportunityRequestRepository extends JpaRepository<StudentOpportunityRequest,Integer> {

    StudentOpportunityRequest findStudentOpportunityRequestById(Integer id);

    @Query("SELECT s FROM StudentOpportunityRequest s WHERE s.opportunity.organization.id =?1")
    List<StudentOpportunityRequest> findStudentOpportunityRequestsByOrganizationId(Integer organizationId);

    //***
    StudentOpportunityRequest findByStudentAndOpportunity(Student student,Opportunity opportunity);

    List<StudentOpportunityRequest>findAllByStudentId(Integer studentId);



    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.opportunity.organization.id = ?1 AND r.status = 'completed'")
    List<StudentOpportunityRequest> findCompletedByOrganizationId(Integer organizationId);


    //10
    List<StudentOpportunityRequest> findByStudentIdAndStatus(Integer studentId, String status);

    boolean existsByStudentId(Integer studentId);

    //41
    //تقيم الفرصه من شرف النشاط
    // التأكد من وجود طالب من المدرسة قام بفرصة وحالتها منجزة
    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.opportunity.id = :opportunityId AND r.status = 'completed' AND r.student.school.id = :schoolId")
    List<StudentOpportunityRequest> findCompletedRequestsForOpportunity(Integer opportunityId, Integer schoolId);

    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.opportunity.organization.id = ?1 AND r.status = 'Pending'")
    List<StudentOpportunityRequest> findPendingRequestsByOrganizationId(Integer organizationId);

    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.opportunity.organization.id = ?1 AND r.status IN ('accepted', 'rejected')")
    List<StudentOpportunityRequest> findHistoryByOrganizationId(Integer organizationId);


    List<StudentOpportunityRequest> findAllByStudent_Id(Integer studentId);






    List<StudentOpportunityRequest> findAllByStudent(Student student);

    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.student.id = ?1 AND LOWER(r.status) = 'completed'")
    List<StudentOpportunityRequest> findCompletedOpportunitiesByStudentId(Integer studentId);


    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.student.school.id = ?1")
    List<StudentOpportunityRequest> findAllBySchoolId(Integer schoolId);

    boolean existsByStudentAndOpportunity(Student student, Opportunity opportunity);

    @Query("SELECT r FROM StudentOpportunityRequest r WHERE r.student.id = :studentId AND r.opportunity.id = :opportunityId")
    StudentOpportunityRequest findRequestByStudentIdAndOpportunityId(Integer studentId, Integer opportunityId);

}