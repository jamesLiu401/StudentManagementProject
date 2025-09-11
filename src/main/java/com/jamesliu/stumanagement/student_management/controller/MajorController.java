package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.Service.MajorService.IMajorService;
import com.jamesliu.stumanagement.student_management.Service.AcademyService.IAcademyService;
import com.jamesliu.stumanagement.student_management.Service.TeacherService.ITeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 专业管理控制器
 * 提供专业信息的增删改查功能
 * 
 * 主要功能：
 * 1. 专业信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 学院关联管理 - 支持按学院查询专业
 * 4. 辅导员关联管理 - 支持辅导员的分配和管理
 * 
 * 权限控制：
 * - 增删改操作：仅ADMIN
 * - 查询操作：ADMIN和TEACHER
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/majors")
public class MajorController {

    private final IMajorService majorService;
    private final IAcademyService academyService;
    private final ITeacherService teacherService;

    public MajorController(IMajorService majorService,
                          IAcademyService academyService,
                          ITeacherService teacherService) {
        this.majorService = majorService;
        this.academyService = academyService;
        this.teacherService = teacherService;
    }

    /**
     * 添加专业信息
     * 
     * @param major 专业信息对象
     * @return 保存成功的专业信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Major> addMajor(@RequestBody Major major) {
        try {
            Major savedMajor = majorService.saveMajor(major);
            return ResponseMessage.success(savedMajor);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新专业信息
     * 
     * @param id 专业ID
     * @param major 更新的专业信息
     * @return 更新后的专业信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Major> updateMajor(
            @PathVariable Integer id, 
            @RequestBody Major major) {
        try {
            Major updatedMajor = majorService.updateMajor(id, major.getMajorName(), 
                major.getAcademy(), major.getGrade(), major.getCounselor());
            return ResponseMessage.success(updatedMajor);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除专业
     * 
     * @param id 专业ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteMajor(@PathVariable Integer id) {
        majorService.deleteById(id);
        return ResponseMessage.success("专业删除成功");
    }

    /**
     * 根据ID查询专业
     * 
     * @param id 专业ID
     * @return 专业信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Major> getMajorById(@PathVariable Integer id) {
        Optional<Major> major = majorService.findById(id);
        return major.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("专业不存在"));
    }

    /**
     * 查询所有专业
     * 
     * @return 所有专业列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Major>> getAllMajors() {
        List<Major> majors = majorService.findAll();
        return ResponseMessage.success(majors);
    }

    /**
     * 分页查询专业
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的专业列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Major>> getMajorsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Major> majors = majorService.findAll(pageable);
        return ResponseMessage.success(majors);
    }

    /**
     * 根据学院查询专业
     * 
     * @param academyId 学院ID
     * @return 该学院下的专业列表
     */
    @GetMapping("/academy/{academyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Major>> getMajorsByAcademy(@PathVariable Integer academyId) {
        List<Major> majors = majorService.getMajorsByAcademyId(academyId);
        return ResponseMessage.success(majors);
    }

    /**
     * 根据年级查询专业
     * 
     * @param grade 年级
     * @return 该年级的专业列表
     */
    @GetMapping("/grade/{grade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Major>> getMajorsByGrade(@PathVariable Integer grade) {
        List<Major> majors = majorService.getMajorsByGrade(grade);
        return ResponseMessage.success(majors);
    }

    /**
     * 根据专业名称查询
     * 
     * @param majorName 专业名称
     * @return 匹配的专业列表
     */
    @GetMapping("/name/{majorName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Major>> getMajorsByName(@PathVariable String majorName) {
        List<Major> majors = majorService.searchMajors(majorName);
        return ResponseMessage.success(majors);
    }
}