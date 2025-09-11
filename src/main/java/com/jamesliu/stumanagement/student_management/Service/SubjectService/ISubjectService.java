package com.jamesliu.stumanagement.student_management.Service.SubjectService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 课程服务接口
 * 定义课程相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 课程的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 课程数量统计和学分统计</li>
 *   <li>聚合操作 - 学分求和、学院列表等</li>
 *   <li>业务逻辑操作 - 课程创建、更新、搜索等</li>
 * </ul>
 * 
 * <p>接口设计原则：</p>
 * <ul>
 *   <li>单一职责 - 每个方法只负责一个功能</li>
 *   <li>参数验证 - 所有参数都进行非空验证</li>
 *   <li>异常处理 - 统一的异常处理机制</li>
 *   <li>事务支持 - 支持事务管理</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-22
 */
public interface ISubjectService {
    
    // 基础 CRUD 操作
    Subject saveSubject(Subject subject);
    Optional<Subject> findById(Long id);
    List<Subject> findAll();
    Page<Subject> findAll(Pageable pageable);
    void deleteById(Long id);
    void deleteSubject(Subject subject);
    
    // 查询操作
    List<Subject> findByAcademy(String academy);
    List<Subject> findBySubjectNameContaining(String name);
    Optional<Subject> findBySubjectName(String subjectName);
    Optional<Subject> findByAcademyAndSubjectName(String academy, String subjectName);
    List<Subject> findByCredit(Double credit);
    List<Subject> findByCreditBetween(Double minCredit, Double maxCredit);
    List<Subject> findByAcademyAndCredit(String academy, Double credit);
    List<Subject> findByAcademyAndCreditBetween(String academy, Double minCredit, Double maxCredit);
    
    // 分页查询
    Page<Subject> findByAcademy(String academy, Pageable pageable);
    Page<Subject> findBySubjectNameContaining(String name, Pageable pageable);
    Page<Subject> findByCredit(Double credit, Pageable pageable);
    Page<Subject> findByCreditBetween(Double minCredit, Double maxCredit, Pageable pageable);
    
    // 多条件查询
    List<Subject> findByMultipleConditions(String academy, String subjectName, Double minCredit, Double maxCredit);
    Page<Subject> findByMultipleConditions(String academy, String subjectName, Double minCredit, Double maxCredit, Pageable pageable);
    
    // 统计操作
    long countByAcademy(String academy);
    long countByCredit(Double credit);
    long countByCreditBetween(Double minCredit, Double maxCredit);
    long countByAcademyAndCreditBetween(String academy, Double minCredit, Double maxCredit);
    
    // 聚合操作
    Double sumCreditByAcademy(String academy);
    List<String> findAllAcademies();
    List<Double> findCreditsByAcademy(String academy);
    
    // 业务逻辑操作
    boolean existsByAcademyAndSubjectName(String academy, String subjectName);
    Subject createSubject(String subjectName, String academy, Double credit);
    Subject updateSubject(Long id, String subjectName, String academy, Double credit);
    List<Subject> getSubjectsByAcademy(String academy);
    List<Subject> searchSubjects(String keyword);
}
