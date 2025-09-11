package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClassRepository extends JpaRepository<SubClass, Integer> {
    
    // 根据班级名称和大班查询
    @NonNull
    Optional<SubClass> findBySubClassNameAndTotalClass(@NonNull String subClassName, @NonNull TotalClass totalClass);
    
    // 根据班级名称查询
    @NonNull
    List<SubClass> findBySubClassName(@NonNull String subClassName);
    
    // 根据大班查询
    @NonNull
    List<SubClass> findByTotalClass(@NonNull TotalClass totalClass);
}
