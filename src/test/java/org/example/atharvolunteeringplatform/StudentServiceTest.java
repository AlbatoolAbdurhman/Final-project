package org.example.atharvolunteeringplatform;

import org.example.atharvolunteeringplatform.DTO.StudentDTO;
import org.example.atharvolunteeringplatform.Model.MyUser;
import org.example.atharvolunteeringplatform.Model.School;
import org.example.atharvolunteeringplatform.Model.Student;
import org.example.atharvolunteeringplatform.Repository.*;
import org.example.atharvolunteeringplatform.Service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @InjectMocks
    StudentService studentService;

    @Mock
    StudentRepository studentRepository;

    @Mock
    MyUserRepository myUserRepository;

    @Mock
    SchoolRepository schoolRepository;

    @Mock
    OpportunityRepository opportunityRepository;

    @Mock
    StudentOpportunityRequestRepository studentOpportunityRequestRepository;

    Student student;
    MyUser user;
    School school;
    StudentDTO studentDTO;
    List<School> schools;

    @BeforeEach
    void setUp() {
        user = new MyUser();
        user.setId(1);
        user.setName("Ali");
        user.setUsername("ali123");
        user.setEmail("ali@example.com");
        user.setPhone_number("1234567890");
        user.setPassword("password");
        user.setRole("student");

        school = new School();
        school.setId(1);
        school.setName("Future School");
        school.setStatus("Active");
        school.setGender("male");

        student = new Student();
        student.setId(1);
        student.setName("Ali");
        student.setAge(16);
        student.setGrade_level("10");
        student.setGender("male");
        student.setSchool(school);
        student.setStatus("Inactive");
        student.setUserStudent(user);

        studentDTO = new StudentDTO();
        studentDTO.setName("Ali");
        studentDTO.setUsername("ali123");
        studentDTO.setPassword("password");
        studentDTO.setEmail("ali@example.com");
        studentDTO.setPhone_number("1234567890");
        studentDTO.setSchool_name("Future School");
        studentDTO.setAge(16);
        studentDTO.setGrade_level("10");
        studentDTO.setGender("male");

        schools = new ArrayList<>();
        schools.add(school);
    }

    @Test
    public void testGetAllStudents() {
        List<Student> studentList = new ArrayList<>();
        studentList.add(student);

        when(studentRepository.findAll()).thenReturn(studentList);

        List<Student> result = studentService.getAllStudents();
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    public void testAddStudent() {
        when(schoolRepository.findAll()).thenReturn(schools);

        studentService.addStudent(studentDTO);

        verify(myUserRepository, times(1)).save(any(MyUser.class));
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void testUpdateStudent() {
        when(myUserRepository.findMyUserById(1)).thenReturn(user);
        when(studentRepository.findStudentById(1)).thenReturn(student);
        when(schoolRepository.findAll()).thenReturn(schools);

        studentDTO.setStatus("Active");
        studentService.updateStudent(1, studentDTO);

        verify(myUserRepository, times(1)).save(user);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void testDeleteStudent() {
        when(myUserRepository.findMyUserById(1)).thenReturn(user);
        when(studentRepository.findStudentById(1)).thenReturn(student);

        studentService.deleteStudent(1);

        verify(studentRepository, times(1)).delete(student);
        verify(myUserRepository, times(1)).delete(user);
    }

    @Test
    public void testGetStudentHoursSummary() {
        student.setTotal_hours(25);
        when(studentRepository.findStudentById(1)).thenReturn(student);

        Map<String, Integer> summary = studentService.getStudentHoursSummary(1);

        assertEquals(25, summary.get("completed_hours"));
        assertEquals(15, summary.get("remaining_hours"));
        verify(studentRepository, times(1)).findStudentById(1);
    }
}
