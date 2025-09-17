package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {

    // 根据专业名称和年级查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    Optional<Major> findByMajorNameAndGrade(@NonNull String majorName, @NonNull Integer grade);

    // 根据专业名称查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByMajorName(@NonNull String majorName);

    // 根据学院查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByAcademy(@NonNull Academy academy);

    // 根据学院分页查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    Page<Major> findByAcademy(@NonNull Academy academy, @NonNull Pageable pageable);

    // 根据年级查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByGrade(@NonNull Integer grade);

    // 根据年级分页查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    Page<Major> findByGrade(@NonNull Integer grade, @NonNull Pageable pageable);

    // 根据专业名称模糊查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByMajorNameContaining(@NonNull String name);

    // 根据学院和专业名称查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByAcademyAndMajorNameContaining(@NonNull Academy academy, @NonNull String name);

    // 根据学院和年级查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByAcademyAndGrade(@NonNull Academy academy, @NonNull Integer grade);

    // 根据辅导员查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    List<Major> findByCounselor(@NonNull Teacher counselor);

    // 根据辅导员分页查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    Page<Major> findByCounselor(@NonNull Teacher counselor, @NonNull Pageable pageable);

    // 根据专业名称模糊分页查询
    @NonNull
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    Page<Major> findByMajorNameContaining(@NonNull String name, @NonNull Pageable pageable);

    // 覆盖基础方法以显式加载关联
    @Override
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    @NonNull
    Page<Major> findAll(@NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    @NonNull
    List<Major> findAll();

    @Override
    @EntityGraph(attributePaths = {"academy", "counselor", "totalClasses"})
    @NonNull
    Optional<Major> findById(@NonNull Integer id);

    // 统计方法
    long countByCounselor(@NonNull Teacher counselor);
    long countByAcademy(@NonNull Academy academy);
    long countByGrade(@NonNull Integer grade);
    long countByAcademyAndGrade(@NonNull Academy academy, @NonNull Integer grade);
}
