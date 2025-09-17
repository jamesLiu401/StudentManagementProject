package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Student.Score;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 成绩数据访问层
 * 提供成绩相关的数据库操作接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 成绩的增删改查</li>
 *   <li>学生成绩查询 - 按学生查询所有成绩</li>
 *   <li>课程成绩查询 - 按课程查询所有成绩</li>
 *   <li>成绩统计查询 - 平均分、最高分、最低分等</li>
 *   <li>分页查询 - 支持分页和排序</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-03
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    
    // 预加载关联数据的查询
    @NonNull
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Optional<Score> findById(@NonNull Integer id);
    
    // 预加载关联数据的分页查询
    @NonNull
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Page<Score> findAll(@NonNull Pageable pageable);
    
    // 基础查询
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findByStudent(Student student);
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findBySubject(Subject subject);
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Optional<Score> findByStudentAndSubject(Student student, Subject subject);
    
    // 分页查询
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Page<Score> findByStudent(Student student, Pageable pageable);
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Page<Score> findBySubject(Subject subject, Pageable pageable);
    
    // 成绩范围查询
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findByStuScoreBetween(Double minScore, Double maxScore);
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findByStudentAndStuScoreBetween(Student student, Double minScore, Double maxScore);
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findBySubjectAndStuScoreBetween(Subject subject, Double minScore, Double maxScore);
    
    // 统计查询
    @Query("SELECT AVG(s.stuScore) FROM Score s WHERE s.student = :student")
    Double findAverageScoreByStudent(@Param("student") Student student);
    
    @Query("SELECT AVG(s.stuScore) FROM Score s WHERE s.subject = :subject")
    Double findAverageScoreBySubject(@Param("subject") Subject subject);
    
    @Query("SELECT MAX(s.stuScore) FROM Score s WHERE s.student = :student")
    Double findMaxScoreByStudent(@Param("student") Student student);
    
    @Query("SELECT MIN(s.stuScore) FROM Score s WHERE s.student = :student")
    Double findMinScoreByStudent(@Param("student") Student student);
    
    @Query("SELECT MAX(s.stuScore) FROM Score s WHERE s.subject = :subject")
    Double findMaxScoreBySubject(@Param("subject") Subject subject);
    
    @Query("SELECT MIN(s.stuScore) FROM Score s WHERE s.subject = :subject")
    Double findMinScoreBySubject(@Param("subject") Subject subject);
    
    // 计数查询
    long countByStudent(Student student);
    long countBySubject(Subject subject);
    long countByStudentAndStuScoreBetween(Student student, Double minScore, Double maxScore);
    long countBySubjectAndStuScoreBetween(Subject subject, Double minScore, Double maxScore);
    
    // 成绩等级查询
    @Query("SELECT s FROM Score s WHERE s.student = :student AND s.stuScore >= :minScore")
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findPassingScoresByStudent(@Param("student") Student student, @Param("minScore") Double minScore);
    
    @Query("SELECT s FROM Score s WHERE s.student = :student AND s.stuScore < :minScore")
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findFailingScoresByStudent(@Param("student") Student student, @Param("minScore") Double minScore);
    
    // 按学生ID和课程ID查询
    @Query("SELECT s FROM Score s WHERE s.student.stuId = :studentId AND s.subject.subjectId = :subjectId")
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    Optional<Score> findByStudentIdAndSubjectId(@Param("studentId") Integer studentId, @Param("subjectId") Long subjectId);
    
    // 按学生ID查询所有成绩
    @Query("SELECT s FROM Score s WHERE s.student.stuId = :studentId")
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findByStudentId(@Param("studentId") Integer studentId);
    
    // 按课程ID查询所有成绩
    @Query("SELECT s FROM Score s WHERE s.subject.subjectId = :subjectId")
    @EntityGraph(attributePaths = {"student", "student.stuMajor", "student.stuClassId", "subject", "subject.academy"})
    List<Score> findBySubjectId(@Param("subjectId") Long subjectId);
    
    // 删除学生所有成绩
    void deleteByStudent(Student student);
    
    // 删除课程所有成绩
    void deleteBySubject(Subject subject);
}
