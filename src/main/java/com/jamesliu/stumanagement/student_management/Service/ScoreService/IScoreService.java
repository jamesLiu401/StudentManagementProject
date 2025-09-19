package com.jamesliu.stumanagement.student_management.Service.ScoreService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Score;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 成绩服务接口
 * 定义成绩相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 成绩的增删改查</li>
 *   <li>学生成绩管理 - 查询、录入、更新学生成绩</li>
 *   <li>课程成绩管理 - 查询、统计课程成绩</li>
 *   <li>成绩统计分析 - 平均分、最高分、最低分等</li>
 *   <li>成绩等级管理 - 及格、不及格成绩统计</li>
 *   <li>批量操作 - 批量录入、更新成绩</li>
 * </ul>
 * 
 * <p>业务规则：</p>
 * <ul>
 *   <li>成绩范围：0-100分</li>
 *   <li>及格分数：60分</li>
 *   <li>同一学生同一课程只能有一个成绩记录</li>
 *   <li>成绩录入后可以修改</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-20
 */
public interface IScoreService {
    
    // 基础 CRUD 操作
    Score saveScore(Score score);
    Optional<Score> findById(Integer id);
    List<Score> findAll();
    Page<Score> findAll(Pageable pageable);
    void deleteById(Integer id);
    void deleteScore(Score score);
    
    // 学生成绩操作
    List<Score> findByStudent(Student student);
    Page<Score> findByStudent(Student student, Pageable pageable);
    List<Score> findByStudentId(Integer studentId);
    Optional<Score> findByStudentAndSubject(Student student, Subject subject);
    Optional<Score> findByStudentIdAndSubjectId(Integer studentId, Long subjectId);
    
    // 课程成绩操作
    List<Score> findBySubject(Subject subject);
    Page<Score> findBySubject(Subject subject, Pageable pageable);
    List<Score> findBySubjectId(Long subjectId);
    
    // 成绩范围查询
    List<Score> findByScoreRange(Double minScore, Double maxScore);
    List<Score> findByStudentAndScoreRange(Student student, Double minScore, Double maxScore);
    List<Score> findBySubjectAndScoreRange(Subject subject, Double minScore, Double maxScore);
    
    // 成绩统计
    Double getAverageScoreByStudent(Student student);
    Double getAverageScoreByStudentId(Integer studentId);
    Double getAverageScoreBySubject(Subject subject);
    Double getAverageScoreBySubjectId(Long subjectId);
    
    Double getMaxScoreByStudent(Student student);
    Double getMaxScoreByStudentId(Integer studentId);
    Double getMaxScoreBySubject(Subject subject);
    Double getMaxScoreBySubjectId(Long subjectId);
    
    Double getMinScoreByStudent(Student student);
    Double getMinScoreByStudentId(Integer studentId);
    Double getMinScoreBySubject(Subject subject);
    Double getMinScoreBySubjectId(Long subjectId);
    
    // 成绩等级统计
    List<Score> getPassingScoresByStudent(Student student);
    List<Score> getPassingScoresByStudentId(Integer studentId);
    List<Score> getFailingScoresByStudent(Student student);
    List<Score> getFailingScoresByStudentId(Integer studentId);
    
    long countPassingScoresByStudent(Student student);
    long countPassingScoresByStudentId(Integer studentId);
    long countFailingScoresByStudent(Student student);
    long countFailingScoresByStudentId(Integer studentId);
    
    // 业务逻辑操作
    boolean existsByStudentAndSubject(Student student, Subject subject);
    boolean existsByStudentIdAndSubjectId(Integer studentId, Long subjectId);
    
    Score createScore(Student student, Subject subject, Double score);
    Score updateScore(Integer scoreId, Double newScore);
    Score updateScoreByStudentAndSubject(Student student, Subject subject, Double newScore);
    Score updateScoreByStudentIdAndSubjectId(Integer studentId, Long subjectId, Double newScore);
    
    // 批量操作
    List<Score> batchCreateScores(List<Score> scores);
    List<Score> batchUpdateScores(List<Score> scores);
    
    // 删除操作
    void deleteByStudent(Student student);
    void deleteByStudentId(Integer studentId);
    void deleteBySubject(Subject subject);
    void deleteBySubjectId(Long subjectId);
    
    // 成绩验证
    boolean isValidScore(Double score);
    String getScoreGrade(Double score);
}
