package com.jamesliu.stumanagement.student_management.Service.SubjectService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.dto.SubjectDTO;
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
    Optional<SubjectDTO> findById(Long id);
    List<SubjectDTO> findAll();
    Page<SubjectDTO> findAll(Pageable pageable);
    void deleteById(Long id);
    void deleteSubject(Subject subject);
    
    // 内部方法，返回实体
    Optional<Subject> findEntityById(Long id);

    Subject createSubjectDTO(SubjectDTO subjectDTO);
    // 查询操作
    List<SubjectDTO> findByAcademy(Academy academy);
    List<SubjectDTO> findBySubjectNameContaining(String name);
    Optional<SubjectDTO> findBySubjectName(String subjectName);
    Optional<SubjectDTO> findByAcademyAndSubjectName(Academy academy, String subjectName);
    List<SubjectDTO> findByCredit(Double credit);
    List<SubjectDTO> findByCreditBetween(Double minCredit, Double maxCredit);
    List<SubjectDTO> findByAcademyAndCredit(Academy academy, Double credit);
    List<SubjectDTO> findByAcademyAndCreditBetween(Academy academy, Double minCredit, Double maxCredit);
    
    // 分页查询
    Page<SubjectDTO> findByAcademy(Academy academy, Pageable pageable);
    Page<SubjectDTO> findBySubjectNameContaining(String name, Pageable pageable);
    Page<SubjectDTO> findByCredit(Double credit, Pageable pageable);
    Page<SubjectDTO> findByCreditBetween(Double minCredit, Double maxCredit, Pageable pageable);
    
    // 多条件查询
    List<SubjectDTO> findByMultipleConditions(Academy academy, String subjectName, Double minCredit, Double maxCredit);
    Page<SubjectDTO> findByMultipleConditions(Academy academy, String subjectName, Double minCredit, Double maxCredit, Pageable pageable);
    
    // 统计操作
    long countByAcademy(Academy academy);
    long countByCredit(Double credit);
    long countByCreditBetween(Double minCredit, Double maxCredit);
    long countByAcademyAndCreditBetween(Academy academy, Double minCredit, Double maxCredit);
    
    // 聚合操作
    Double sumCreditByAcademy(Academy academy);
    List<Academy> findAllAcademies();
    List<Double> findCreditsByAcademy(Academy academy);
    
    // 业务逻辑操作
    boolean existsByAcademyAndSubjectName(Academy academy, String subjectName);
    Subject createSubject(String subjectName, Academy academy, Double credit);
    Subject updateSubject(Long id, String subjectName, Academy academy, Double credit);
    List<SubjectDTO> getSubjectsByAcademy(Academy academy);
    List<SubjectDTO> searchSubjects(String keyword);
}
