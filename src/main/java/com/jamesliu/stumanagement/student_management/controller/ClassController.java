package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import com.jamesliu.stumanagement.student_management.Service.ClassService.IClassService;
import com.jamesliu.stumanagement.student_management.Service.MajorService.IMajorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 班级管理控制器
 * 提供大班和小班的管理功能
 * 
 * 主要功能：
 * 1. 大班管理 - TotalClass的增删改查
 * 2. 小班管理 - SubClass的增删改查
 * 3. 分页查询 - 支持排序和筛选
 * 4. 专业关联管理 - 支持按专业查询班级
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
@RequestMapping("/classes")
public class ClassController {

    private final IClassService classService;
    private final IMajorService majorService;

    public ClassController(IClassService classService,
                          IMajorService majorService) {
        this.classService = classService;
        this.majorService = majorService;
    }

    // ==================== 大班管理 ====================

    /**
     * 添加大班
     */
    @PostMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> addTotalClass(@RequestBody TotalClass totalClass) {
        try {
            TotalClass savedTotalClass = classService.saveTotalClass(totalClass);
            return ResponseMessage.success(savedTotalClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新大班信息
     */
    @PutMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> updateTotalClass(
            @PathVariable Integer id, 
            @RequestBody TotalClass totalClass) {
        try {
            TotalClass existingTotalClass = classService.findTotalClassById(id)
                    .orElseThrow(() -> new IllegalArgumentException("大班不存在"));
            
            existingTotalClass.setTotalClassName(totalClass.getTotalClassName());
            if (totalClass.getMajor() != null) {
                existingTotalClass.setMajor(totalClass.getMajor());
            }
            
            TotalClass updatedTotalClass = classService.saveTotalClass(existingTotalClass);
            return ResponseMessage.success(updatedTotalClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除大班
     */
    @DeleteMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteTotalClass(@PathVariable Integer id) {
        classService.deleteTotalClassById(id);
        return ResponseMessage.success("大班删除成功");
    }

    /**
     * 根据ID查询大班
     */
    @GetMapping("/total/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<TotalClass> getTotalClassById(@PathVariable Integer id) {
        Optional<TotalClass> totalClass = classService.findTotalClassById(id);
        return totalClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("大班不存在"));
    }

    /**
     * 查询所有大班
     */
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getAllTotalClasses() {
        List<TotalClass> totalClasses = classService.findAllTotalClasses();
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 分页查询大班
     */
    @GetMapping("/total/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<TotalClass>> getTotalClassesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "totalClassId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TotalClass> totalClasses = classService.findAllTotalClasses(pageable);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 根据专业查询大班
     */
    @GetMapping("/total/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getTotalClassesByMajor(@PathVariable Integer majorId) {
        List<TotalClass> totalClasses = classService.findTotalClassesByMajorId(majorId);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 根据大班名称查询
     */
    @GetMapping("/total/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> searchTotalClasses(@RequestParam String name) {
        List<TotalClass> totalClasses = classService.searchTotalClasses(name);
        return ResponseMessage.success(totalClasses);
    }

    // ==================== 小班管理 ====================

    /**
     * 添加小班
     */
    @PostMapping("/sub")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> addSubClass(@RequestBody SubClass subClass) {
        try {
            SubClass savedSubClass = classService.saveSubClass(subClass);
            return ResponseMessage.success(savedSubClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新小班信息
     */
    @PutMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> updateSubClass(
            @PathVariable Integer id, 
            @RequestBody SubClass subClass) {
        try {
            SubClass existingSubClass = classService.findSubClassById(id)
                    .orElseThrow(() -> new IllegalArgumentException("小班不存在"));
            
            existingSubClass.setSubClassName(subClass.getSubClassName());
            if (subClass.getTotalClass() != null) {
                existingSubClass.setTotalClass(subClass.getTotalClass());
            }
            
            SubClass updatedSubClass = classService.saveSubClass(existingSubClass);
            return ResponseMessage.success(updatedSubClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除小班
     */
    @DeleteMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteSubClass(@PathVariable Integer id) {
        classService.deleteSubClassById(id);
        return ResponseMessage.success("小班删除成功");
    }

    /**
     * 根据ID查询小班
     */
    @GetMapping("/sub/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubClass> getSubClassById(@PathVariable Integer id) {
        Optional<SubClass> subClass = classService.findSubClassById(id);
        return subClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("小班不存在"));
    }

    /**
     * 查询所有小班
     */
    @GetMapping("/sub")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getAllSubClasses() {
        List<SubClass> subClasses = classService.findAllSubClasses();
        return ResponseMessage.success(subClasses);
    }

    /**
     * 分页查询小班
     */
    @GetMapping("/sub/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<SubClass>> getSubClassesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "subClassId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubClass> subClasses = classService.findAllSubClasses(pageable);
        return ResponseMessage.success(subClasses);
    }

    /**
     * 根据大班查询小班
     */
    @GetMapping("/sub/total/{totalClassId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getSubClassesByTotalClass(@PathVariable Integer totalClassId) {
        List<SubClass> subClasses = classService.findSubClassesByTotalClassId(totalClassId);
        return ResponseMessage.success(subClasses);
    }

    /**
     * 根据小班名称查询
     */
    @GetMapping("/sub/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> searchSubClasses(@RequestParam String name) {
        List<SubClass> subClasses = classService.searchSubClasses(name);
        return ResponseMessage.success(subClasses);
    }
}