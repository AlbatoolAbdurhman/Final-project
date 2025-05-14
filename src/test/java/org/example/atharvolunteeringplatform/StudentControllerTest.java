package org.example.atharvolunteeringplatform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.atharvolunteeringplatform.Controller.StudentController;
import org.example.atharvolunteeringplatform.DTO.StudentDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.Opportunity;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Service.StudentOpportunityRequestService;
import org.example.atharvolunteeringplatform.Service.StudentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = StudentController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class StudentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentService studentService;

    @MockBean
    StudentOpportunityRequestService studentOpportunityRequestService;

    Student student;
    StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1);
        student.setName("Ahmed");
        student.setSchool_name("High School");
        student.setAge(17);
        student.setGrade_level("Second Secondary");
        student.setGender("Male");
        student.setStatus("Inactive");
        student.setTotal_hours(20);
        student.setBadges_count(2);

        studentDTO = new StudentDTO();
        studentDTO.setId(1);
        studentDTO.setUsername("ahmed123");
        studentDTO.setName("Ahmed");
        studentDTO.setEmail("ahmed@example.com");
        studentDTO.setPhone_number("0555555555");
        studentDTO.setPassword("password123");
        studentDTO.setSchool_name("High School");
        studentDTO.setAge(17);
        studentDTO.setGrade_level("Second Secondary");
        studentDTO.setGender("Male");
        studentDTO.setStatus("Inactive");
    }

    @Test
    void testGetAllStudents() throws Exception {
        List<Student> students = Collections.singletonList(student);
        Mockito.when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/v1/student/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Ahmed"));
    }

    @Test
    void testAddStudent() throws Exception {
        mockMvc.perform(post("/api/v1/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStudent() throws Exception {
        MyUser mockUser = new MyUser();
        mockUser.setId(1); // يجب أن يتطابق مع ما تتوقعه الخدمة

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null);

        mockMvc.perform(put("/api/v1/student/update")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(studentDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/v1/student/delete/{studentId}", 1))
                .andExpect(status().isOk());
    }


    @Test
    void testGetOpportunitiesByDateRange() throws Exception {

        Opportunity opportunity1 = new Opportunity();
        opportunity1.setId(1);
        opportunity1.setTitle("Opportunity A");

        Opportunity opportunity2 = new Opportunity();
        opportunity2.setId(2);
        opportunity2.setTitle("Opportunity B");

        List<Opportunity> mockOpportunities = List.of(opportunity1, opportunity2);


        String fromDate = "2024-01-01";
        String toDate = "2024-12-31";

        // mock
        Mockito.when(studentService.getOpportunitiesByDateRange(
                        LocalDate.parse(fromDate), LocalDate.parse(toDate)))
                .thenReturn(mockOpportunities);

        mockMvc.perform(get("/api/v1/student/opportunities-by-date/{from}/{to}", fromDate, toDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Opportunity A"))
                .andExpect(jsonPath("$[1].title").value("Opportunity B"));
    }



}
