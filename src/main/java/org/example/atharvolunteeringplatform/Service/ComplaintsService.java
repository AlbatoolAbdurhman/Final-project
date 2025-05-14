package org.example.atharvolunteeringplatform.Service;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.Model.Complaint;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Repository.ComplaintsRepository;
import org.example.atharvolunteeringplatform.Repository.OpportunityRepository;
import org.example.atharvolunteeringplatform.Repository.StudentOpportunityRequestRepository;
import org.example.atharvolunteeringplatform.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintsService {


    private final ComplaintsRepository complaintsRepository;
    private final StudentRepository studentRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentOpportunityRequestRepository studentOpportunityRequestRepository;
    public List<Complaint> getAllComplaints() {
        return complaintsRepository.findAll();
    }

    public void addComplaint(Complaint complaint, Integer studentId, Integer opportunityId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) throw new ApiException("Student not found");

        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);
        if (opportunity == null) throw new ApiException("Opportunity not found");

        StudentOpportunityRequest request = studentOpportunityRequestRepository.findRequestByStudentIdAndOpportunityId(studentId, opportunity.getId());

        if (request == null || (!request.getStatus().equalsIgnoreCase("completed") &&
                !request.getStatus().equalsIgnoreCase("incomplete"))) {
            throw new ApiException("You can only submit a complaint if you have completed or incompletely finished the opportunity");
        }

        complaint.setTitle(complaint.getTitle());
        complaint.setDescription(complaint.getDescription());
        complaint.setOpportunity(opportunity);
        complaint.setStudents(student);
        complaint.setCreateAt(LocalDateTime.now());
        complaint.setStatus("In Progress");
        complaintsRepository.save(complaint);
    }

    public void updateComplaint(Integer id, Complaint updatedComplaint, Integer studentId) {
        Complaint complaint = complaintsRepository.findComplaintsById(id);
        if (complaint == null) throw new ApiException("Complaint not found");

        if (!complaint.getStudents().getId().equals(studentId)) {
            throw new ApiException("You can only update your own complaint");
        }

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setDescription(updatedComplaint.getDescription());
        complaint.setStatus(updatedComplaint.getStatus());
        complaintsRepository.save(complaint);
    }

    public void deleteComplaint(Integer id, Integer studentId) {
        Complaint complaint = complaintsRepository.findComplaintsById(id);
        if (complaint == null) throw new ApiException("Complaint not found");

        if (!complaint.getStudents().getId().equals(studentId)) {
            throw new ApiException("You can only delete your own complaint");
        }

        complaintsRepository.delete(complaint);
    }

    //12
    public List<Complaint> getComplaintsByStudentId(Integer studentId) {
        List<Complaint> complaints = complaintsRepository.findByStudentsId(studentId);
        if (complaints == null || complaints.isEmpty()) {
            throw new ApiException("No complaints found for the given student ID");
        }
        return complaints;
    }

    //14

    public List<Complaint> getComplaintsByStudentAndDate(Integer studentId, LocalDateTime from, LocalDateTime to) {
        return complaintsRepository.findByStudentIdAndCreateAtBetween(studentId, from, to);
    }


    //13
    public List<Complaint> getMyComplaintsByStatus(Integer studentId, String status) {
        return complaintsRepository.findByStudentIdAndStatus(studentId, status);
    }


    public void updateComplaintStatus(Integer complaintId, String newStatus) {
        Complaint complaint = complaintsRepository.findComplaintsById(complaintId);

        if (complaint == null) {
            throw new ApiException("Complaint not found");
        }

        if (!newStatus.matches("^(In Progress|Resolved|Closed)$")) {
            throw new ApiException("Invalid status. Must be one of: In Progress, Resolved, Closed");
        }

        complaint.setStatus(newStatus);
        complaintsRepository.save(complaint);
    }

}