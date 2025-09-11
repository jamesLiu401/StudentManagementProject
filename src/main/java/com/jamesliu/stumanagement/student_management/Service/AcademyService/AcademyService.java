package com.jamesliu.stumanagement.student_management.Service.AcademyService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 学院服务实现类
 * 提供学院相关的业务逻辑操作，实现IAcademyService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>学院CRUD操作 - 创建、查询、更新、删除学院</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供学院数量统计</li>
 *   <li>业务验证 - 学院名称和代码唯一性检查、数据完整性验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-15
 */
@Service
@Transactional
public class AcademyService implements IAcademyService {
    
    @Autowired
    private AcademyRepository academyRepository;
    
    // 基础 CRUD 操作
    @Override
    public Academy saveAcademy(Academy academy) {
        if (academy.getAcademyName() != null && existsByAcademyName(academy.getAcademyName())) {
            throw new IllegalArgumentException("学院名称已存在");
        }
        if (academy.getAcademyCode() != null && existsByAcademyCode(academy.getAcademyCode())) {
            throw new IllegalArgumentException("学院代码已存在");
        }
        return academyRepository.save(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Academy> findById(Integer id) {
        return academyRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> findAll() {
        return academyRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Academy> findAll(Pageable pageable) {
        return academyRepository.findAll(pageable);
    }
    
    @Override
    public void deleteById(Integer id) {
        academyRepository.deleteById(id);
    }
    
    @Override
    public void deleteAcademy(Academy academy) {
        academyRepository.delete(academy);
    }
    
    // 查询操作
    @Override
    @Transactional(readOnly = true)
    public Optional<Academy> findByAcademyName(String academyName) {
        return academyRepository.findByAcademyName(academyName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Academy> findByAcademyCode(String academyCode) {
        return academyRepository.findByAcademyCode(academyCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> findByAcademyNameContaining(String name) {
        return academyRepository.findByAcademyNameContaining(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> findByDeanName(String deanName) {
        return academyRepository.findByDeanName(deanName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> findByAcademyNameContainingAndDeanName(String name, String deanName) {
        return academyRepository.findByAcademyNameContainingAndDeanName(name, deanName);
    }
    
    // 分页查询
    @Override
    @Transactional(readOnly = true)
    public Page<Academy> findByAcademyNameContaining(String name, Pageable pageable) {
        return academyRepository.findByAcademyNameContaining(name, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Academy> findByDeanName(String deanName, Pageable pageable) {
        return academyRepository.findByDeanName(deanName, pageable);
    }
    
    // 统计操作
    @Override
    @Transactional(readOnly = true)
    public long countByAcademyNameContaining(String name) {
        return academyRepository.countByAcademyNameContaining(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByDeanName(String deanName) {
        return academyRepository.countByDeanName(deanName);
    }
    
    // 业务逻辑操作
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAcademyName(String academyName) {
        return academyRepository.findByAcademyName(academyName).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByAcademyCode(String academyCode) {
        return academyRepository.findByAcademyCode(academyCode).isPresent();
    }
    
    @Override
    public Academy createAcademy(String academyName, String academyCode, String description, String deanName, String contactPhone, String address) {
        if (existsByAcademyName(academyName)) {
            throw new IllegalArgumentException("学院名称已存在");
        }
        if (academyCode != null && existsByAcademyCode(academyCode)) {
            throw new IllegalArgumentException("学院代码已存在");
        }
        
        Academy academy = new Academy();
        academy.setAcademyName(academyName);
        academy.setAcademyCode(academyCode);
        academy.setDescription(description);
        academy.setDeanName(deanName);
        academy.setContactPhone(contactPhone);
        academy.setAddress(address);
        
        return saveAcademy(academy);
    }
    
    @Override
    public Academy updateAcademy(Integer id, String academyName, String academyCode, String description, String deanName, String contactPhone, String address) {
        Academy academy = academyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("学院不存在"));
        
        // 检查学院名称是否被其他学院使用
        if (academyName != null && !academyName.equals(academy.getAcademyName()) && existsByAcademyName(academyName)) {
            throw new IllegalArgumentException("学院名称已存在");
        }
        
        // 检查学院代码是否被其他学院使用
        if (academyCode != null && !academyCode.equals(academy.getAcademyCode()) && existsByAcademyCode(academyCode)) {
            throw new IllegalArgumentException("学院代码已存在");
        }
        
        if (academyName != null) academy.setAcademyName(academyName);
        if (academyCode != null) academy.setAcademyCode(academyCode);
        if (description != null) academy.setDescription(description);
        if (deanName != null) academy.setDeanName(deanName);
        if (contactPhone != null) academy.setContactPhone(contactPhone);
        if (address != null) academy.setAddress(address);
        
        return saveAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Academy> searchAcademies(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        return academyRepository.findByAcademyNameContaining(keyword.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllAcademyNames() {
        return academyRepository.findAll().stream()
                .map(Academy::getAcademyName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllAcademyCodes() {
        return academyRepository.findAll().stream()
                .map(Academy::getAcademyCode)
                .filter(code -> code != null && !code.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
