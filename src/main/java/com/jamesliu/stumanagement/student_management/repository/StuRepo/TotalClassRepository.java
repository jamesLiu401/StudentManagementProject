package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TotalClassRepository extends JpaRepository<TotalClass, Integer> {

    // 根据班级名称和专业查询
    @NonNull
    @EntityGraph(attributePaths = {"major","subClasses"})
    Optional<TotalClass> findByTotalClassNameAndMajor(@NonNull String totalClassName, @NonNull Major major);

    // 根据班级名称查询
    @NonNull
    @EntityGraph(attributePaths = {"major","subClasses"})
    List<TotalClass> findByTotalClassName(@NonNull String totalClassName);

    // 根据专业查询
    @NonNull
    @EntityGraph(attributePaths = {"major","subClasses"})
    List<TotalClass> findByMajor(@NonNull Major major);

    // 根据班级名称模糊查询
    @NonNull
    @EntityGraph(attributePaths = {"major","subClasses"})
    List<TotalClass> findByTotalClassNameContaining(@NonNull String name);

    @NonNull
    @EntityGraph(attributePaths = {"major","subClasses"})
    TotalClass findByTotalClassId(@NonNull Integer totalClassId);
}
