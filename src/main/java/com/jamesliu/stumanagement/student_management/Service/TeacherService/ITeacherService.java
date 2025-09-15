package com.jamesliu.stumanagement.student_management.Service.TeacherService;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 教师服务接口
 * 定义教师相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 教师的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 教师数量统计</li>
 *   <li>业务逻辑操作 - 教师创建、更新、搜索等</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-01-01
 */
public interface ITeacherService {
    
    // 基础 CRUD 操作
    Teacher saveTeacher(Teacher teacher);
    Optional<Teacher> findById(Integer id);
    Optional<Teacher> findByIdWithDetails(Integer id);
    List<Teacher> findAll();
    Page<Teacher> findAll(Pageable pageable);
    void deleteById(Integer id);
    void deleteTeacher(Teacher teacher);
    
    // 查询操作
    List<Teacher> findByDepartment(String department);
    List<Teacher> findByTitle(String title);
    Optional<Teacher> findByTeacherNo(String teacherNo);
    List<Teacher> findByTeacherNameContaining(String name);
    List<Teacher> findByDepartmentAndTitle(String department, String title);
    
    // 分页查询
    Page<Teacher> findByDepartment(String department, Pageable pageable);
    Page<Teacher> findByTitle(String title, Pageable pageable);
    Page<Teacher> findByTeacherNameContaining(String name, Pageable pageable);
    
    // 统计操作
    long countByDepartment(String department);
    long countByTitle(String title);
    long countByDepartmentAndTitle(String department, String title);
    
    // 业务逻辑操作
    boolean existsByTeacherNo(String teacherNo);
    Teacher createTeacher(String teacherNo, String teacherName, String department, String title);
    Teacher updateTeacher(Integer id, String teacherNo, String teacherName, String department, String title);
    List<Teacher> searchTeachers(String keyword);
    List<String> findAllDepartments();
    List<String> findAllTitles();
}
