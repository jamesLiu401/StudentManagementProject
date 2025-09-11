package com.jamesliu.stumanagement.student_management.Service.TeacherService;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 教师服务实现类
 * 提供教师相关的业务逻辑操作，实现ITeacherService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>教师CRUD操作 - 创建、查询、更新、删除教师</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供教师数量统计</li>
 *   <li>业务验证 - 教师编号唯一性检查、数据完整性验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-09-01
 */
@Service
@Transactional
public class TeacherService implements ITeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    // 基础 CRUD 操作
    @Override
    public Teacher saveTeacher(Teacher teacher) {
        if (teacher.getTeacherNo() != null && existsByTeacherNo(teacher.getTeacherNo())) {
            throw new IllegalArgumentException("教师编号已存在");
        }
        return teacherRepository.save(teacher);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Integer id) {
        return teacherRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Teacher> findAll(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }
    
    @Override
    public void deleteById(Integer id) {
        teacherRepository.deleteById(id);
    }
    
    @Override
    public void deleteTeacher(Teacher teacher) {
        teacherRepository.delete(teacher);
    }
    
    // 查询操作
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByTitle(String title) {
        return teacherRepository.findByTitle(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Teacher> findByTeacherNo(String teacherNo) {
        return teacherRepository.findByTeacherNo(teacherNo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByTeacherNameContaining(String name) {
        return teacherRepository.findByTeacherNameContaining(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findByDepartmentAndTitle(String department, String title) {
        return teacherRepository.findByDepartmentAndTitle(department, title);
    }
    
    // 分页查询
    @Override
    @Transactional(readOnly = true)
    public Page<Teacher> findByDepartment(String department, Pageable pageable) {
        return teacherRepository.findByDepartment(department, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Teacher> findByTitle(String title, Pageable pageable) {
        return teacherRepository.findByTitle(title, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Teacher> findByTeacherNameContaining(String name, Pageable pageable) {
        return teacherRepository.findByTeacherNameContaining(name, pageable);
    }
    
    // 统计操作
    @Override
    @Transactional(readOnly = true)
    public long countByDepartment(String department) {
        return teacherRepository.countByDepartment(department);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByTitle(String title) {
        return teacherRepository.countByTitle(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByDepartmentAndTitle(String department, String title) {
        return teacherRepository.countByDepartmentAndTitle(department, title);
    }
    
    // 业务逻辑操作
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTeacherNo(String teacherNo) {
        return teacherRepository.findByTeacherNo(teacherNo).isPresent();
    }
    
    @Override
    public Teacher createTeacher(String teacherNo, String teacherName, String department, String title) {
        if (existsByTeacherNo(teacherNo)) {
            throw new IllegalArgumentException("教师编号已存在");
        }
        
        Teacher teacher = new Teacher();
        teacher.setTeacherNo(teacherNo);
        teacher.setTeacherName(teacherName);
        teacher.setDepartment(department);
        teacher.setTitle(title);
        
        return saveTeacher(teacher);
    }
    
    @Override
    public Teacher updateTeacher(Integer id, String teacherNo, String teacherName, String department, String title) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("教师不存在"));
        
        // 检查教师编号是否被其他教师使用
        if (teacherNo != null && !teacherNo.equals(teacher.getTeacherNo()) && existsByTeacherNo(teacherNo)) {
            throw new IllegalArgumentException("教师编号已存在");
        }
        
        if (teacherNo != null) teacher.setTeacherNo(teacherNo);
        if (teacherName != null) teacher.setTeacherName(teacherName);
        if (department != null) teacher.setDepartment(department);
        if (title != null) teacher.setTitle(title);
        
        return saveTeacher(teacher);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Teacher> searchTeachers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        return teacherRepository.findByTeacherNameContaining(keyword.trim());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllDepartments() {
        return teacherRepository.findAll().stream()
                .map(Teacher::getDepartment)
                .distinct()
                .filter(dept -> dept != null && !dept.trim().isEmpty())
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> findAllTitles() {
        return teacherRepository.findAll().stream()
                .map(Teacher::getTitle)
                .distinct()
                .filter(title -> title != null && !title.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
