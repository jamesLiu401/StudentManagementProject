package com.jamesliu.stumanagement.student_management.Service.ClassService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 班级服务接口
 * 定义班级相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 班级的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 班级数量统计</li>
 *   <li>业务逻辑操作 - 班级创建、更新、搜索等</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-01-01
 */
public interface IClassService {
    
    // TotalClass 相关操作
    TotalClass saveTotalClass(TotalClass totalClass);
    Optional<TotalClass> findTotalClassById(Integer id);
    List<TotalClass> findAllTotalClasses();
    Page<TotalClass> findAllTotalClasses(Pageable pageable);
    void deleteTotalClassById(Integer id);
    void deleteTotalClass(TotalClass totalClass);
    
    List<TotalClass> findTotalClassesByMajor(Major major);
    List<TotalClass> findTotalClassesByMajorId(Integer majorId);
    Optional<TotalClass> findByTotalClassNameAndMajor(String totalClassName, Major major);
    List<TotalClass> findByTotalClassNameContaining(String name);
    
    // SubClass 相关操作
    SubClass saveSubClass(SubClass subClass);
    Optional<SubClass> findSubClassById(Integer id);
    List<SubClass> findAllSubClasses();
    Page<SubClass> findAllSubClasses(Pageable pageable);
    void deleteSubClassById(Integer id);
    void deleteSubClass(SubClass subClass);
    
    List<SubClass> findSubClassesByTotalClass(TotalClass totalClass);
    List<SubClass> findSubClassesByTotalClassId(Integer totalClassId);
    Optional<SubClass> findBySubClassNameAndTotalClass(String subClassName, TotalClass totalClass);
    List<SubClass> findBySubClassNameContaining(String name);
    
    // 业务逻辑操作
    boolean existsByTotalClassNameAndMajor(String totalClassName, Major major);
    boolean existsBySubClassNameAndTotalClass(String subClassName, TotalClass totalClass);
    
    TotalClass createTotalClass(String totalClassName, Major major);
    SubClass createSubClass(String subClassName, TotalClass totalClass);
    
    List<TotalClass> searchTotalClasses(String keyword);
    List<SubClass> searchSubClasses(String keyword);
}
