package org.example.atharvolunteeringplatform.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.DTO.OrganizationDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.Organization;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Repository.MyUserRepository;
import org.example.atharvolunteeringplatform.Repository.OpportunityRepository;
import org.example.atharvolunteeringplatform.Repository.OrganizationRepository;
import org.example.atharvolunteeringplatform.Repository.StudentOpportunityRequestRepository;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final MyUserRepository userRepository;
    private final StudentOpportunityRequestRepository studentOpportunityRequestRepository;
    private final OpportunityRepository opportunityRepository;

    private final MailSender mailSender;



    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    public void createOrganization(OrganizationDTO organizationDTO) {
        MyUser user = new MyUser();

        user.setName(organizationDTO.getName());
        user.setUsername(organizationDTO.getUsername());
        user.setEmail(organizationDTO.getEmail());
        String hashPassword = new BCryptPasswordEncoder().encode(organizationDTO.getPassword());
        user.setPassword(hashPassword);
        user.setPhone_number(organizationDTO.getPhoneNumber());
        user.setRole("organization");
        user.setCreated_at(LocalDateTime.now());

        Organization organization = new Organization();

        organization.setName(organizationDTO.getName());
        organization.setLicense(organizationDTO.getLicense());
        organization.setLocation(organizationDTO.getLocation());
        organization.setStatus("Inactive");
        organization.setUser(user);
        user.setOrganization(organization);
        organizationRepository.save(organization);
        userRepository.save(user);
    }

    public void updateOrganization(Integer organizationId , OrganizationDTO organizationDTO) {
        MyUser oldUser = userRepository.findMyUserById(organizationId);
        Organization oldOrganization = organizationRepository.findOrganizationById(organizationId);

        if (oldUser == null || oldOrganization == null) {
            throw new ApiException("Organization not found");
        }
        oldUser.setName(organizationDTO.getName());
        oldUser.setUsername(organizationDTO.getUsername());
        oldUser.setEmail(organizationDTO.getEmail());
        oldUser.setPhone_number(organizationDTO.getPhoneNumber());
        String hashPassword = new BCryptPasswordEncoder().encode(organizationDTO.getPassword());
        oldUser.setPassword(hashPassword);

        userRepository.save(oldUser);

        oldOrganization.setDescription(oldOrganization.getDescription());
        oldOrganization.setLicense(oldOrganization.getLicense());
        oldOrganization.setLocation(oldOrganization.getLocation());
        oldOrganization.setStatus("Inactive");
        organizationRepository.save(oldOrganization);

    }

    public void deleteOrganization(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        organizationRepository.delete(organization);
    }



    //19 volunteersCount
    public int volunteersCount(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        List<StudentOpportunityRequest> completedByOrganization = studentOpportunityRequestRepository.findCompletedByOrganizationId(organizationId);

        int volunteersCount = 0;
        for (int i = 0; i < completedByOrganization.size(); i++) {
            volunteersCount++;
        }

        return volunteersCount;
    }

    //59
    public int opportunitiesCount(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        List<Opportunity> openOpportunities = opportunityRepository.findOpenOpportunitiesByOrganizationId(organizationId);
        int opportunitiesCount = 0;
        for (int i = 0; i < openOpportunities.size(); i++) {
            opportunitiesCount++;
        }
        return opportunitiesCount;
    }

    //60
    public int getTotalVolunteeringHours(Integer organizationId) {
        List<StudentOpportunityRequest> requests = studentOpportunityRequestRepository.findCompletedByOrganizationId(organizationId);

        int totalHours = 0;
        for (StudentOpportunityRequest request : requests) {
            totalHours += request.getOpportunity().getHours();
        }
        return totalHours;
    }



    //عرض طلبات التطوع المرسلة من قبل الطلاب : 30
    public List<StudentOpportunityRequest> getPendingRequestsByOrganization(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }

        return studentOpportunityRequestRepository.findPendingRequestsByOrganizationId(organizationId);
    }

    //32
    public void rejectVolunteerRequest(Integer organizationId, Integer requestId) {
        StudentOpportunityRequest request = studentOpportunityRequestRepository.findStudentOpportunityRequestById(requestId);
        Organization organization = organizationRepository.findOrganizationById(organizationId);

        if (organization == null) throw new ApiException("Organization not found");
        if (organization.getStatus().equals("Inactive")) throw new ApiException("Organization is Inactive");
        if (request == null) throw new ApiException("Request not found");
        if (!request.getOpportunity().getOrganization().getId().equals(organization.getId())) {
            throw new ApiException("Request is not in this organization");
        }

        request.setOrganization_status("rejected");
        request.setStatus("rejected");
        studentOpportunityRequestRepository.save(request);

        String to = request.getStudent().getUserStudent().getEmail();
        String subject = "رفض طلب التطوع";
        String body = "نأسف، تم رفض طلبك للتطوع في الفرصة: " + request.getOpportunity().getTitle();
        sendDecisionEmail(to, subject, body);
    }



    public void acceptVolunteerRequest(Integer organizationId, Integer requestId) {
        StudentOpportunityRequest request = studentOpportunityRequestRepository.findStudentOpportunityRequestById(requestId);
        Organization organization = organizationRepository.findOrganizationById(organizationId);

        if (organization == null) throw new ApiException("Organization not found");
        if (organization.getStatus().equalsIgnoreCase("Inactive")) throw new ApiException("Organization is Inactive");
        if (request == null) throw new ApiException("Request not found");

        if (!request.getOpportunity().getOrganization().getId().equals(organization.getId())) {
            throw new ApiException("Request is not in this organization");
        }

        // ... نفس الكود السابق

        request.setOrganization_status("approved");

        if ("approved".equalsIgnoreCase(request.getSupervisor_status())) {
            request.setStatus("approved");
            request.setApproved_at(LocalDateTime.now());
        }

        studentOpportunityRequestRepository.save(request);

        // ✅ فقط إذا تم اعتماد الطلب من الطرفين
        if ("approved".equalsIgnoreCase(request.getStatus())) {
            String to = request.getStudent().getUserStudent().getEmail();
            String subject = "قبول طلب التطوع";
            String body = "تهانينا! تم قبول طلبك للتطوع في الفرصة من قبل الجهة والمنسق: " + request.getOpportunity().getTitle();
            sendDecisionEmail(to, subject, body);
        }
    }



    //34
    public List<StudentOpportunityRequest> getVolunteerRequestHistory(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        return studentOpportunityRequestRepository.findHistoryByOrganizationId(organizationId);
    }

    public void openOpportunity(Integer opportunityId, Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);

        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        if (organization.getStatus().equals("Inactive") ) {
            throw new ApiException("Organization is Inactive ");
        }
        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }
        if (opportunity.getOrganization().getId() != organizationId) {
            throw new ApiException("Unauthorized access");
        }


        if (opportunity.getStatus().equalsIgnoreCase("open")) {
            throw new ApiException("Opportunity is already open");
        }
        if (!opportunity.getStatus().equalsIgnoreCase("accepted")) {
            throw new ApiException("Only accepted opportunities can be opened");
        }


        opportunity.setStatus("open");

        opportunityRepository.save(opportunity);
    }

    public void closeOpportunity(Integer opportunityId, Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        Opportunity opportunity = opportunityRepository.findOpportunityById(opportunityId);

        if (organization == null) {
            throw new ApiException("Organization not found");
        }
        if (organization.getStatus().equals("Inactive") ) {
            throw new ApiException("Organization is Inactive ");
        }
        if (opportunity == null) {
            throw new ApiException("Opportunity not found");
        }
        if (opportunity.getOrganization().getId() != organizationId) {
            throw new ApiException("Unauthorized access");
        }

        if (opportunity.getStatus().equalsIgnoreCase("open") || opportunity.getStatus().equalsIgnoreCase("closed") || opportunity.getStatus().equalsIgnoreCase("accepted")) {
            opportunity.setStatus("closed");
        }
        opportunityRepository.save(opportunity);
    }


    public void sendDecisionEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }



    //55
    public void activateOrganization(Integer organizationId) {
        Organization organization = organizationRepository.findOrganizationById(organizationId);
        if (organization == null) {
            throw new ApiException("Organization not found");
        }

        if (organization.getStatus().equals("Active")) {
            throw new ApiException("Organization is already active");
        }

        organization.setStatus("Active");
        organizationRepository.save(organization);
    }


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void createAdminUser() {
        if (userRepository.findUserByUsername("admin") == null) {
            MyUser admin = new MyUser();
            admin.setName("Admin User");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPhone_number("0500000000");
            admin.setPassword(passwordEncoder.encode("Admin12345"));            admin.setRole("admin");
            admin.setCreated_at(LocalDateTime.now());

            userRepository.save(admin);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}