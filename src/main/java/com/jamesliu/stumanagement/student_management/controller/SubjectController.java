package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Service.SubjectService.ISubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 课程管理控制器
 * 提供课程相关的 REST API 接口，支持完整的课程管理功能
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>课程CRUD操作 - 创建、查询、更新、删除课程</li>
 *   <li>条件查询接口 - 支持多条件组合查询</li>
 *   <li>分页查询接口 - 支持分页和排序</li>
 *   <li>统计查询接口 - 课程数量统计和学分统计</li>
 *   <li>搜索功能 - 支持课程名称模糊搜索</li>
 *   <li>学院关联查询 - 按学院查询课程</li>
 * </ul>
 * 
 * <p>权限控制：</p>
 * <ul>
 *   <li>管理员权限 - 创建、更新、删除课程</li>
 *   <li>教师权限 - 查询、搜索、统计课程</li>
 *   <li>使用@PreAuthorize注解进行方法级权限控制</li>
 * </ul>
 * 
 * <p>API设计：</p>
 * <ul>
 *   <li>RESTful风格 - 遵循REST设计原则</li>
 *   <li>统一响应格式 - 使用ResponseMessage封装响应</li>
 *   <li>异常处理 - 统一的错误处理机制</li>
 *   <li>跨域支持 - 支持跨域访问</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-01
 */
