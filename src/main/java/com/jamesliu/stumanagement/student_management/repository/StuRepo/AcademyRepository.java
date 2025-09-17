package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学院数据访问接口
 * 提供学院相关的数据库操作，包括基础CRUD和条件查询
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 继承JpaRepository提供标准操作</li>
 *   <li>条件查询 - 支持按学院名称、代码、院长姓名等条件查询</li>
 *   <li>模糊查询 - 支持学院名称和院长姓名的模糊搜索</li>
 *   <li>唯一性查询 - 支持按学院名称和代码的精确查询</li>
 * </ul>
 * 
 * <p>查询方法说明：</p>
 * <ul>
 *   <li>findByAcademyName - 根据学院名称精确查询</li>
 *   <li>findByAcademyCode - 根据学院代码精确查询</li>
 *   <li>findByAcademyNameContaining - 根据学院名称模糊查询</li>
 *   <li>findByDeanName - 根据院长姓名精确查询</li>
 *   <li>findByDeanNameContaining - 根据院长姓名模糊查询</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-12
 */
@Repository
public interface AcademyRepository extends JpaRepository<Academy, Integer> {
    
    // 根据学院名称查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    Optional<Academy> findByAcademyName(@NonNull String academyName);
    
    // 根据学院代码查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    Optional<Academy> findByAcademyCode(@NonNull String academyCode);

    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    Optional<Academy> findByAcademyId(@NonNull Integer academyId);
    // 根据学院名称模糊查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    List<Academy> findByAcademyNameContaining(@NonNull String academyName);
    
    // 根据院长姓名查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    List<Academy> findByDeanName(@NonNull String deanName);
    
    // 根据院长姓名模糊查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    List<Academy> findByDeanNameContaining(@NonNull String deanName);
    
    // 根据学院名称模糊查询和院长姓名查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    List<Academy> findByAcademyNameContainingAndDeanName(@NonNull String academyName, @NonNull String deanName);
    
    // 根据学院名称模糊分页查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    Page<Academy> findByAcademyNameContaining(@NonNull String academyName, @NonNull Pageable pageable);
    
    // 根据院长姓名分页查询
    @NonNull
    @EntityGraph(attributePaths = {"majors"})
    Page<Academy> findByDeanName(@NonNull String deanName, @NonNull Pageable pageable);
    
    // 统计方法
    long countByAcademyNameContaining(@NonNull String name);
    long countByDeanName(@NonNull String deanName);
}
