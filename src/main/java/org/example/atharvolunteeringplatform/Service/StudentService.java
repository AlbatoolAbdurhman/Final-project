package org.example.atharvolunteeringplatform.Service;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.atharvolunteeringplatform.Api.ApiException;
import org.example.atharvolunteeringplatform.DTO.StudentDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
 
import org.example.atharvolunteeringplatform.Model.School;
 
import org.example.atharvolunteeringplatform.Model.Opportunity;
 
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Model.StudentOpportunityRequest;
import org.example.atharvolunteeringplatform.Repository.MyUserRepository;
 
import org.example.atharvolunteeringplatform.Repository.SchoolRepository;
 
import org.example.atharvolunteeringplatform.Repository.OpportunityRepository;
import org.example.atharvolunteeringplatform.Repository.StudentOpportunityRequestRepository;
 
import org.example.atharvolunteeringplatform.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {


    private final StudentRepository studentRepository;
    private final MyUserRepository myUserRepository;
 
    private final SchoolRepository schoolRepository;
 
    private final OpportunityRepository opportunityRepository;
    private final StudentOpportunityRequestRepository studentOpportunityRequestRepository;



    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void addStudent(StudentDTO studentDTO) {

        List<School> schools = schoolRepository.findAll();

        // تشفير كلمة المرور قبل حفظها
        String encodedPassword = passwordEncoder.encode(studentDTO.getPassword());

        // إنشاء مستخدم جديد
        MyUser myUser = new MyUser();
        myUser.setName(studentDTO.getName());
        myUser.setUsername(studentDTO.getUsername());
        myUser.setEmail(studentDTO.getEmail());
        myUser.setPhone_number(studentDTO.getPhone_number());
        myUser.setPassword(encodedPassword);
        myUser.setRole("student");
        myUser.setCreated_at(LocalDateTime.now());

        // حفظ المستخدم في قاعدة البيانات
        myUserRepository.save(myUser);

        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setSchool_name(studentDTO.getSchool_name());
        student.setAge(studentDTO.getAge());
        student.setGrade_level(studentDTO.getGrade_level());
        student.setGender(studentDTO.getGender());
        student.setStatus("Inactive");

        // البحث عن المدرسة المطابقة
        School matchedSchool = null;
        for (School school : schools) {
            if (school != null && school.getName() != null && studentDTO.getSchool_name().equalsIgnoreCase(school.getName())) {
                matchedSchool = school;
                break;
            }
        }

        // إذا لم يتم العثور على المدرسة
        if (matchedSchool == null) {
            // نلغي عملية إضافة المستخدم إذا لم تكن المدرسة موجودة
            myUserRepository.delete(myUser);  // حذف المستخدم من قاعدة البيانات
            throw new ApiException("School with name '" + studentDTO.getSchool_name() + "' not found");
        }

        // إذا كانت المدرسة غير نشطة
        if (!"Active".equalsIgnoreCase(matchedSchool.getStatus())) {
            // نلغي عملية إضافة المستخدم إذا كانت المدرسة غير نشطة
            myUserRepository.delete(myUser);  // حذف المستخدم من قاعدة البيانات
            throw new ApiException("Cannot register student to a school that is not Active");
        }

        // إذا كان الجنس غير متوافق
        if (!studentDTO.getGender().equalsIgnoreCase(matchedSchool.getGender())) {
            // نلغي عملية إضافة المستخدم إذا كان الجنس غير متوافق
            myUserRepository.delete(myUser);  // حذف المستخدم من قاعدة البيانات
            throw new ApiException("Student gender does not match the school's gender");
        }


        student.setSchool(matchedSchool);


        student.setUserStudent(myUser);


        studentRepository.save(student);
    }


    public void updateStudent(Integer studentId, StudentDTO studentDTO) {
        MyUser oldUser = myUserRepository.findMyUserById(studentId);
        if (oldUser == null) {
            throw new ApiException("Student not found");
        }

        Student student = studentRepository.findStudentById(oldUser.getId());
        if (student == null) {
            throw new ApiException("Student entity not found");
        }


        oldUser.setName(studentDTO.getName());
        oldUser.setUsername(studentDTO.getUsername());
        oldUser.setEmail(studentDTO.getEmail());
        oldUser.setPhone_number(studentDTO.getPhone_number());
        oldUser.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        oldUser.setRole("student");


        myUserRepository.save(oldUser);

        List<School> schools = schoolRepository.findAll();
        School matchedSchool = null;
        for (School school : schools) {
            if (school != null && school.getName() != null && studentDTO.getSchool_name().equalsIgnoreCase(school.getName())) {
                matchedSchool = school;
                break;
            }
        }

        if (matchedSchool == null) {
            throw new ApiException("School with name '" + studentDTO.getSchool_name() + "' not found");
        }

        if (!"Active".equalsIgnoreCase(matchedSchool.getStatus())) {
            throw new ApiException("Cannot register student to a school that is not Active");
        }

        student.setSchool(matchedSchool);


        student.setAge(studentDTO.getAge());
        student.setGrade_level(studentDTO.getGrade_level());
        student.setStatus(studentDTO.getStatus());

        studentRepository.save(student);
    }

    public void deleteStudent(Integer studentId) {
        MyUser oldUser = myUserRepository.findMyUserById(studentId);
        if (oldUser == null) {
            throw new ApiException("User not found");
        }


        if (!"student".equalsIgnoreCase(oldUser.getRole())) {
            throw new ApiException("Only student accounts can be deleted by this endpoint");
        }

        Student student = studentRepository.findStudentById(oldUser.getId());
        if (student != null) {
            studentRepository.delete(student);
        }

        myUserRepository.delete(oldUser);
    }

    //3
    public List<Opportunity> getOpportunitiesSortedByHours() {
        return opportunityRepository.findAllByOrderByHoursDesc();
    }

    //7
    public List<Opportunity> getOpportunitiesByDateRange(LocalDate from, LocalDate to) {
        return opportunityRepository.findByStartDateBetween(from, to);
    }

    //9
    public List<StudentOpportunityRequest> getMyRequests(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student not found");
        }
        return studentOpportunityRequestRepository.findAllByStudent(student);
    }


    //16
    public Map<String, Integer> getStudentHoursSummary(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student not found");
        }

        int completedHours = 0;
        if (student.getTotal_hours() != null) {
            completedHours = student.getTotal_hours();
        }
        int requiredHours = 40;
        int remainingHours = Math.max(requiredHours - completedHours, 0);

        Map<String, Integer> hoursSummary = new HashMap<>();
        hoursSummary.put("completed_hours", completedHours);
        hoursSummary.put("remaining_hours", remainingHours);

        return hoursSummary;
    }


    //44
    //for supervisor
    //ارجاع الطالب المسجلين في الموقع لقبولهم
    public List<Student> getInactiveStudents(Integer schoolId) {
        return studentRepository.findStudentByStatus(schoolId);
    }




    public void requestCertificate(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) throw new ApiException("Student not found");

        if (student.getTotal_hours() == null || student.getTotal_hours() < 40) {
            throw new ApiException("Student has not completed 40 hours.");
        }

        byte[] pdf = generateCertificatePdf(student.getName());
        sendCertificateEmail(student.getUserStudent().getEmail(), pdf);
    }
    private byte[] generateCertificatePdf(String studentName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            // Load English font
            String fontPath = "src/main/resources/fonts/GreatVibes-Regular.ttf";  // Change this to your actual font path
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 16, Font.NORMAL);
            Font titleFont = new Font(baseFont, 20, Font.BOLD);

            // Title
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Certificate of Volunteering", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);

            document.add(new com.itextpdf.text.Paragraph(" ")); // Spacer

            // Content
            com.itextpdf.text.Paragraph paragraph1 = new com.itextpdf.text.Paragraph(
                    "This is to certify that the student " + studentName + " has completed 40 hours of voluntary work.", font);
            paragraph1.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            document.add(paragraph1);

            com.itextpdf.text.Paragraph paragraph2 = new com.itextpdf.text.Paragraph(
                    "We believe in the importance of volunteering and its positive impact on the community.", font);
            paragraph2.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            document.add(paragraph2);

            com.itextpdf.text.Paragraph paragraph3 = new com.itextpdf.text.Paragraph(
                    "Issued on: " + LocalDate.now(), font);
            paragraph3.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            document.add(paragraph3);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }





    @Autowired
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    private void sendCertificateEmail(String toEmail, byte[] pdfData) {
        try {
            org.springframework.mail.javamail.MimeMessageHelper helper;
            jakarta.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
            helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("شهادة إتمام ساعات تطوعية");
            helper.setText("مبروك! تم إرفاق شهادتك بصيغة PDF.");
            helper.addAttachment("VolunteerCertificate.pdf", new org.springframework.core.io.ByteArrayResource(pdfData));

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


}