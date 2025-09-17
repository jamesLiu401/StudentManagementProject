package com.jamesliu.stumanagement.student_management.Service.StudentService;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStudentService {
    Student addStudent(StudentDTO student);
    Student updateStudent(Integer id, StudentDTO student);
    void deleteStudent(Integer id);
    Student getStudentById(Integer id);
    List<Student> getAllStudents();
    Page<Student> getStudentsByPage(Pageable pageable);
    List<Student> searchStudentsByName(String name);
    ResponseMessage<List<Student>> batchImportFromCsv(MultipartFile file);
}
