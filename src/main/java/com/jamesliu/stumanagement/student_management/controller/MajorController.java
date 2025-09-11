package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.MajorRepository;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
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

    private final MajorRepository majorRepository;
    private final AcademyRepository academyRepository;
    private final TeacherRepository teacherRepository;

    public MajorController(MajorRepository majorRepository,
                          AcademyRepository academyRepository,
                          TeacherRepository teacherRepository) {
        this.majorRepository = majorRepository;
        this.academyRepository = academyRepository;
        this.teacherRepository = teacherRepository;
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
        // 检查学院是否存在
        if (major.getAcademy() == null || major.getAcademy().getAcademyId() == null) {
            return ResponseMessage.error("学院信息不能为空");
        }
        
        Optional<Academy> academy = academyRepository.findById(major.getAcademy().getAcademyId());
        if (!academy.isPresent()) {
            return ResponseMessage.error("学院不存在");
        }
        
        // 检查专业名称和年级是否已存在
        if (majorRepository.findByMajorNameAndGrade(major.getMajorName(), major.getGrade()).isPresent()) {
            return ResponseMessage.error("该年级的专业已存在");
        }
        
        // 检查辅导员是否存在
        if (major.getCounselor() != null && major.getCounselor().getTeacherId() != null) {
            Optional<Teacher> counselor = teacherRepository.findById(major.getCounselor().getTeacherId());
            if (!counselor.isPresent()) {
                return ResponseMessage.error("辅导员不存在");
            }
            major.setCounselor(counselor.get());
        }
        
        major.setAcademy(academy.get());
        Major savedMajor = majorRepository.save(major);
        return ResponseMessage.success(savedMajor);
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
        return majorRepository.findById(id)
                .map(existingMajor -> {
                    // 检查学院是否存在
                    if (major.getAcademy() != null && major.getAcademy().getAcademyId() != null) {
                        Optional<Academy> academy = academyRepository.findById(major.getAcademy().getAcademyId());
                        if (!academy.isPresent()) {
                            throw new RuntimeException("学院不存在");
                        }
                        existingMajor.setAcademy(academy.get());
                    }
                    
                    // 检查专业名称和年级是否与其他专业冲突
                    if (major.getMajorName() != null && major.getGrade() != null) {
                        Optional<Major> conflict = majorRepository.findByMajorNameAndGrade(major.getMajorName(), major.getGrade());
                        if (conflict.isPresent() && !conflict.get().getMajorId().equals(id)) {
                            throw new RuntimeException("该年级的专业已存在");
                        }
                    }
                    
                    // 检查辅导员是否存在
                    if (major.getCounselor() != null && major.getCounselor().getTeacherId() != null) {
                        Optional<Teacher> counselor = teacherRepository.findById(major.getCounselor().getTeacherId());
                        if (!counselor.isPresent()) {
                            throw new RuntimeException("辅导员不存在");
                        }
                        existingMajor.setCounselor(counselor.get());
                    } else {
                        existingMajor.setCounselor(null);
                    }
                    
                    if (major.getMajorName() != null) {
                        existingMajor.setMajorName(major.getMajorName());
                    }
                    if (major.getGrade() != null) {
                        existingMajor.setGrade(major.getGrade());
                    }
                    
                    return majorRepository.save(existingMajor);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("专业不存在"));
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
        if (majorRepository.existsById(id)) {
            majorRepository.deleteById(id);
            return ResponseMessage.success("专业删除成功");
        }
        return ResponseMessage.error("专业不存在");
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
        Optional<Major> major = majorRepository.findById(id);
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
        List<Major> majors = majorRepository.findAll();
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
        Page<Major> majors = majorRepository.findAll(pageable);
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
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (!academy.isPresent()) {
            return ResponseMessage.error("学院不存在");
        }
        
        List<Major> majors = majorRepository.findByAcademy(academy.get());
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
        List<Major> majors = majorRepository.findByGrade(grade);
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
        List<Major> majors = majorRepository.findByMajorName(majorName);
        return ResponseMessage.success(majors);
    }
}