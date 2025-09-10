package com.jamesliu.stumanagement.student_management.repository.TeacherRepo;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    
    // 根据教师编号查询
    Optional<Teacher> findByTeacherNo(String teacherNo);
    
    // 根据姓名模糊查询
    List<Teacher> findByTeacherNameContaining(String name);
    
    // 根据部门查询
    List<Teacher> findByDepartment(String department);
}
