package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问接口
 * 提供课程相关的数据库操作，包括基础CRUD和复杂查询
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 继承JpaRepository提供标准操作</li>
 *   <li>条件查询 - 支持按学院、课程名称、学分等条件查询</li>
 *   <li>分页查询 - 支持分页和排序</li>
 *   <li>统计查询 - 支持课程数量统计和学分统计</li>
 *   <li>自定义查询 - 使用@Query注解实现复杂查询</li>
 * </ul>
 * 
 * <p>查询方法命名规范：</p>
 * <ul>
 *   <li>findBy + 属性名 - 精确查询</li>
 *   <li>findBy + 属性名 + Containing - 模糊查询</li>
 *   <li>findBy + 属性名 + Between - 范围查询</li>
 *   <li>countBy + 属性名 - 统计查询</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-11
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    // 根据学院查询课程
    @NonNull
    List<Subject> findBySubjectAcademy(@NonNull String academy);
    
    // 根据课程名称模糊查询
    @NonNull
    List<Subject> findBySubjectNameContaining(@NonNull String name);
    
    // 根据课程名称精确查询
    @NonNull
    Optional<Subject> findBySubjectName(@NonNull String subjectName);
    
    // 根据学院和课程名称查询
    @NonNull
    Optional<Subject> findBySubjectAcademyAndSubjectName(@NonNull String academy, @NonNull String subjectName);
    
    // 根据学分查询
    @NonNull
    List<Subject> findByCredit(@NonNull Double credit);
    
    // 根据学分范围查询
    @NonNull
    List<Subject> findByCreditBetween(@NonNull Double minCredit, @NonNull Double maxCredit);
    
    // 根据学院和学分查询
    @NonNull
    List<Subject> findBySubjectAcademyAndCredit(@NonNull String academy, @NonNull Double credit);
    
    // 根据学院和学分范围查询
    @NonNull
    List<Subject> findBySubjectAcademyAndCreditBetween(@NonNull String academy, @NonNull Double minCredit, @NonNull Double maxCredit);
    
    // 分页查询所有课程
    @NonNull
    Page<Subject> findAll(@NonNull Pageable pageable);
    
    // 根据学院分页查询
    @NonNull
    Page<Subject> findBySubjectAcademy(@NonNull String academy, @NonNull Pageable pageable);
    
    // 根据课程名称模糊分页查询
    @NonNull
    Page<Subject> findBySubjectNameContaining(@NonNull String name, @NonNull Pageable pageable);
    
    // 根据学分分页查询
    @NonNull
    Page<Subject> findByCredit(@NonNull Double credit, @NonNull Pageable pageable);
    
    // 根据学分范围分页查询
    @NonNull
    Page<Subject> findByCreditBetween(@NonNull Double minCredit, @NonNull Double maxCredit, @NonNull Pageable pageable);
    
    // 统计课程数量
    long countBySubjectAcademy(@NonNull String academy);
    
    // 统计指定学分的课程数量
    long countByCredit(@NonNull Double credit);
    
    // 统计学分范围内的课程数量
    long countByCreditBetween(@NonNull Double minCredit, @NonNull Double maxCredit);
    
    // 统计学院和学分范围内的课程数量
    long countBySubjectAcademyAndCreditBetween(@NonNull String academy, @NonNull Double minCredit, @NonNull Double maxCredit);
    
    // 计算学院总学分
    @Query("SELECT COALESCE(SUM(s.credit), 0) FROM Subject s WHERE s.subjectAcademy = :academy")
    @NonNull
    Double sumCreditBySubjectAcademy(@Param("academy") @NonNull String academy);
    
    // 查询所有学院列表
    @Query("SELECT DISTINCT s.subjectAcademy FROM Subject s WHERE s.subjectAcademy IS NOT NULL")
    @NonNull
    List<String> findAllAcademies();
    
    // 查询指定学院的所有学分列表
    @Query("SELECT DISTINCT s.credit FROM Subject s WHERE s.subjectAcademy = :academy AND s.credit IS NOT NULL ORDER BY s.credit")
    @NonNull
    List<Double> findCreditsByAcademy(@Param("academy") @NonNull String academy);
    
    // 根据多个条件查询课程
    @Query("SELECT s FROM Subject s WHERE " +
           "(:academy IS NULL OR s.subjectAcademy = :academy) AND " +
           "(:subjectName IS NULL OR s.subjectName LIKE %:subjectName%) AND " +
           "(:minCredit IS NULL OR s.credit >= :minCredit) AND " +
           "(:maxCredit IS NULL OR s.credit <= :maxCredit)")
    @NonNull
    List<Subject> findByMultipleConditions(@Param("academy") @Nullable String academy,
                                          @Param("subjectName") @Nullable String subjectName,
                                          @Param("minCredit") @Nullable Double minCredit,
                                          @Param("maxCredit") @Nullable Double maxCredit);
    
    // 根据多个条件分页查询课程
    @Query("SELECT s FROM Subject s WHERE " +
           "(:academy IS NULL OR s.subjectAcademy = :academy) AND " +
           "(:subjectName IS NULL OR s.subjectName LIKE %:subjectName%) AND " +
           "(:minCredit IS NULL OR s.credit >= :minCredit) AND " +
           "(:maxCredit IS NULL OR s.credit <= :maxCredit)")
    @NonNull
    Page<Subject> findByMultipleConditions(@Param("academy") @Nullable String academy,
                                          @Param("subjectName") @Nullable String subjectName,
                                          @Param("minCredit") @Nullable Double minCredit,
                                          @Param("maxCredit") @Nullable Double maxCredit,
                                          @NonNull Pageable pageable);
}
