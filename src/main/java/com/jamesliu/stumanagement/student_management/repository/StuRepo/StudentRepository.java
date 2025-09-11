package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    // 根据姓名模糊查询
    @NonNull
    List<Student> findByStuNameContaining(@NonNull String name);
    
    // 根据班级ID查询
    @NonNull
    List<Student> findByStuClassSubClassId(@NonNull Integer classId);
    
    // 分页查询
    @NonNull
    Page<Student> findAll(@NonNull Pageable pageable);
    
    // 根据专业查询
    @Query("SELECT s FROM Student s WHERE s.stuMajor = :major")
    @NonNull
    List<Student> findByMajor(@Param("major") @NonNull String major);
    
    // 根据年级查询
    @NonNull
    List<Student> findByGrade(@NonNull Integer grade);
    
    // 根据小班查询学生
    @NonNull
    List<Student> findByStuClass(@NonNull SubClass stuClass);
    
    // 统计小班学生数量
    long countByStuClass(@NonNull SubClass stuClass);
}
