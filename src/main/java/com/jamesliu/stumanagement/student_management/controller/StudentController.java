package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Service.StudentService.IStudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Student> addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        return ResponseMessage.success(savedStudent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Student> updateStudent(
            @PathVariable Integer id, 
            @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseMessage.success(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseMessage.success("学生删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Student> getStudentById(@PathVariable Integer id) {
        Student student = studentService.getStudentById(id);
        return ResponseMessage.success(student);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseMessage.success(students);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Student>> getStudentsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stuId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> students = studentService.getStudentsByPage(pageable);
        return ResponseMessage.success(students);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Student>> searchStudentsByName(
            @RequestParam String name) {
        List<Student> students = studentService.searchStudentsByName(name);
        return ResponseMessage.success(students);
    }

    @PostMapping("/batch/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<List<Student>> batchImportFromCsv(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseMessage.error("请选择要上传的文件");
        }
        
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseMessage.error("请上传CSV格式的文件");
        }
        
        return studentService.batchImportFromCsv(file);
    }
}
