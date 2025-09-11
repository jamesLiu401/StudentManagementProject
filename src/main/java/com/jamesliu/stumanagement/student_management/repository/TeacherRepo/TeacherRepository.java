package com.jamesliu.stumanagement.student_management.repository.TeacherRepo;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    
    // 根据教师编号查询
    @NonNull
    Optional<Teacher> findByTeacherNo(@NonNull String teacherNo);
    
    // 根据姓名模糊查询
    @NonNull
    List<Teacher> findByTeacherNameContaining(@NonNull String name);
    
    // 根据部门查询
    @NonNull
    List<Teacher> findByDepartment(@NonNull String department);
    
    // 根据职称查询
    @NonNull
    List<Teacher> findByTitle(@NonNull String title);
    
    // 根据部门和职称查询
    @NonNull
    List<Teacher> findByDepartmentAndTitle(@NonNull String department, @NonNull String title);
    
    // 根据部门分页查询
    @NonNull
    Page<Teacher> findByDepartment(@NonNull String department, @NonNull Pageable pageable);
    
    // 根据职称分页查询
    @NonNull
    Page<Teacher> findByTitle(@NonNull String title, @NonNull Pageable pageable);
    
    // 根据教师姓名模糊分页查询
    @NonNull
    Page<Teacher> findByTeacherNameContaining(@NonNull String teacherName, @NonNull Pageable pageable);
    
    // 统计方法
    long countByDepartment(@NonNull String department);
    long countByTitle(@NonNull String title);
    long countByDepartmentAndTitle(@NonNull String department, @NonNull String title);
}
