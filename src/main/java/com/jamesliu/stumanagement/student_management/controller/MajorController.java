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
import com.jamesliu.stumanagement.student_management.dto.MajorDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 专业管理控制器
 * 提供专业信息的增删改查功能
 * <p>
 * 主要功能：
 * 1. 专业信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 学院关联管理 - 支持按学院查询专业
 * 4. 辅导员关联管理 - 支持辅导员的分配和管理
 * <p>
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
    public ResponseMessage<MajorDTO> addMajor(@RequestBody Major major) {
        try {
            Major savedMajor = majorService.saveMajor(major);
            return ResponseMessage.success(DtoMapper.toDto(savedMajor));
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
    public ResponseMessage<MajorDTO> updateMajor(
            @PathVariable Integer id, 
            @RequestBody Major major) {
        try {
            Major updatedMajor = majorService.updateMajor(id, major.getMajorName(), 
                major.getAcademy(), major.getGrade(), major.getCounselor());
            return ResponseMessage.success(DtoMapper.toDto(updatedMajor));
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
    public ResponseMessage<MajorDTO> getMajorById(@PathVariable Integer id) {
        Optional<MajorDTO> major = majorService.findById(id);
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
    public ResponseMessage<List<MajorDTO>> getAllMajors() {
        List<MajorDTO> majors = majorService.findAll();
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
    public ResponseMessage<Page<MajorDTO>> getMajorsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MajorDTO> majors = majorService.findAll(pageable);
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
    public ResponseMessage<List<MajorDTO>> getMajorsByAcademy(@PathVariable Integer academyId) {
        List<MajorDTO> majors = majorService.getMajorsByAcademyId(academyId);
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
    public ResponseMessage<List<MajorDTO>> getMajorsByGrade(@PathVariable Integer grade) {
        List<MajorDTO> majors = majorService.getMajorsByGrade(grade);
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
    public ResponseMessage<List<MajorDTO>> getMajorsByName(@PathVariable String majorName) {
        List<MajorDTO> majors = majorService.searchMajors(majorName);
        return ResponseMessage.success(majors);
    }

    @GetMapping("/grade/{grade}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<MajorDTO>> getMajorsByGradePage(
            @PathVariable Integer grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MajorDTO> majors = majorService.findByGrade(grade, pageable);
        return ResponseMessage.success(majors);
    }

    @GetMapping("/academy/{academyId}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<MajorDTO>> getMajorsByAcademyPage(
            @PathVariable Integer academyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            var academy = academyService.findById(academyId)
                    .orElseThrow(() -> new IllegalArgumentException("学院不存在"));
            Page<MajorDTO> majors = majorService.findByAcademy(academy, pageable);
            return ResponseMessage.success(majors);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/counselor/{counselorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<MajorDTO>> getMajorsByCounselor(@PathVariable Integer counselorId) {
        try {
            var counselor = teacherService.findById(counselorId)
                    .orElseThrow(() -> new IllegalArgumentException("辅导员不存在"));
            List<MajorDTO> majors = majorService.findByCounselor(counselor);
            return ResponseMessage.success(majors);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/counselor/{counselorId}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<MajorDTO>> getMajorsByCounselorPage(
            @PathVariable Integer counselorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        try {
            var counselor = teacherService.findById(counselorId)
                    .orElseThrow(() -> new IllegalArgumentException("辅导员不存在"));
            Page<MajorDTO> majors = majorService.findByCounselor(counselor, pageable);
            return ResponseMessage.success(majors);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/academy/{academyId}/grade/{grade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<MajorDTO>> getMajorsByAcademyAndGrade(
            @PathVariable Integer academyId,
            @PathVariable Integer grade) {
        try {
            var academy = academyService.findById(academyId)
                    .orElseThrow(() -> new IllegalArgumentException("学院不存在"));
            List<MajorDTO> majors = majorService.findByAcademyAndGrade(academy, grade);
            return ResponseMessage.success(majors);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/name/{majorName}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<MajorDTO>> getMajorsByNamePage(
            @PathVariable String majorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "majorId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MajorDTO> majors = majorService.findByMajorNameContaining(majorName, pageable);
        return ResponseMessage.success(majors);
    }

    // 统计相关API
    @GetMapping("/counselor/{counselorId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countMajorsByCounselor(@PathVariable Integer counselorId) {
        try {
            var counselor = teacherService.findById(counselorId)
                    .orElseThrow(() -> new IllegalArgumentException("辅导员不存在"));
            long count = majorService.countByCounselor(counselor);
            return ResponseMessage.success(count);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/academy/{academyId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countMajorsByAcademy(@PathVariable Integer academyId) {
        try {
            var academy = academyService.findById(academyId)
                    .orElseThrow(() -> new IllegalArgumentException("学院不存在"));
            long count = majorService.countByAcademy(academy);
            return ResponseMessage.success(count);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/grade/{grade}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countMajorsByGrade(@PathVariable Integer grade) {
        long count = majorService.countByGrade(grade);
        return ResponseMessage.success(count);
    }

    @GetMapping("/academy/{academyId}/grade/{grade}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countMajorsByAcademyAndGrade(
            @PathVariable Integer academyId,
            @PathVariable Integer grade) {
        try {
            var academy = academyService.findById(academyId)
                    .orElseThrow(() -> new IllegalArgumentException("学院不存在"));
            long count = majorService.countByAcademyAndGrade(academy, grade);
            return ResponseMessage.success(count);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
}