@RestController
@RequestMapping("/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {
    
    @Autowired
    private ISubjectService subjectService;
    
    /**
     * 创建新课程
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseMessage<Subject>> createSubject(@RequestBody Subject subject) {
        try {
            Subject savedSubject = subjectService.saveSubject(subject);
            return ResponseEntity.ok(ResponseMessage.success("课程创建成功", savedSubject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseMessage.error("课程创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage<Subject>> getSubjectById(@PathVariable Long id) {
        Optional<Subject> subject = subjectService.findById(id);
        if (subject.isPresent()) {
            return ResponseEntity.ok(ResponseMessage.success("获取课程成功", subject.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取所有课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<ResponseMessage<List<Subject>>> getAllSubjects() {
        List<Subject> subjects = subjectService.findAll();
        return ResponseEntity.ok(ResponseMessage.success("获取课程列表成功", subjects));
    }
    
    /**
     * 分页获取课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/page")
    public ResponseEntity<ResponseMessage<Page<Subject>>> getSubjectsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Subject> subjects = subjectService.findAll(pageable);
        return ResponseEntity.ok(ResponseMessage.success("获取课程分页数据成功", subjects));
    }
    
    /**
     * 根据学院获取课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/academy/{academy}")
    public ResponseEntity<ResponseMessage<List<Subject>>> getSubjectsByAcademy(@PathVariable String academy) {
        List<Subject> subjects = subjectService.findByAcademy(academy);
        return ResponseEntity.ok(ResponseMessage.success("获取学院课程成功", subjects));
    }
    
    /**
     * 根据学院分页获取课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/academy/{academy}/page")
    public ResponseEntity<ResponseMessage<Page<Subject>>> getSubjectsByAcademyPage(
            @PathVariable String academy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Subject> subjects = subjectService.findByAcademy(academy, pageable);
        return ResponseEntity.ok(ResponseMessage.success("获取学院课程分页数据成功", subjects));
    }
    
    /**
     * 根据课程名称搜索
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/search")
    public ResponseEntity<ResponseMessage<List<Subject>>> searchSubjects(@RequestParam String keyword) {
        List<Subject> subjects = subjectService.searchSubjects(keyword);
        return ResponseEntity.ok(ResponseMessage.success("搜索课程成功", subjects));
    }
    
    /**
     * 根据课程名称模糊分页搜索
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/search/page")
    public ResponseEntity<ResponseMessage<Page<Subject>>> searchSubjectsPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Subject> subjects = subjectService.findBySubjectNameContaining(keyword, pageable);
        return ResponseEntity.ok(ResponseMessage.success("搜索课程分页数据成功", subjects));
    }
    
    /**
     * 根据学分查询课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/credit/{credit}")
    public ResponseEntity<ResponseMessage<List<Subject>>> getSubjectsByCredit(@PathVariable Double credit) {
        List<Subject> subjects = subjectService.findByCredit(credit);
        return ResponseEntity.ok(ResponseMessage.success("获取指定学分课程成功", subjects));
    }
    
    /**
     * 根据学分范围查询课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/credit/range")
    public ResponseEntity<ResponseMessage<List<Subject>>> getSubjectsByCreditRange(
            @RequestParam Double minCredit,
            @RequestParam Double maxCredit) {
        List<Subject> subjects = subjectService.findByCreditBetween(minCredit, maxCredit);
        return ResponseEntity.ok(ResponseMessage.success("获取学分范围课程成功", subjects));
    }
    
    /**
     * 多条件查询课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/filter")
    public ResponseEntity<ResponseMessage<List<Subject>>> filterSubjects(
            @RequestParam(required = false) String academy,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) Double minCredit,
            @RequestParam(required = false) Double maxCredit) {
        
        List<Subject> subjects = subjectService.findByMultipleConditions(academy, subjectName, minCredit, maxCredit);
        return ResponseEntity.ok(ResponseMessage.success("多条件查询课程成功", subjects));
    }
    
    /**
     * 多条件分页查询课程
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/filter/page")
    public ResponseEntity<ResponseMessage<Page<Subject>>> filterSubjectsPage(
            @RequestParam(required = false) String academy,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) Double minCredit,
            @RequestParam(required = false) Double maxCredit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subjectId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Subject> subjects = subjectService.findByMultipleConditions(academy, subjectName, minCredit, maxCredit, pageable);
        return ResponseEntity.ok(ResponseMessage.success("多条件分页查询课程成功", subjects));
    }
    
    /**
     * 更新课程
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage<Subject>> updateSubject(
            @PathVariable Long id,
            @RequestBody Subject subject) {
        try {
            Subject updatedSubject = subjectService.updateSubject(id, subject.getSubjectName(), 
                subject.getSubjectAcademy(), subject.getCredit());
            return ResponseEntity.ok(ResponseMessage.success("课程更新成功", updatedSubject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseMessage.error("课程更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除课程
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage<Void>> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteById(id);
            return ResponseEntity.ok(ResponseMessage.success("课程删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseMessage.error("课程删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有学院列表
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/academies")
    public ResponseEntity<ResponseMessage<List<String>>> getAllAcademies() {
        List<String> academies = subjectService.findAllAcademies();
        return ResponseEntity.ok(ResponseMessage.success("获取学院列表成功", academies));
    }
    
    /**
     * 获取指定学院的所有学分列表
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/academy/{academy}/credits")
    public ResponseEntity<ResponseMessage<List<Double>>> getCreditsByAcademy(@PathVariable String academy) {
        List<Double> credits = subjectService.findCreditsByAcademy(academy);
        return ResponseEntity.ok(ResponseMessage.success("获取学院学分列表成功", credits));
    }
    
    /**
     * 计算学院总学分
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/academy/{academy}/total-credit")
    public ResponseEntity<ResponseMessage<Double>> getTotalCreditByAcademy(@PathVariable String academy) {
        Double totalCredit = subjectService.sumCreditByAcademy(academy);
        return ResponseEntity.ok(ResponseMessage.success("获取学院总学分成功", totalCredit));
    }
    
    /**
     * 统计课程数量
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/count")
    public ResponseEntity<ResponseMessage<Long>> getSubjectCount(
            @RequestParam(required = false) String academy,
            @RequestParam(required = false) Double credit,
            @RequestParam(required = false) Double minCredit,
            @RequestParam(required = false) Double maxCredit) {
        
        long count;
        if (academy != null && minCredit != null && maxCredit != null) {
            count = subjectService.countByAcademyAndCreditBetween(academy, minCredit, maxCredit);
        } else if (credit != null) {
            count = subjectService.countByCredit(credit);
        } else if (minCredit != null && maxCredit != null) {
            count = subjectService.countByCreditBetween(minCredit, maxCredit);
        } else if (academy != null) {
            count = subjectService.countByAcademy(academy);
        } else {
            count = (long) subjectService.findAll().size();
        }
        
        return ResponseEntity.ok(ResponseMessage.success("获取课程数量成功", count));
    }
    
    /**
     * 创建课程（业务方法）
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage<Subject>> createSubjectWithValidation(
            @RequestParam String subjectName,
            @RequestParam String academy,
            @RequestParam Double credit) {
        try {
            Subject subject = subjectService.createSubject(subjectName, academy, credit);
            return ResponseEntity.ok(ResponseMessage.success("课程创建成功", subject));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseMessage.error("课程创建失败: " + e.getMessage()));
        }
    }
}
