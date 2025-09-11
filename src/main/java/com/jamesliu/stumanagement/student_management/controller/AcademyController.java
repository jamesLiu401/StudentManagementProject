package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Service.AcademyService.IAcademyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 学院管理控制器
 * 提供学院信息的增删改查功能
 * <p>
 * 主要功能：
 * 1. 学院信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 学院代码管理 - 支持学院代码的查询和管理
 * 4. 院长信息管理 - 支持院长信息的查询和管理
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
@RequestMapping("/academies")
public class AcademyController {

    private final IAcademyService academyService;

    public AcademyController(IAcademyService academyService) {
        this.academyService = academyService;
    }

    /**
     * 添加学院信息
     * 
     * @param academy 学院信息对象
     * @return 保存成功的学院信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Academy> addAcademy(@RequestBody Academy academy) {
        try {
            Academy savedAcademy = academyService.saveAcademy(academy);
            return ResponseMessage.success(savedAcademy);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 更新学院信息
     * 
     * @param id 学院ID
     * @param academy 更新的学院信息
     * @return 更新后的学院信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Academy> updateAcademy(
            @PathVariable Integer id, 
            @RequestBody Academy academy) {
        try {
            Academy updatedAcademy = academyService.updateAcademy(id,
                academy.getAcademyName(), academy.getAcademyCode(), academy.getDescription(),
                academy.getDeanName(), academy.getContactPhone(), academy.getAddress());
            return ResponseMessage.success(updatedAcademy);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    /**
     * 删除学院
     * 
     * @param id 学院ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteAcademy(@PathVariable Integer id) {
        academyService.deleteById(id);
        return ResponseMessage.success("学院删除成功");
    }

    /**
     * 根据ID查询学院
     * 
     * @param id 学院ID
     * @return 学院信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Academy> getAcademyById(@PathVariable Integer id) {
        Optional<Academy> academy = academyService.findById(id);
        return academy.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("学院不存在"));
    }

    /**
     * 查询所有学院
     * 
     * @return 所有学院列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Academy>> getAllAcademies() {
        List<Academy> academies = academyService.findAll();
        return ResponseMessage.success(academies);
    }

    /**
     * 分页查询学院
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 分页的学院列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Academy>> getAcademiesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "academyId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Academy> academies = academyService.findAll(pageable);
        return ResponseMessage.success(academies);
    }

    /**
     * 根据学院名称模糊查询
     * 
     * @param name 学院名称关键字
     * @return 匹配的学院列表
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Academy>> searchAcademiesByName(@RequestParam String name) {
        List<Academy> academies = academyService.searchAcademies(name);
        return ResponseMessage.success(academies);
    }

    /**
     * 根据院长姓名精确查询
     * 
     * @param deanName 院长姓名
     * @return 学院信息
     */
    @GetMapping("/dean/{deanName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Academy>> getAcademiesByDean(@PathVariable String deanName) {
        List<Academy> academies = academyService.findByDeanName(deanName);
        return ResponseMessage.success(academies);
    }

    /**
     * 根据院长姓名模糊查询
     * 
     * @param deanName 院长姓名
     * @return 匹配的学院列表
     */
    @GetMapping("/search/dean")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Academy>> searchAcademiesByDean(@RequestParam String deanName) {
        List<Academy> academies = academyService.findByDeanName(deanName);
        return ResponseMessage.success(academies);
    }

    /**
     * 根据学院名称精确查询
     * 
     * @param name 学院名称
     * @return 学院信息
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Academy> getAcademyByName(@PathVariable String name) {
        Optional<Academy> academy = academyService.findByAcademyName(name);
        return academy.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("学院不存在"));
    }

    /**
     * 根据学院代码查询
     * 
     * @param code 学院代码
     * @return 学院信息
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Academy> getAcademyByCode(@PathVariable String code) {
        Optional<Academy> academy = academyService.findByAcademyCode(code);
        return academy.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("学院代码不存在"));
    }

    @GetMapping("/search/name/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Academy>> searchAcademiesByNamePage(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "academyId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Academy> academies = academyService.findByAcademyNameContaining(name, pageable);
        return ResponseMessage.success(academies);
    }

    @GetMapping("/search/dean/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Academy>> searchAcademiesByDeanPage(
            @RequestParam String deanName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "academyId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Academy> academies = academyService.findByDeanName(deanName, pageable);
        return ResponseMessage.success(academies);
    }

    // 统计相关API
    @GetMapping("/search/name/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countAcademiesByNameContaining(@RequestParam String name) {
        long count = academyService.countByAcademyNameContaining(name);
        return ResponseMessage.success(count);
    }

    @GetMapping("/search/dean/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countAcademiesByDeanName(@RequestParam String deanName) {
        long count = academyService.countByDeanName(deanName);
        return ResponseMessage.success(count);
    }
}
