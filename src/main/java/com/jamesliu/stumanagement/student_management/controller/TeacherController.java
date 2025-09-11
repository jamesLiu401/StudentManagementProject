package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.Service.TeacherService.ITeacherService;
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

    private final ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Teacher> addTeacher(@RequestBody Teacher teacher) {
        try {
            Teacher savedTeacher = teacherService.saveTeacher(teacher);
            return ResponseMessage.success(savedTeacher);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Teacher> updateTeacher(
            @PathVariable Integer id, 
            @RequestBody Teacher teacher) {
        try {
            Teacher updatedTeacher = teacherService.updateTeacher(id, 
                teacher.getTeacherNo(), teacher.getTeacherName(), 
                teacher.getDepartment(), teacher.getTitle());
            return ResponseMessage.success(updatedTeacher);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteById(id);
        return ResponseMessage.success("教师删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Teacher> getTeacherById(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherService.findById(id);
        return teacher.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("教师不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
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
        Page<Teacher> teachers = teacherService.findAll(pageable);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> searchTeachersByName(
            @RequestParam String name) {
        List<Teacher> teachers = teacherService.searchTeachers(name);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getTeachersByDepartment(
            @PathVariable String department) {
        List<Teacher> teachers = teacherService.findByDepartment(department);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getTeachersByTitle(@PathVariable String title) {
        List<Teacher> teachers = teacherService.findByTitle(title);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/department/{department}/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Teacher>> getTeachersByDepartmentAndTitle(
            @PathVariable String department,
            @PathVariable String title) {
        List<Teacher> teachers = teacherService.findByDepartmentAndTitle(department, title);
        return ResponseMessage.success(teachers);
    }

    @GetMapping("/search/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Teacher>> searchTeachersByNamePage(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "teacherId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Teacher> teachers = teacherService.findByTeacherNameContaining(name, pageable);
        return ResponseMessage.success(teachers);
    }

    // 统计相关API
    @GetMapping("/department/{department}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countTeachersByDepartment(@PathVariable String department) {
        long count = teacherService.countByDepartment(department);
        return ResponseMessage.success(count);
    }

    @GetMapping("/title/{title}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countTeachersByTitle(@PathVariable String title) {
        long count = teacherService.countByTitle(title);
        return ResponseMessage.success(count);
    }

    @GetMapping("/department/{department}/title/{title}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countTeachersByDepartmentAndTitle(
            @PathVariable String department,
            @PathVariable String title) {
        long count = teacherService.countByDepartmentAndTitle(department, title);
        return ResponseMessage.success(count);
    }
}
