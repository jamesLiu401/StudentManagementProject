package com.jamesliu.stumanagement.student_management.Service.ClassService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import com.jamesliu.stumanagement.student_management.dto.SubClassDTO;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.MajorRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubClassRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.TotalClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 班级服务实现类
 * 提供班级相关的业务逻辑操作，实现IClassService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>班级CRUD操作 - 创建、查询、更新、删除班级</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供班级数量统计</li>
 *   <li>业务验证 - 班级名称唯一性检查、数据完整性验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-16
 */
@Service
@Transactional
public class ClassService implements IClassService {
    
    @Autowired
    private TotalClassRepository totalClassRepository;
    
    @Autowired
    private SubClassRepository subClassRepository;
    
    @Autowired
    private MajorRepository majorRepository;
    
    // TotalClass 相关操作
    @Override
    public TotalClass saveTotalClass(TotalClass totalClass) {
        return totalClassRepository.save(totalClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TotalClass> findTotalClassById(Integer id) {
        return totalClassRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TotalClass> findAllTotalClasses() {
        return totalClassRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TotalClass> findAllTotalClasses(Pageable pageable) {
        return totalClassRepository.findAll(pageable);
    }
    
    @Override
    public void deleteTotalClassById(Integer id) {
        totalClassRepository.deleteById(id);
    }
    
    @Override
    public void deleteTotalClass(TotalClass totalClass) {
        totalClassRepository.delete(totalClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TotalClass> findTotalClassesByMajor(Major major) {
        return totalClassRepository.findByMajor(major);
    }

    @Override
    @Transactional
    public SubClass saveSubClassDTO(SubClassDTO subClassDTO){
        TotalClass m_totalClass = totalClassRepository.findByTotalClassId(subClassDTO.getTotalClassId());
        SubClass subClass = new SubClass();
        subClass.setSubClassName(subClassDTO.getSubClassName());
        subClass.setTotalClass(m_totalClass);
        return subClassRepository.save(subClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TotalClass> findTotalClassesByMajorId(Integer majorId) {
        Optional<Major> major = majorRepository.findById(majorId);
        return major.map(this::findTotalClassesByMajor).orElse(List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TotalClass> findByTotalClassNameAndMajor(String totalClassName, Major major) {
        return totalClassRepository.findByTotalClassNameAndMajor(totalClassName, major);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TotalClass> findByTotalClassNameContaining(String name) {
        return totalClassRepository.findByTotalClassNameContaining(name);
    }
    
    // SubClass 相关操作
    @Override
    public SubClass saveSubClass(SubClass subClass) {
        return subClassRepository.save(subClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SubClass> findSubClassById(Integer id) {
        return subClassRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubClass> findAllSubClasses() {
        return subClassRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SubClass> findAllSubClasses(Pageable pageable) {
        return subClassRepository.findAll(pageable);
    }
    
    @Override
    public void deleteSubClassById(Integer id) {
        subClassRepository.deleteById(id);
    }
    
    @Override
    public void deleteSubClass(SubClass subClass) {
        subClassRepository.delete(subClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubClass> findSubClassesByTotalClass(TotalClass totalClass) {
        return subClassRepository.findByTotalClass(totalClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubClass> findSubClassesByTotalClassId(Integer totalClassId) {
        Optional<TotalClass> totalClass = totalClassRepository.findById(totalClassId);
        return totalClass.map(this::findSubClassesByTotalClass).orElse(List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SubClass> findBySubClassNameAndTotalClass(String subClassName, TotalClass totalClass) {
        return subClassRepository.findBySubClassNameAndTotalClass(subClassName, totalClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubClass> findBySubClassNameContaining(String name) {
        return subClassRepository.findBySubClassNameContaining(name);
    }
    
    // 业务逻辑操作
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTotalClassNameAndMajor(String totalClassName, Major major) {
        return totalClassRepository.findByTotalClassNameAndMajor(totalClassName, major).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsBySubClassNameAndTotalClass(String subClassName, TotalClass totalClass) {
        return subClassRepository.findBySubClassNameAndTotalClass(subClassName, totalClass).isPresent();
    }
    
    @Override
    public TotalClass createTotalClass(String totalClassName, Major major) {
        if (existsByTotalClassNameAndMajor(totalClassName, major)) {
            throw new IllegalArgumentException("该专业下的大班名称已存在");
        }
        
        TotalClass totalClass = new TotalClass();
        totalClass.setTotalClassName(totalClassName);
        totalClass.setMajor(major);
        
        return saveTotalClass(totalClass);
    }
    
    @Override
    public SubClass createSubClass(String subClassName, TotalClass totalClass) {
        if (existsBySubClassNameAndTotalClass(subClassName, totalClass)) {
            throw new IllegalArgumentException("该大班下的小班名称已存在");
        }
        
        SubClass subClass = new SubClass();
        subClass.setSubClassName(subClassName);
        subClass.setTotalClass(totalClass);
        
        return saveSubClass(subClass);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TotalClass> searchTotalClasses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllTotalClasses();
        }
        
        return totalClassRepository.findByTotalClassNameContaining(keyword.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubClass> searchSubClasses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllSubClasses();
        }
        
        return subClassRepository.findBySubClassNameContaining(keyword.trim());
    }
}
