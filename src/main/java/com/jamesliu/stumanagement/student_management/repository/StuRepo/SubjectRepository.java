package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    // 根据学院查询课程
    List<Subject> findBySubjectAcademy(String academy);
    
    // 根据课程名称模糊查询
    List<Subject> findBySubjectNameContaining(String name);
}
