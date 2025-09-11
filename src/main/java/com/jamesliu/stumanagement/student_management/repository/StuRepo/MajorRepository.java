package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {
    
    // 根据专业名称和年级查询
    @NonNull
    Optional<Major> findByMajorNameAndGrade(@NonNull String majorName, @NonNull Integer grade);
    
    // 根据专业名称查询
    @NonNull
    List<Major> findByMajorName(@NonNull String majorName);
    
    // 根据学院查询
    @NonNull
    List<Major> findByAcademy(@NonNull Academy academy);
    
    // 根据年级查询
    @NonNull
    List<Major> findByGrade(@NonNull Integer grade);
}
