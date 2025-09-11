package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.MajorRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubClassRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.TotalClassRepository;
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

    private final TotalClassRepository totalClassRepository;
    private final SubClassRepository subClassRepository;
    private final MajorRepository majorRepository;

    public ClassController(TotalClassRepository totalClassRepository,
                          SubClassRepository subClassRepository,
                          MajorRepository majorRepository) {
        this.totalClassRepository = totalClassRepository;
        this.subClassRepository = subClassRepository;
        this.majorRepository = majorRepository;
    }

    // ==================== 大班管理 ====================

    /**
     * 添加大班
     * 
     * @param totalClass 大班信息对象
     * @return 保存成功的大班信息
     */
    @PostMapping("/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> addTotalClass(@RequestBody TotalClass totalClass) {
        // 检查专业是否存在
        if (totalClass.getMajor() == null || totalClass.getMajor().getMajorId() == null) {
            return ResponseMessage.error("专业信息不能为空");
        }
        
        Optional<Major> major = majorRepository.findById(totalClass.getMajor().getMajorId());
        if (!major.isPresent()) {
            return ResponseMessage.error("专业不存在");
        }
        
        // 检查大班名称是否已存在
        if (totalClassRepository.findByTotalClassNameAndMajor(totalClass.getTotalClassName(), major.get()).isPresent()) {
            return ResponseMessage.error("该专业下的大班名称已存在");
        }
        
        totalClass.setMajor(major.get());
        TotalClass savedTotalClass = totalClassRepository.save(totalClass);
        return ResponseMessage.success(savedTotalClass);
    }

    /**
     * 更新大班信息
     * 
     * @param id 大班ID
     * @param totalClass 更新的大班信息
     * @return 更新后的大班信息
     */
    @PutMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> updateTotalClass(
            @PathVariable Integer id, 
            @RequestBody TotalClass totalClass) {
        return totalClassRepository.findById(id)
                .map(existingTotalClass -> {
                    // 检查专业是否存在
                    if (totalClass.getMajor() != null && totalClass.getMajor().getMajorId() != null) {
                        Optional<Major> major = majorRepository.findById(totalClass.getMajor().getMajorId());
                        if (!major.isPresent()) {
                            throw new RuntimeException("专业不存在");
                        }
                        existingTotalClass.setMajor(major.get());
                    }
                    
                    // 检查大班名称是否与其他大班冲突
                    if (totalClass.getTotalClassName() != null) {
                        Optional<TotalClass> conflict = totalClassRepository.findByTotalClassNameAndMajor(
                            totalClass.getTotalClassName(), existingTotalClass.getMajor());
                        if (conflict.isPresent() && !conflict.get().getTotalClassId().equals(id)) {
                            throw new RuntimeException("该专业下的大班名称已存在");
                        }
                        existingTotalClass.setTotalClassName(totalClass.getTotalClassName());
                    }
                    
                    return totalClassRepository.save(existingTotalClass);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("大班不存在"));
    }

    /**
     * 删除大班
     * 
     * @param id 大班ID
     * @return 删除结果
     */
    @DeleteMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteTotalClass(@PathVariable Integer id) {
        if (totalClassRepository.existsById(id)) {
            totalClassRepository.deleteById(id);
            return ResponseMessage.success("大班删除成功");
        }
        return ResponseMessage.error("大班不存在");
    }

    /**
     * 根据ID查询大班
     * 
     * @param id 大班ID
     * @return 大班信息
     */
    @GetMapping("/total/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<TotalClass> getTotalClassById(@PathVariable Integer id) {
        Optional<TotalClass> totalClass = totalClassRepository.findById(id);
        return totalClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("大班不存在"));
    }

    /**
     * 查询所有大班
     * 
     * @return 所有大班列表
     */
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getAllTotalClasses() {
        List<TotalClass> totalClasses = totalClassRepository.findAll();
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 根据专业查询大班
     * 
     * @param majorId 专业ID
     * @return 该专业下的大班列表
     */
    @GetMapping("/total/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getTotalClassesByMajor(@PathVariable Integer majorId) {
        Optional<Major> major = majorRepository.findById(majorId);
        if (!major.isPresent()) {
            return ResponseMessage.error("专业不存在");
        }
        
        List<TotalClass> totalClasses = totalClassRepository.findByMajor(major.get());
        return ResponseMessage.success(totalClasses);
    }

    // ==================== 小班管理 ====================

    /**
     * 添加小班
     * 
     * @param subClass 小班信息对象
     * @return 保存成功的小班信息
     */
    @PostMapping("/sub")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> addSubClass(@RequestBody SubClass subClass) {
        // 检查大班是否存在
        if (subClass.getTotalClass() == null || subClass.getTotalClass().getTotalClassId() == null) {
            return ResponseMessage.error("大班信息不能为空");
        }
        
        Optional<TotalClass> totalClass = totalClassRepository.findById(subClass.getTotalClass().getTotalClassId());
        if (!totalClass.isPresent()) {
            return ResponseMessage.error("大班不存在");
        }
        
        // 检查小班名称是否已存在
        if (subClassRepository.findBySubClassNameAndTotalClass(subClass.getSubClassName(), totalClass.get()).isPresent()) {
            return ResponseMessage.error("该大班下的小班名称已存在");
        }
        
        subClass.setTotalClass(totalClass.get());
        SubClass savedSubClass = subClassRepository.save(subClass);
        return ResponseMessage.success(savedSubClass);
    }

    /**
     * 更新小班信息
     * 
     * @param id 小班ID
     * @param subClass 更新的小班信息
     * @return 更新后的小班信息
     */
    @PutMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> updateSubClass(
            @PathVariable Integer id, 
            @RequestBody SubClass subClass) {
        return subClassRepository.findById(id)
                .map(existingSubClass -> {
                    // 检查大班是否存在
                    if (subClass.getTotalClass() != null && subClass.getTotalClass().getTotalClassId() != null) {
                        Optional<TotalClass> totalClass = totalClassRepository.findById(subClass.getTotalClass().getTotalClassId());
                        if (!totalClass.isPresent()) {
                            throw new RuntimeException("大班不存在");
                        }
                        existingSubClass.setTotalClass(totalClass.get());
                    }
                    
                    // 检查小班名称是否与其他小班冲突
                    if (subClass.getSubClassName() != null) {
                        Optional<SubClass> conflict = subClassRepository.findBySubClassNameAndTotalClass(
                            subClass.getSubClassName(), existingSubClass.getTotalClass());
                        if (conflict.isPresent() && !conflict.get().getSubClassId().equals(id)) {
                            throw new RuntimeException("该大班下的小班名称已存在");
                        }
                        existingSubClass.setSubClassName(subClass.getSubClassName());
                    }
                    
                    return subClassRepository.save(existingSubClass);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("小班不存在"));
    }

    /**
     * 删除小班
     * 
     * @param id 小班ID
     * @return 删除结果
     */
    @DeleteMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteSubClass(@PathVariable Integer id) {
        if (subClassRepository.existsById(id)) {
            subClassRepository.deleteById(id);
            return ResponseMessage.success("小班删除成功");
        }
        return ResponseMessage.error("小班不存在");
    }

    /**
     * 根据ID查询小班
     * 
     * @param id 小班ID
     * @return 小班信息
     */
    @GetMapping("/sub/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubClass> getSubClassById(@PathVariable Integer id) {
        Optional<SubClass> subClass = subClassRepository.findById(id);
        return subClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("小班不存在"));
    }

    /**
     * 查询所有小班
     * 
     * @return 所有小班列表
     */
    @GetMapping("/sub")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getAllSubClasses() {
        List<SubClass> subClasses = subClassRepository.findAll();
        return ResponseMessage.success(subClasses);
    }

    /**
     * 根据大班查询小班
     * 
     * @param totalClassId 大班ID
     * @return 该大班下的小班列表
     */
    @GetMapping("/sub/total/{totalClassId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getSubClassesByTotalClass(@PathVariable Integer totalClassId) {
        Optional<TotalClass> totalClass = totalClassRepository.findById(totalClassId);
        if (!totalClass.isPresent()) {
            return ResponseMessage.error("大班不存在");
        }
        
        List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass.get());
        return ResponseMessage.success(subClasses);
    }

    // ==================== 分页查询 ====================

    /**
     * 分页查询大班
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的大班列表
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
        Page<TotalClass> totalClasses = totalClassRepository.findAll(pageable);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 分页查询小班
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的小班列表
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
        Page<SubClass> subClasses = subClassRepository.findAll(pageable);
        return ResponseMessage.success(subClasses);
    }
}