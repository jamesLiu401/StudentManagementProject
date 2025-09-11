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
 * 提供班级信息的增删改查功能
 * <p>
 * 主要功能：
 * 1. 班级信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 专业关联管理 - 支持按专业查询班级
 * 4. 班级层级管理 - 支持总班级和子班级管理
 * 5. 多条件查询 - 支持班级名称模糊查询
 * 6. 统计分析 - 班级数量统计
 * <p>
 * 权限控制：
 * - 增删改操作：仅ADMIN
 * - 查询操作：ADMIN和TEACHER
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-09-10
 */
@RestController
@RequestMapping("/classes")
public class ClassController {

    private final IClassService classService;
    private final IMajorService majorService;

    public ClassController(IClassService classService, IMajorService majorService) {
        this.classService = classService;
        this.majorService = majorService;
    }

    // ==================== TotalClass 总班级管理 ====================

    /**
     * 添加总班级信息
     * 
     * @param totalClass 总班级信息对象
     * @return 保存成功的总班级信息
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
     * 创建总班级（通过参数）
     * 
     * @param totalClassName 总班级名称
     * @param majorId 专业ID
     * @return 创建的总班级信息
     */
    @PostMapping("/total/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> createTotalClass(
            @RequestParam String totalClassName,
            @RequestParam Integer majorId) {
        try {
            var major = majorService.findById(majorId)
                    .orElseThrow(() -> new IllegalArgumentException("专业不存在"));
            TotalClass totalClass = classService.createTotalClass(totalClassName, major);
            return ResponseMessage.success(totalClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新总班级信息
     * 
     * @param id 总班级ID
     * @param totalClass 更新的总班级信息
     * @return 更新后的总班级信息
     */
    @PutMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<TotalClass> updateTotalClass(
            @PathVariable Integer id, 
            @RequestBody TotalClass totalClass) {
        try {
            // 这里需要实现updateTotalClass方法，暂时使用save
            TotalClass updatedTotalClass = classService.saveTotalClass(totalClass);
            return ResponseMessage.success(updatedTotalClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除总班级
     * 
     * @param id 总班级ID
     * @return 删除结果
     */
    @DeleteMapping("/total/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteTotalClass(@PathVariable Integer id) {
        classService.deleteTotalClassById(id);
        return ResponseMessage.success("总班级删除成功");
    }

    /**
     * 根据ID查询总班级
     * 
     * @param id 总班级ID
     * @return 总班级信息
     */
    @GetMapping("/total/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<TotalClass> getTotalClassById(@PathVariable Integer id) {
        Optional<TotalClass> totalClass = classService.findTotalClassById(id);
        return totalClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("总班级不存在"));
    }

    /**
     * 查询所有总班级
     * 
     * @return 所有总班级列表
     */
    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getAllTotalClasses() {
        List<TotalClass> totalClasses = classService.findAllTotalClasses();
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 分页查询总班级
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的总班级列表
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
     * 根据专业查询总班级
     * 
     * @param majorId 专业ID
     * @return 该专业下的总班级列表
     */
    @GetMapping("/total/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> getTotalClassesByMajor(@PathVariable Integer majorId) {
        List<TotalClass> totalClasses = classService.findTotalClassesByMajorId(majorId);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 根据总班级名称模糊查询
     * 
     * @param name 总班级名称关键字
     * @return 匹配的总班级列表
     */
    @GetMapping("/total/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> searchTotalClasses(@RequestParam String name) {
        List<TotalClass> totalClasses = classService.findByTotalClassNameContaining(name);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 根据总班级名称和专业查询
     * 
     * @param totalClassName 总班级名称
     * @param majorId 专业ID
     * @return 总班级信息
     */
    @GetMapping("/total/name/{totalClassName}/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<TotalClass> getTotalClassByNameAndMajor(
            @PathVariable String totalClassName,
            @PathVariable Integer majorId) {
        try {
            var major = majorService.findById(majorId)
                    .orElseThrow(() -> new IllegalArgumentException("专业不存在"));
            Optional<TotalClass> totalClass = classService.findByTotalClassNameAndMajor(totalClassName, major);
            return totalClass.map(ResponseMessage::success)
                    .orElse(ResponseMessage.error("总班级不存在"));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    // ==================== SubClass 子班级管理 ====================

    /**
     * 添加子班级信息
     * 
     * @param subClass 子班级信息对象
     * @return 保存成功的子班级信息
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
     * 创建子班级（通过参数）
     * 
     * @param subClassName 子班级名称
     * @param totalClassId 总班级ID
     * @return 创建的子班级信息
     */
    @PostMapping("/sub/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> createSubClass(
            @RequestParam String subClassName,
            @RequestParam Integer totalClassId) {
        try {
            var totalClass = classService.findTotalClassById(totalClassId)
                    .orElseThrow(() -> new IllegalArgumentException("总班级不存在"));
            SubClass subClass = classService.createSubClass(subClassName, totalClass);
            return ResponseMessage.success(subClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新子班级信息
     * 
     * @param id 子班级ID
     * @param subClass 更新的子班级信息
     * @return 更新后的子班级信息
     */
    @PutMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<SubClass> updateSubClass(
            @PathVariable Integer id, 
            @RequestBody SubClass subClass) {
        try {
            // 这里需要实现updateSubClass方法，暂时使用save
            SubClass updatedSubClass = classService.saveSubClass(subClass);
            return ResponseMessage.success(updatedSubClass);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除子班级
     * 
     * @param id 子班级ID
     * @return 删除结果
     */
    @DeleteMapping("/sub/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteSubClass(@PathVariable Integer id) {
        classService.deleteSubClassById(id);
        return ResponseMessage.success("子班级删除成功");
    }

    /**
     * 根据ID查询子班级
     * 
     * @param id 子班级ID
     * @return 子班级信息
     */
    @GetMapping("/sub/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubClass> getSubClassById(@PathVariable Integer id) {
        Optional<SubClass> subClass = classService.findSubClassById(id);
        return subClass.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("子班级不存在"));
    }

    /**
     * 查询所有子班级
     * 
     * @return 所有子班级列表
     */
    @GetMapping("/sub")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getAllSubClasses() {
        List<SubClass> subClasses = classService.findAllSubClasses();
        return ResponseMessage.success(subClasses);
    }

    /**
     * 分页查询子班级
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的子班级列表
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
     * 根据总班级查询子班级
     * 
     * @param totalClassId 总班级ID
     * @return 该总班级下的子班级列表
     */
    @GetMapping("/sub/total/{totalClassId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> getSubClassesByTotalClass(@PathVariable Integer totalClassId) {
        List<SubClass> subClasses = classService.findSubClassesByTotalClassId(totalClassId);
        return ResponseMessage.success(subClasses);
    }

    /**
     * 根据子班级名称模糊查询
     * 
     * @param name 子班级名称关键字
     * @return 匹配的子班级列表
     */
    @GetMapping("/sub/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> searchSubClasses(@RequestParam String name) {
        List<SubClass> subClasses = classService.findBySubClassNameContaining(name);
        return ResponseMessage.success(subClasses);
    }

    /**
     * 根据子班级名称和总班级查询
     * 
     * @param subClassName 子班级名称
     * @param totalClassId 总班级ID
     * @return 子班级信息
     */
    @GetMapping("/sub/name/{subClassName}/total/{totalClassId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<SubClass> getSubClassByNameAndTotalClass(
            @PathVariable String subClassName,
            @PathVariable Integer totalClassId) {
        try {
            var totalClass = classService.findTotalClassById(totalClassId)
                    .orElseThrow(() -> new IllegalArgumentException("总班级不存在"));
            Optional<SubClass> subClass = classService.findBySubClassNameAndTotalClass(subClassName, totalClass);
            return subClass.map(ResponseMessage::success)
                    .orElse(ResponseMessage.error("子班级不存在"));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    // ==================== 业务逻辑API ====================

    /**
     * 检查总班级是否存在
     * 
     * @param totalClassName 总班级名称
     * @param majorId 专业ID
     * @return 是否存在
     */
    @GetMapping("/total/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Boolean> checkTotalClassExists(
            @RequestParam String totalClassName,
            @RequestParam Integer majorId) {
        try {
            var major = majorService.findById(majorId)
                    .orElseThrow(() -> new IllegalArgumentException("专业不存在"));
            boolean exists = classService.existsByTotalClassNameAndMajor(totalClassName, major);
            return ResponseMessage.success(exists);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 检查子班级是否存在
     * 
     * @param subClassName 子班级名称
     * @param totalClassId 总班级ID
     * @return 是否存在
     */
    @GetMapping("/sub/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Boolean> checkSubClassExists(
            @RequestParam String subClassName,
            @RequestParam Integer totalClassId) {
        try {
            var totalClass = classService.findTotalClassById(totalClassId)
                    .orElseThrow(() -> new IllegalArgumentException("总班级不存在"));
            boolean exists = classService.existsBySubClassNameAndTotalClass(subClassName, totalClass);
            return ResponseMessage.success(exists);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 通用搜索总班级
     * 
     * @param keyword 搜索关键字
     * @return 匹配的总班级列表
     */
    @GetMapping("/total/search/general")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<TotalClass>> searchTotalClassesGeneral(@RequestParam String keyword) {
        List<TotalClass> totalClasses = classService.searchTotalClasses(keyword);
        return ResponseMessage.success(totalClasses);
    }

    /**
     * 通用搜索子班级
     * 
     * @param keyword 搜索关键字
     * @return 匹配的子班级列表
     */
    @GetMapping("/sub/search/general")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<SubClass>> searchSubClassesGeneral(@RequestParam String keyword) {
        List<SubClass> subClasses = classService.searchSubClasses(keyword);
        return ResponseMessage.success(subClasses);
    }
}