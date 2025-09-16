package com.jamesliu.stumanagement.student_management.Service.MajorService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.MajorRepository;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.jamesliu.stumanagement.student_management.dto.MajorDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;

/**
 * 专业服务实现类
 * 提供专业相关的业务逻辑操作，实现IMajorService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>专业CRUD操作 - 创建、查询、更新、删除专业</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供专业数量统计</li>
 *   <li>业务验证 - 专业名称和年级唯一性检查、数据完整性验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-01
 */
@Service
@Transactional
public class MajorService implements IMajorService {
    
    @Autowired
    private MajorRepository majorRepository;
    
    @Autowired
    private AcademyRepository academyRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    // 基础 CRUD 操作
    @Override
    public Major saveMajor(Major major) {
        if (major.getMajorName() != null && major.getGrade() != null && 
            existsByMajorNameAndGrade(major.getMajorName(), major.getGrade())) {
            throw new IllegalArgumentException("该年级的专业名称已存在");
        }
        Optional<Academy> o_academy = academyRepository.findByAcademyId(major.getAcademy().getAcademyId());
        o_academy.ifPresent(major::setAcademy);
        Optional<Teacher> o_teacher = teacherRepository.findById(major.getCounselor().getTeacherId());
        o_teacher.ifPresent(major::setCounselor);
        return majorRepository.save(major);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MajorDTO> findById(Integer id) {
        return majorRepository.findById(id).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findAll() {
        return majorRepository.findAll().stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MajorDTO> findAll(Pageable pageable) {
        return majorRepository.findAll(pageable).map(DtoMapper::toDto);
    }
    
    @Override
    public void deleteById(Integer id) {
        majorRepository.deleteById(id);
    }
    
    @Override
    public void deleteMajor(Major major) {
        majorRepository.delete(major);
    }
    
    // 查询操作
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByAcademy(Academy academy) {
        return majorRepository.findByAcademy(academy).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByGrade(Integer grade) {
        return majorRepository.findByGrade(grade).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByCounselor(Teacher counselor) {
        return majorRepository.findByCounselor(counselor).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByMajorNameContaining(String name) {
        return majorRepository.findByMajorNameContaining(name).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByAcademyAndGrade(Academy academy, Integer grade) {
        return majorRepository.findByAcademyAndGrade(academy, grade).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> findByAcademyAndMajorNameContaining(Academy academy, String name) {
        return majorRepository.findByAcademyAndMajorNameContaining(academy, name).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MajorDTO> findByMajorNameAndGrade(String majorName, Integer grade) {
        return majorRepository.findByMajorNameAndGrade(majorName, grade).map(DtoMapper::toDto);
    }
    
    // 分页查询
    @Override
    @Transactional(readOnly = true)
    public Page<MajorDTO> findByAcademy(Academy academy, Pageable pageable) {
        return majorRepository.findByAcademy(academy, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MajorDTO> findByGrade(Integer grade, Pageable pageable) {
        return majorRepository.findByGrade(grade, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MajorDTO> findByCounselor(Teacher counselor, Pageable pageable) {
        return majorRepository.findByCounselor(counselor, pageable).map(DtoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<MajorDTO> findByMajorNameContaining(String name, Pageable pageable) {
        return majorRepository.findByMajorNameContaining(name, pageable).map(DtoMapper::toDto);
    }
    
    // 统计操作
    @Override
    @Transactional(readOnly = true)
    public long countByAcademy(Academy academy) {
        return majorRepository.countByAcademy(academy);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByGrade(Integer grade) {
        return majorRepository.countByGrade(grade);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByCounselor(Teacher counselor) {
        return majorRepository.countByCounselor(counselor);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByAcademyAndGrade(Academy academy, Integer grade) {
        return majorRepository.countByAcademyAndGrade(academy, grade);
    }
    
    // 业务逻辑操作
    @Override
    @Transactional(readOnly = true)
    public boolean existsByMajorNameAndGrade(String majorName, Integer grade) {
        return majorRepository.findByMajorNameAndGrade(majorName, grade).isPresent();
    }
    
    @Override
    public Major createMajor(String majorName, Academy academy, Integer grade, Teacher counselor) {
        if (existsByMajorNameAndGrade(majorName, grade)) {
            throw new IllegalArgumentException("该年级的专业名称已存在");
        }
        
        Major major = new Major();
        major.setMajorName(majorName);
        major.setAcademy(academy);
        major.setGrade(grade);
        major.setCounselor(counselor);
        
        return saveMajor(major);
    }
    
    @Override
    public Major updateMajor(Integer id, String majorName, Academy academy, Integer grade, Teacher counselor) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("专业不存在"));
        
        // 检查专业名称和年级组合是否被其他专业使用
        if (majorName != null && grade != null && 
            !(majorName.equals(major.getMajorName()) && grade.equals(major.getGrade())) &&
            existsByMajorNameAndGrade(majorName, grade)) {
            throw new IllegalArgumentException("该年级的专业名称已存在");
        }
        
        if (majorName != null) major.setMajorName(majorName);
        if (academy != null) major.setAcademy(academy);
        if (grade != null) major.setGrade(grade);
        if (counselor != null) major.setCounselor(counselor);
        
        return saveMajor(major);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> searchMajors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return majorRepository.findByMajorNameContaining(keyword.trim()).stream().map(DtoMapper::toDto).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> getMajorsByAcademyId(Integer academyId) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        return academy.map(this::findByAcademy).orElse(List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MajorDTO> getMajorsByGrade(Integer grade) {
        return findByGrade(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Major> findEntityById(Integer id) {
        return majorRepository.findById(id);
    }
}
