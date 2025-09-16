package com.jamesliu.stumanagement.student_management.Service.SubjectService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubjectRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import com.jamesliu.stumanagement.student_management.dto.SubjectDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;
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
    
    @Autowired
    private AcademyRepository academyRepository;
    
    @Override
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectDTO> findById(Long id) {
        return subjectRepository.findById(id).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findAll() {
        return subjectRepository.findAll().stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findAll(Pageable pageable) {
        return subjectRepository.findAll(pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Subject> findEntityById(Long id) {
        return subjectRepository.findById(id);
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
    public List<SubjectDTO> findByAcademy(Academy academy) {
        return subjectRepository.findByAcademy(academy).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findBySubjectNameContaining(String name) {
        return subjectRepository.findBySubjectNameContaining(name).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectDTO> findBySubjectName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectDTO> findByAcademyAndSubjectName(Academy academy, String subjectName) {
        return subjectRepository.findByAcademyAndSubjectName(academy, subjectName).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByCredit(Double credit) {
        return subjectRepository.findByCredit(credit).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByCreditBetween(Double minCredit, Double maxCredit) {
        return subjectRepository.findByCreditBetween(minCredit, maxCredit).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByAcademyAndCredit(Academy academy, Double credit) {
        return subjectRepository.findByAcademyAndCredit(academy, credit).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByAcademyAndCreditBetween(Academy academy, Double minCredit, Double maxCredit) {
        return subjectRepository.findByAcademyAndCreditBetween(academy, minCredit, maxCredit).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByAcademy(Academy academy, Pageable pageable) {
        return subjectRepository.findByAcademy(academy, pageable).map(DtoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Subject createSubjectDTO(SubjectDTO subjectDTO) {
        Optional<Academy> O_academy = academyRepository.findById(subjectDTO.getAcademyId());
        Subject m_subject = new Subject();
        m_subject.setSubjectName(subjectDTO.getSubjectName());
        m_subject.setCredit(subjectDTO.getCredit());
        O_academy.ifPresent(m_subject::setAcademy);
        return subjectRepository.save(m_subject);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findBySubjectNameContaining(String name, Pageable pageable) {
        return subjectRepository.findBySubjectNameContaining(name, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByCredit(Double credit, Pageable pageable) {
        return subjectRepository.findByCredit(credit, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByCreditBetween(Double minCredit, Double maxCredit, Pageable pageable) {
        return subjectRepository.findByCreditBetween(minCredit, maxCredit, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> findByMultipleConditions(Academy academy, String subjectName, Double minCredit, Double maxCredit) {
        return subjectRepository.findByMultipleConditions(academy, subjectName, minCredit, maxCredit).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findByMultipleConditions(Academy academy, String subjectName, Double minCredit, Double maxCredit, Pageable pageable) {
        return subjectRepository.findByMultipleConditions(academy, subjectName, minCredit, maxCredit, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByAcademy(Academy academy) {
        return subjectRepository.countByAcademy(academy);
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
    public long countByAcademyAndCreditBetween(Academy academy, Double minCredit, Double maxCredit) {
        return subjectRepository.countByAcademyAndCreditBetween(academy, minCredit, maxCredit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double sumCreditByAcademy(Academy academy) {
        return subjectRepository.sumCreditByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> findAllAcademies() {
        return subjectRepository.findAllAcademies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Double> findCreditsByAcademy(Academy academy) {
        return subjectRepository.findCreditsByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAcademyAndSubjectName(Academy academy, String subjectName) {
        return subjectRepository.findByAcademyAndSubjectName(academy, subjectName).isPresent();
    }
    
    @Override
    public Subject createSubject(String subjectName, Academy academy, Double credit) {
        // 检查是否已存在相同的课程
        if (existsByAcademyAndSubjectName(academy, subjectName)) {
            throw new IllegalArgumentException("该学院已存在同名课程: " + subjectName);
        }
        
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setAcademy(academy);
        subject.setCredit(credit);
        
        return saveSubject(subject);
    }
    
    @Override
    public Subject updateSubject(Long id, String subjectName, Academy academy, Double credit) {
        Optional<Subject> existingSubject = findEntityById(id);
        if (existingSubject.isEmpty()) {
            throw new IllegalArgumentException("课程不存在，ID: " + id);
        }
        
        Subject subject = existingSubject.get();
        
        // 如果学院或课程名称发生变化，检查是否会产生重复
        if (!subject.getAcademy().equals(academy) || !subject.getSubjectName().equals(subjectName)) {
            if (existsByAcademyAndSubjectName(academy, subjectName)) {
                throw new IllegalArgumentException("该学院已存在同名课程: " + subjectName);
            }
        }
        
        subject.setSubjectName(subjectName);
        subject.setAcademy(academy);
        subject.setCredit(credit);
        
        return saveSubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsByAcademy(Academy academy) {
        return findByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> searchSubjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        return findBySubjectNameContaining(keyword.trim());
    }
}
