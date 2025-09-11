package com.jamesliu.stumanagement.student_management.Service.AcademyService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 学院服务接口
 * 定义学院相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 学院的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 学院数量统计</li>
 *   <li>业务逻辑操作 - 学院创建、更新、搜索等</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-01-01
 */
public interface IAcademyService {
    
    // 基础 CRUD 操作
    Academy saveAcademy(Academy academy);
    Optional<Academy> findById(Integer id);
    List<Academy> findAll();
    Page<Academy> findAll(Pageable pageable);
    void deleteById(Integer id);
    void deleteAcademy(Academy academy);
    
    // 查询操作
    Optional<Academy> findByAcademyName(String academyName);
    Optional<Academy> findByAcademyCode(String academyCode);
    List<Academy> findByAcademyNameContaining(String name);
    List<Academy> findByDeanName(String deanName);
    List<Academy> findByAcademyNameContainingAndDeanName(String name, String deanName);
    
    // 分页查询
    Page<Academy> findByAcademyNameContaining(String name, Pageable pageable);
    Page<Academy> findByDeanName(String deanName, Pageable pageable);
    
    // 统计操作
    long countByAcademyNameContaining(String name);
    long countByDeanName(String deanName);
    
    // 业务逻辑操作
    boolean existsByAcademyName(String academyName);
    boolean existsByAcademyCode(String academyCode);
    Academy createAcademy(String academyName, String academyCode, String description, String deanName, String contactPhone, String address);
    Academy updateAcademy(Integer id, String academyName, String academyCode, String description, String deanName, String contactPhone, String address);
    List<Academy> searchAcademies(String keyword);
    List<String> findAllAcademyNames();
    List<String> findAllAcademyCodes();
}
