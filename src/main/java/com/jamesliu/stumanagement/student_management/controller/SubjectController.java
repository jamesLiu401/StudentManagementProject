package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Service.AcademyService.AcademyService;
import com.jamesliu.stumanagement.student_management.dto.SubjectDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;
import com.jamesliu.stumanagement.student_management.Service.SubjectService.ISubjectService;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 课程管理控制器
 * 提供课程信息的增删改查功能
 * <p>
 * 主要功能：
 * 1. 课程信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 学院关联管理 - 支持按学院查询课程
 * 4. 学分管理 - 支持按学分查询和统计
 * 5. 多条件查询 - 支持学院、课程名称、学分范围组合查询
 * 6. 统计分析 - 课程数量统计和学分统计
 * <p>
 * 权限控制：
 * - 增删改操作：仅ADMIN
 * - 查询操作：ADMIN和TEACHER
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-13
 */
@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final ISubjectService subjectService;
    private final AcademyRepository academyRepository;
    private final AcademyService academyService;

    public SubjectController(ISubjectService subjectService, AcademyRepository academyRepository, AcademyService academyService) {
        this.subjectService = subjectService;
        this.academyRepository = academyRepository;
        this.academyService = academyService;
    }

    /**
     * 添加课程信息
     * 
     * @param subjectDTO 课程信息对象
     * @return 保存成功的课程信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Subject> addSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            Subject savedSubject = subjectService.createSubjectDTO(subjectDTO);
            return ResponseMessage.success(savedSubject);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 创建课程（通过参数）
     * 
     * @param subjectName 课程名称
     * @param academyId 学院ID
     * @param credit 学分
     * @return 创建的课程信息
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Subject> createSubject(
            @RequestParam String subjectName,
            @RequestParam Integer academyId,
            @RequestParam Double credit) {
        try {
            Optional<Academy> academy = academyRepository.findById(academyId);
            if (academy.isEmpty()) {
                return ResponseMessage.error("学院不存在");
            }
            SubjectDTO subjectDTO = new SubjectDTO();
            subjectDTO.setSubjectName(subjectName);
            subjectDTO.setCredit(credit);
            subjectDTO.setAcademyId(academyId);
            subjectDTO.setAcademyName(academy.get().getAcademyName());
            Subject subject = subjectService.createSubjectDTO(subjectDTO);
            return ResponseMessage.success(subject);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新课程信息
     * 
     * @param id 课程ID
     * @param subjectDTO 更新的课程信息
     * @return 更新后的课程信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Subject> updateSubject(
            @PathVariable Long id, 
            @RequestBody SubjectDTO subjectDTO) {
        try {
            // 需要根据academyId获取Academy实体
            Academy academy = null;
            Optional<Academy> academyOpt = academyService.findById(subjectDTO.getAcademyId());
            if (academyOpt.isEmpty()) {
                return ResponseMessage.error("学院不存在");
            }
            academy = academyOpt.get();
            Subject updatedSubject = subjectService.updateSubject(id,
                subjectDTO.getSubjectName(), academy, subjectDTO.getCredit());
            return ResponseMessage.success(updatedSubject);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除课程
     * 
     * @param id 课程ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteById(id);
        return ResponseMessage.success("课程删除成功");
    }

    /**
     * 根据ID查询课程
     * 
     * @param id 课程ID
     * @return 课程信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubjectDTO> getSubjectById(@PathVariable Long id) {
        Optional<SubjectDTO> subject = subjectService.findById(id);
        return subject.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("课程不存在"));
    }

    /**
     * 查询所有课程
     * 
     * @return 所有课程列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getAllSubjects() {
        List<SubjectDTO> subjects = subjectService.findAll();
        return ResponseMessage.success(subjects);
    }

    /**
     * 分页查询课程
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> getSubjectsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectDTO> subjects = subjectService.findAll(pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学院查询课程
     * 
     * @param academyId 学院ID
     * @return 该学院下的课程列表
     */
    @GetMapping("/academy/{academyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getSubjectsByAcademy(@PathVariable Integer academyId) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        List<SubjectDTO> subjects = subjectService.findByAcademy(academy.get());
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学院分页查询课程
     * 
     * @param academyId 学院ID
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/academy/{academyId}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> getSubjectsByAcademyPage(
            @PathVariable Integer academyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectDTO> subjects = subjectService.findByAcademy(academy.get(), pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据课程名称模糊查询
     * 
     * @param name 课程名称关键字
     * @return 匹配的课程列表
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> searchSubjectsByName(@RequestParam String name) {
        List<SubjectDTO> subjects = subjectService.findBySubjectNameContaining(name);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据课程名称模糊分页查询
     * 
     * @param name 课程名称关键字
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/search/name/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> searchSubjectsByNamePage(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectDTO> subjects = subjectService.findBySubjectNameContaining(name, pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据课程名称精确查询
     * 
     * @param subjectName 课程名称
     * @return 课程信息
     */
    @GetMapping("/name/{subjectName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubjectDTO> getSubjectByName(@PathVariable String subjectName) {
        Optional<SubjectDTO> subject = subjectService.findBySubjectName(subjectName);
        return subject.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("课程不存在"));
    }

    /**
     * 根据学院和课程名称查询
     * 
     * @param academyId 学院名称
     * @param subjectName 课程名称
     * @return 课程信息
     */
    @GetMapping("/academy/{academyId}/name/{subjectName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubjectDTO> getSubjectByAcademyAndName(
            @PathVariable Integer academyId,
            @PathVariable String subjectName) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        Optional<SubjectDTO> subject = subjectService.findByAcademyAndSubjectName(academy.get(), subjectName);
        return subject.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("课程不存在"));
    }

    /**
     * 根据学分查询课程
     * 
     * @param credit 学分
     * @return 该学分的课程列表
     */
    @GetMapping("/credit/{credit}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getSubjectsByCredit(@PathVariable Double credit) {
        List<SubjectDTO> subjects = subjectService.findByCredit(credit);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学分分页查询课程
     * 
     * @param credit 学分
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/credit/{credit}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> getSubjectsByCreditPage(
            @PathVariable Double credit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectDTO> subjects = subjectService.findByCredit(credit, pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学分范围查询课程
     * 
     * @param minCredit 最小学分
     * @param maxCredit 最大学分
     * @return 该学分范围的课程列表
     */
    @GetMapping("/credit-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getSubjectsByCreditRange(
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit) {
        List<SubjectDTO> subjects = subjectService.findByCreditBetween(minCredit, maxCredit);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学分范围分页查询课程
     * 
     * @param minCredit 最小学分
     * @param maxCredit 最大学分
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/credit-range/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> getSubjectsByCreditRangePage(
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubjectDTO> subjects = subjectService.findByCreditBetween(minCredit, maxCredit, pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学院和学分查询课程
     * 
     * @param academyId 学院名称
     * @param credit 学分
     * @return 该学院和学分的课程列表
     */
    @GetMapping("/academy/{academyId}/credit/{credit}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getSubjectsByAcademyAndCredit(
            @PathVariable Integer academyId,
            @PathVariable Double credit) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        List<SubjectDTO> subjects = subjectService.findByAcademyAndCredit(academy.get(), credit);
        return ResponseMessage.success(subjects);
    }

    /**
     * 根据学院和学分范围查询课程
     * 
     * @param academyId 学院名称
     * @param minCredit 最小学分
     * @param maxCredit 最大学分
     * @return 该学院和学分范围的课程列表
     */
    @GetMapping("/academy/{academyId}/credit-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> getSubjectsByAcademyAndCreditRange(
            @PathVariable Integer academyId,
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        List<SubjectDTO> subjects = subjectService.findByAcademyAndCreditBetween(academy.get(), minCredit, maxCredit);
        return ResponseMessage.success(subjects);
    }

    /**
     * 多条件查询课程
     * 
     * @param academyId 学院名称（可选）
     * @param subjectName 课程名称关键字（可选）
     * @param minCredit 最小学分（可选）
     * @param maxCredit 最大学分（可选）
     * @return 匹配的课程列表
     */
    @GetMapping("/search/multiple")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> searchSubjectsByMultipleConditions(
            @RequestParam(required = false) Integer academyId,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) Double minCredit,
            @RequestParam(required = false) Double maxCredit) {
        Academy academy = null;
        if (academyId != null) {
            Optional<Academy> academyOpt = academyRepository.findById(academyId);
            if (academyOpt.isEmpty()) {
                return ResponseMessage.error("学院不存在");
            }
            academy = academyOpt.get();
        }
        List<SubjectDTO> subjects = subjectService.findByMultipleConditions(academy, subjectName, minCredit, maxCredit);
        return ResponseMessage.success(subjects);
    }

    /**
     * 多条件分页查询课程
     * 
     * @param academyId 学院名称（可选）
     * @param subjectName 课程名称关键字（可选）
     * @param minCredit 最小学分（可选）
     * @param maxCredit 最大学分（可选）
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的课程列表
     */
    @GetMapping("/search/multiple/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubjectDTO>> searchSubjectsByMultipleConditionsPage(
            @RequestParam(required = false) Integer academyId,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) Double minCredit,
            @RequestParam(required = false) Double maxCredit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Academy academy = null;
        if (academyId != null) {
            Optional<Academy> academyOpt = academyRepository.findById(academyId);
            if (academyOpt.isEmpty()) {
                return ResponseMessage.error("学院不存在");
            }
            academy = academyOpt.get();
        }
        Page<SubjectDTO> subjects = subjectService.findByMultipleConditions(academy, subjectName, minCredit, maxCredit, pageable);
        return ResponseMessage.success(subjects);
    }

    /**
     * 搜索课程（通用搜索）
     * 
     * @param keyword 搜索关键字
     * @return 匹配的课程列表
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubjectDTO>> searchSubjects(@RequestParam String keyword) {
        List<SubjectDTO> subjects = subjectService.searchSubjects(keyword);
        return ResponseMessage.success(subjects);
    }

    // 统计相关API
    @GetMapping("/academy/{academyId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countSubjectsByAcademy(@PathVariable Integer academyId) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        long count = subjectService.countByAcademy(academy.get());
        return ResponseMessage.success(count);
    }

    @GetMapping("/credit/{credit}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countSubjectsByCredit(@PathVariable Double credit) {
        long count = subjectService.countByCredit(credit);
        return ResponseMessage.success(count);
    }

    @GetMapping("/credit-range/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countSubjectsByCreditRange(
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit) {
        long count = subjectService.countByCreditBetween(minCredit, maxCredit);
        return ResponseMessage.success(count);
    }

    @GetMapping("/academy/{academyId}/credit-range/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countSubjectsByAcademyAndCreditRange(
            @PathVariable Integer academyId,
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        long count = subjectService.countByAcademyAndCreditBetween(academy.get(), minCredit, maxCredit);
        return ResponseMessage.success(count);
    }

    // 聚合操作API
    @GetMapping("/academy/{academyId}/total-credit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Double> getTotalCreditByAcademy(@PathVariable Integer academyId) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        Double totalCredit = subjectService.sumCreditByAcademy(academy.get());
        return ResponseMessage.success(totalCredit);
    }

    @GetMapping("/academies")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Academy>> getAllAcademies() {
        List<Academy> academies = subjectService.findAllAcademies();
        return ResponseMessage.success(academies);
    }

    @GetMapping("/academy/{academyId}/credits")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Double>> getCreditsByAcademy(@PathVariable Integer academyId) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        List<Double> credits = subjectService.findCreditsByAcademy(academy.get());
        return ResponseMessage.success(credits);
    }

    // 业务逻辑API
    @GetMapping("/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Boolean> checkSubjectExists(
            @RequestParam Integer academyId,
            @RequestParam String subjectName) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            return ResponseMessage.error("学院不存在");
        }
        boolean exists = subjectService.existsByAcademyAndSubjectName(academy.get(), subjectName);
        return ResponseMessage.success(exists);
    }
}