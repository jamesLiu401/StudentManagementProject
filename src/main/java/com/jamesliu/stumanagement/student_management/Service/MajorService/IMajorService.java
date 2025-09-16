package com.jamesliu.stumanagement.student_management.Service.MajorService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.dto.MajorDTO;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 专业服务接口
 * 定义专业相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 专业的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 专业数量统计</li>
 *   <li>业务逻辑操作 - 专业创建、更新、搜索等</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-01-01
 */
public interface IMajorService {
    
    // 基础 CRUD 操作
    Major saveMajor(Major major);
    Optional<MajorDTO> findById(Integer id);
    List<MajorDTO> findAll();
    Page<MajorDTO> findAll(Pageable pageable);
    void deleteById(Integer id);
    void deleteMajor(Major major);
    
    // 查询操作
    List<MajorDTO> findByAcademy(Academy academy);
    List<MajorDTO> findByGrade(Integer grade);
    List<MajorDTO> findByCounselor(Teacher counselor);
    List<MajorDTO> findByMajorNameContaining(String name);
    List<MajorDTO> findByAcademyAndGrade(Academy academy, Integer grade);
    List<MajorDTO> findByAcademyAndMajorNameContaining(Academy academy, String name);
    Optional<MajorDTO> findByMajorNameAndGrade(String majorName, Integer grade);
    
    // 分页查询
    Page<MajorDTO> findByAcademy(Academy academy, Pageable pageable);
    Page<MajorDTO> findByGrade(Integer grade, Pageable pageable);
    Page<MajorDTO> findByCounselor(Teacher counselor, Pageable pageable);
    Page<MajorDTO> findByMajorNameContaining(String name, Pageable pageable);
    
    // 统计操作
    long countByAcademy(Academy academy);
    long countByGrade(Integer grade);
    long countByCounselor(Teacher counselor);
    long countByAcademyAndGrade(Academy academy, Integer grade);
    
    // 业务逻辑操作
    boolean existsByMajorNameAndGrade(String majorName, Integer grade);
    Major createMajor(String majorName, Academy academy, Integer grade, Teacher counselor);
    Major updateMajor(Integer id, String majorName, Academy academy, Integer grade, Teacher counselor);
    List<MajorDTO> searchMajors(String keyword);
    List<MajorDTO> getMajorsByAcademyId(Integer academyId);
    List<MajorDTO> getMajorsByGrade(Integer grade);

    // 内部使用：需要实体的场景（如班级创建依赖Major实体）
    Optional<Major> findEntityById(Integer id);
}
