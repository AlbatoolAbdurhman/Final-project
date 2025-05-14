package org.example.atharvolunteeringplatform.Service;

import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.DTO.OpportunityDTO;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Repository.OpportunityRepository;
import org.example.atharvolunteeringplatform.Repository.StudentOpportunityRequestRepository;
import org.example.atharvolunteeringplatform.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentOpportunityRequestService {


    private final StudentRepository studentRepository;
    private final StudentOpportunityRequestRepository studentOpportunityRequestRepository;
    private final OpportunityRepository opportunityrepository;



    public List<StudentOpportunityRequest> getAllRequests(){
        return studentOpportunityRequestRepository.findAll();
    }


    public void RequestOpportunity(Integer studentId, Integer opportunityId, StudentOpportunityRequest studentOpportunityRequest) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student not found");
        }

        Opportunity opportunity = opportunityrepository.findOpportunityById(opportunityId);
        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }

        if (opportunity.getEndDate().isBefore(LocalDate.now())){
            throw new ApiException("Opportunity end date is after current date");
        }

        if (student.getStatus().equalsIgnoreCase("Inactive")) {
            throw new ApiException("Student account not active");
        }

        if (!opportunity.getStatus().equalsIgnoreCase("open")) {
            throw new ApiException("Opportunity not open");
        }

        if (!student.getGender().equalsIgnoreCase(opportunity.getGender())) {
            throw new ApiException("Student not allowed to apply to opportunity due to gender");
        }


        boolean alreadyRequested = studentOpportunityRequestRepository.existsByStudentAndOpportunity(student, opportunity);
        if (alreadyRequested) {
            throw new ApiException("Student has already applied for this opportunity");
        }

        studentOpportunityRequest.setSupervisor_status("pending");
        studentOpportunityRequest.setOrganization_status("pending");
        studentOpportunityRequest.setStatus("pending");
        studentOpportunityRequest.setStudent(student);
        studentOpportunityRequest.setOpportunity(opportunity);
        studentOpportunityRequest.setApplied_at(LocalDateTime.now());

        studentOpportunityRequestRepository.save(studentOpportunityRequest);
    }



    public void deleteOpportunityRequest(Integer requestId) {

        StudentOpportunityRequest existingRequest = studentOpportunityRequestRepository.findStudentOpportunityRequestById(requestId);
        if (existingRequest == null) {
            throw new ApiException("Request not found");
        }


        studentOpportunityRequestRepository.delete(existingRequest);
    }

    public List<StudentOpportunityRequest> getOpportunityRequestsByOrganizationId(Integer organizationId) {
        return studentOpportunityRequestRepository.findStudentOpportunityRequestsByOrganizationId(organizationId);
    }

    //10
    public List<OpportunityDTO> getOpportunitiesByRequestStatus(Integer userId, String status) {
        // التحقق من صحة الحالة
        List<String> validStatuses = Arrays.asList("pending", "rejected", "approved", "progress", "completed", "incomplete");
        if (!validStatuses.contains(status.toLowerCase())) {
            throw new ApiException("Invalid status. Must be one of: " + validStatuses);
        }

        // جلب الطالب من خلال userId
        Student student = studentRepository.findStudentById(userId);
        if (student == null) {
            throw new ApiException("Student not found.");
        }

        // التحقق من أن لديه طلبات
        if (!studentOpportunityRequestRepository.existsByStudentId(student.getId())) {
            throw new ApiException("You have not submitted any volunteer requests.");
        }

        // جلب الطلبات بالحالة المطلوبة
        List<StudentOpportunityRequest> requests =
                studentOpportunityRequestRepository.findByStudentIdAndStatus(student.getId(), status.toLowerCase());

        if (requests.isEmpty()) {
            throw new ApiException("There are no results for this status.");
        }

        // تحويل النتائج إلى DTO
        List<OpportunityDTO> dtoList = new ArrayList<>();
        for (StudentOpportunityRequest request : requests) {
            dtoList.add(new OpportunityDTO(request.getOpportunity()));
        }
        return dtoList;
    }

    //31
    public void approveOrRejectRequestByOrganization(Integer requestId, Integer organizationId, String status) {
        StudentOpportunityRequest request = studentOpportunityRequestRepository.findStudentOpportunityRequestById(requestId);
        if (request == null) {
            throw new ApiException("Request not found");
        }

        Opportunity opportunity = request.getOpportunity();

        // التحقق من أن الجهة هي نفسها المقدمة للفرصة
        if (!opportunity.getOrganization().getId().equals(organizationId)) {
            throw new ApiException("You are not allowed to modify this request");
        }

        // تعيين حالة الجهة
        request.setOrganization_status(status.toLowerCase());

        // في حالة الرفض، نرفض مباشرة
        if (status.equalsIgnoreCase("rejected")) {
            request.setStatus("rejected");
        }
        // في حالة القبول، نتحقق هل المشرف وافق أيضاً؟
        else if (status.equalsIgnoreCase("approved") &&
                "approved".equalsIgnoreCase(request.getSupervisor_status())) {
            request.setStatus("approved");
            request.setApproved_at(LocalDateTime.now());
        }

        studentOpportunityRequestRepository.save(request);
    }



    //15
    public List<StudentOpportunityRequest> getCompletedOpportunitiesByStudent(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student not found");
        }

        return studentOpportunityRequestRepository.findCompletedOpportunitiesByStudentId(studentId);
    }


}