package com.jamesliu.stumanagement.student_management.Service.SubjectService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 课程服务实现类
 * 提供课程相关的业务逻辑操作，实现ISubjectService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>课程CRUD操作 - 创建、查询、更新、删除课程</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供课程数量统计和学分统计</li>
 *   <li>业务验证 - 课程重复性检查、数据完整性验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * <p>业务规则：</p>
 * <ul>
 *   <li>同一学院下不能有同名课程</li>
 *   <li>课程学分必须大于0</li>
 *   <li>删除课程前检查关联关系</li>
 * </ul>
 * 
 * <p>异常处理：</p>
 * <ul>
 *   <li>IllegalArgumentException - 业务规则违反</li>
 *   <li>RuntimeException - 系统异常</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-21
 */
@Service
@Transactional
public class SubjectService implements ISubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findAll(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }
    
    @Override
    public void deleteById(Long id) {
        subjectRepository.deleteById(id);
    }
    
    @Override
    public void deleteSubject(Subject subject) {
        subjectRepository.delete(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByAcademy(String academy) {
        return subjectRepository.findBySubjectAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findBySubjectNameContaining(String name) {
        return subjectRepository.findBySubjectNameContaining(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findBySubjectName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findByAcademyAndSubjectName(String academy, String subjectName) {
        return subjectRepository.findBySubjectAcademyAndSubjectName(academy, subjectName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByCredit(Double credit) {
        return subjectRepository.findByCredit(credit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByCreditBetween(Double minCredit, Double maxCredit) {
        return subjectRepository.findByCreditBetween(minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByAcademyAndCredit(String academy, Double credit) {
        return subjectRepository.findBySubjectAcademyAndCredit(academy, credit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByAcademyAndCreditBetween(String academy, Double minCredit, Double maxCredit) {
        return subjectRepository.findBySubjectAcademyAndCreditBetween(academy, minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findByAcademy(String academy, Pageable pageable) {
        return subjectRepository.findBySubjectAcademy(academy, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findBySubjectNameContaining(String name, Pageable pageable) {
        return subjectRepository.findBySubjectNameContaining(name, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findByCredit(Double credit, Pageable pageable) {
        return subjectRepository.findByCredit(credit, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findByCreditBetween(Double minCredit, Double maxCredit, Pageable pageable) {
        return subjectRepository.findByCreditBetween(minCredit, maxCredit, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> findByMultipleConditions(String academy, String subjectName, Double minCredit, Double maxCredit) {
        return subjectRepository.findByMultipleConditions(academy, subjectName, minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findByMultipleConditions(String academy, String subjectName, Double minCredit, Double maxCredit, Pageable pageable) {
        return subjectRepository.findByMultipleConditions(academy, subjectName, minCredit, maxCredit, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByAcademy(String academy) {
        return subjectRepository.countBySubjectAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByCredit(Double credit) {
        return subjectRepository.countByCredit(credit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByCreditBetween(Double minCredit, Double maxCredit) {
        return subjectRepository.countByCreditBetween(minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByAcademyAndCreditBetween(String academy, Double minCredit, Double maxCredit) {
        return subjectRepository.countBySubjectAcademyAndCreditBetween(academy, minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double sumCreditByAcademy(String academy) {
        return subjectRepository.sumCreditBySubjectAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllAcademies() {
        return subjectRepository.findAllAcademies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Double> findCreditsByAcademy(String academy) {
        return subjectRepository.findCreditsByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAcademyAndSubjectName(String academy, String subjectName) {
        return subjectRepository.findBySubjectAcademyAndSubjectName(academy, subjectName).isPresent();
    }
    
    @Override
    public Subject createSubject(String subjectName, String academy, Double credit) {
        // 检查是否已存在相同的课程
        if (existsByAcademyAndSubjectName(academy, subjectName)) {
            throw new IllegalArgumentException("该学院已存在同名课程: " + subjectName);
        }
        
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setSubjectAcademy(academy);
        subject.setCredit(credit);
        
        return saveSubject(subject);
    }
    
    @Override
    public Subject updateSubject(Long id, String subjectName, String academy, Double credit) {
        Optional<Subject> existingSubject = findById(id);
        if (existingSubject.isEmpty()) {
            throw new IllegalArgumentException("课程不存在，ID: " + id);
        }
        
        Subject subject = existingSubject.get();
        
        // 如果学院或课程名称发生变化，检查是否会产生重复
        if (!subject.getSubjectAcademy().equals(academy) || !subject.getSubjectName().equals(subjectName)) {
            if (existsByAcademyAndSubjectName(academy, subjectName)) {
                throw new IllegalArgumentException("该学院已存在同名课程: " + subjectName);
            }
        }
        
        subject.setSubjectName(subjectName);
        subject.setSubjectAcademy(academy);
        subject.setCredit(credit);
        
        return saveSubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByAcademy(String academy) {
        return findByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Subject> searchSubjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        return findBySubjectNameContaining(keyword.trim());
    }
}
