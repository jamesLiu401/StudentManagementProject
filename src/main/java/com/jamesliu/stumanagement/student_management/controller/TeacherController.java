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
import com.jamesliu.stumanagement.student_management.dto.TeacherDTO;
import com.jamesliu.stumanagement.student_management.dto.TeacherDetailDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TeacherDTO> addTeacher(@RequestBody Teacher teacher) {
        try {
            Teacher savedTeacher = teacherService.saveTeacher(teacher);
            return ResponseMessage.success(DtoMapper.toDto(savedTeacher));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TeacherDTO> updateTeacher(
            @PathVariable Integer id, 
            @RequestBody Teacher teacher) {
        try {
            Teacher updatedTeacher = teacherService.updateTeacher(id, 
                teacher.getTeacherNo(), teacher.getTeacherName(), 
                teacher.getDepartment(), teacher.getTitle());
            return ResponseMessage.success(DtoMapper.toDto(updatedTeacher));
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
    public ResponseMessage<TeacherDetailDTO> getTeacherById(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherService.findByIdWithDetails(id);
        return teacher.map(t -> ResponseMessage.success(DtoMapper.toDetailDto(t)))
                .orElse(ResponseMessage.error("教师不存在"));
    }

    @GetMapping("/{id}/basic")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<TeacherDTO> getTeacherBasicById(@PathVariable Integer id) {
        Optional<Teacher> teacher = teacherService.findById(id);
        return teacher.map(t -> ResponseMessage.success(DtoMapper.toDto(t)))
                .orElse(ResponseMessage.error("教师不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TeacherDTO>> getAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
        return ResponseMessage.success(teachers.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<TeacherDTO>> getTeachersByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "teacherId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Teacher> teachers = teacherService.findAll(pageable);
        return ResponseMessage.success(teachers.map(DtoMapper::toDto));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TeacherDTO>> searchTeachersByName(
            @RequestParam String name) {
        List<Teacher> teachers = teacherService.searchTeachers(name);
        return ResponseMessage.success(teachers.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TeacherDTO>> getTeachersByDepartment(
            @PathVariable String department) {
        List<Teacher> teachers = teacherService.findByDepartment(department);
        return ResponseMessage.success(teachers.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TeacherDTO>> getTeachersByTitle(@PathVariable String title) {
        List<Teacher> teachers = teacherService.findByTitle(title);
        return ResponseMessage.success(teachers.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/department/{department}/title/{title}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TeacherDTO>> getTeachersByDepartmentAndTitle(
            @PathVariable String department,
            @PathVariable String title) {
        List<Teacher> teachers = teacherService.findByDepartmentAndTitle(department, title);
        return ResponseMessage.success(teachers.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/search/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<TeacherDTO>> searchTeachersByNamePage(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "teacherId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Teacher> teachers = teacherService.findByTeacherNameContaining(name, pageable);
        return ResponseMessage.success(teachers.map(DtoMapper::toDto));
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
