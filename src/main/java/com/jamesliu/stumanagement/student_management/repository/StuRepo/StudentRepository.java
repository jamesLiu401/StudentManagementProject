package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    // 根据姓名模糊查询
    List<Student> findByStuNameContaining(String name);
    
    // 根据班级ID查询
    List<Student> findByStuClassSubClassId(Integer classId);
    
    // 分页查询
    Page<Student> findAll(Pageable pageable);
    
    // 根据专业查询
    @Query("SELECT s FROM Student s WHERE s.stuMajor = :major")
    List<Student> findByMajor(@Param("major") String major);
    
    // 根据年级查询
    List<Student> findByGrade(Integer grade);
}
