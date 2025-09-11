package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherRepository teacherRepository;

    public TeacherController(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Teacher> addTeacher(@RequestBody Teacher teacher) {
        // 检查教师编号是否已存在
        if (teacherRepository.findByTeacherNo(teacher.getTeacherNo()).isPresent()) {
            return ResponseMessage.error("教师编号已存在");
        }
        
        Teacher savedTeacher = teacherRepository.save(teacher);
        return ResponseMessage.success(savedTeacher);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Teacher> updateTeacher(
            @PathVariable Integer id, 
            @RequestBody Teacher teacher) {
        return teacherRepository.findById(id)
                .map(existingTeacher -> {
                    existingTeacher.setTeacherName(teacher.getTeacherName());
                    existingTeacher.setTeacherNo(teacher.getTeacherNo());
                    existingTeacher.setDepartment(teacher.getDepartment());
                    existingTeacher.setTitle(teacher.getTitle());
                    return teacherRepository.save(existingTeacher);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("教师不存在"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteTeacher(@PathVariable Integer id) {
        if (teacherRepository.existsById(id)) {
            teacherRepository.deleteById(id);
            return ResponseMessage.success("教师删除成功");
        }
        return ResponseMessage.error("教师不存在");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Teacher> getTeacherById(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        return teacher.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("教师不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Teacher>> getTeachersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "teacherId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Teacher> teachers = teacherRepository.findAll(pageable);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> searchTeachersByName(
            @RequestParam String name) {
        List<Teacher> teachers = teacherRepository.findByTeacherNameContaining(name);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getTeachersByDepartment(
            @PathVariable String department) {
        List<Teacher> teachers = teacherRepository.findByDepartment(department);
        return ResponseMessage.success(teachers);
    }
}
