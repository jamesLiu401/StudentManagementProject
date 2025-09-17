package com.jamesliu.stumanagement.student_management.Service.ScoreService;

import com.jamesliu.stumanagement.student_management.Entity.Student.Score;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.ScoreRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 成绩服务实现类
 * 提供成绩相关的业务逻辑操作，实现IScoreService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>成绩CRUD操作 - 创建、查询、更新、删除成绩</li>
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
 * @since 2025-01-01
 */
@Service
@Transactional
public class ScoreService implements IScoreService {
    
    @Autowired
    private ScoreRepository scoreRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    // 基础 CRUD 操作
    @Override
    public Score saveScore(Score score) {
        if (!isValidScore(score.getStuScore())) {
            throw new IllegalArgumentException("成绩必须在0-100分之间");
        }
        Score savedScore = scoreRepository.save(score);
        // 重新查询以预加载关联数据，避免懒加载异常
        return scoreRepository.findById(savedScore.getScoreId())
                .orElse(savedScore);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Score> findById(Integer id) {
        return scoreRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> findAll() {
        return scoreRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Score> findAll(Pageable pageable) {
        return scoreRepository.findAll(pageable);
    }
    
    @Override
    public void deleteById(Integer id) {
        scoreRepository.deleteById(id);
    }
    
    @Override
    public void deleteScore(Score score) {
        scoreRepository.delete(score);
    }
    
    // 学生成绩操作
    @Override
    @Transactional(readOnly = true)
    public List<Score> findByStudent(Student student) {
        return scoreRepository.findByStudent(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Score> findByStudent(Student student, Pageable pageable) {
        return scoreRepository.findByStudent(student, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> findByStudentId(Integer studentId) {
        return scoreRepository.findByStudentId(studentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Score> findByStudentAndSubject(Student student, Subject subject) {
        return scoreRepository.findByStudentAndSubject(student, subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Score> findByStudentIdAndSubjectId(Integer studentId, Long subjectId) {
        return scoreRepository.findByStudentIdAndSubjectId(studentId, subjectId);
    }
    
    // 课程成绩操作
    @Override
    @Transactional(readOnly = true)
    public List<Score> findBySubject(Subject subject) {
        return scoreRepository.findBySubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Score> findBySubject(Subject subject, Pageable pageable) {
        return scoreRepository.findBySubject(subject, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> findBySubjectId(Long subjectId) {
        return scoreRepository.findBySubjectId(subjectId);
    }
    
    // 成绩范围查询
    @Override
    @Transactional(readOnly = true)
    public List<Score> findByScoreRange(Double minScore, Double maxScore) {
        return scoreRepository.findByStuScoreBetween(minScore, maxScore);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> findByStudentAndScoreRange(Student student, Double minScore, Double maxScore) {
        return scoreRepository.findByStudentAndStuScoreBetween(student, minScore, maxScore);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> findBySubjectAndScoreRange(Subject subject, Double minScore, Double maxScore) {
        return scoreRepository.findBySubjectAndStuScoreBetween(subject, minScore, maxScore);
    }
    
    // 成绩统计
    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreByStudent(Student student) {
        return scoreRepository.findAverageScoreByStudent(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::getAverageScoreByStudent).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreBySubject(Subject subject) {
        return scoreRepository.findAverageScoreBySubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageScoreBySubjectId(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        return subject.map(this::getAverageScoreBySubject).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMaxScoreByStudent(Student student) {
        return scoreRepository.findMaxScoreByStudent(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMaxScoreByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::getMaxScoreByStudent).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMaxScoreBySubject(Subject subject) {
        return scoreRepository.findMaxScoreBySubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMaxScoreBySubjectId(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        return subject.map(this::getMaxScoreBySubject).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMinScoreByStudent(Student student) {
        return scoreRepository.findMinScoreByStudent(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMinScoreByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::getMinScoreByStudent).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMinScoreBySubject(Subject subject) {
        return scoreRepository.findMinScoreBySubject(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getMinScoreBySubjectId(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        return subject.map(this::getMinScoreBySubject).orElse(null);
    }
    
    // 成绩等级统计
    @Override
    @Transactional(readOnly = true)
    public List<Score> getPassingScoresByStudent(Student student) {
        return scoreRepository.findPassingScoresByStudent(student, 60.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> getPassingScoresByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::getPassingScoresByStudent).orElse(List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> getFailingScoresByStudent(Student student) {
        return scoreRepository.findFailingScoresByStudent(student, 60.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Score> getFailingScoresByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::getFailingScoresByStudent).orElse(List.of());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPassingScoresByStudent(Student student) {
        return scoreRepository.countByStudentAndStuScoreBetween(student, 60.0, 100.0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countPassingScoresByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::countPassingScoresByStudent).orElse(0L);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countFailingScoresByStudent(Student student) {
        return scoreRepository.countByStudentAndStuScoreBetween(student, 0.0, 59.9);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countFailingScoresByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        return student.map(this::countFailingScoresByStudent).orElse(0L);
    }
    
    // 业务逻辑操作
    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentAndSubject(Student student, Subject subject) {
        return scoreRepository.findByStudentAndSubject(student, subject).isPresent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndSubjectId(Integer studentId, Long subjectId) {
        return scoreRepository.findByStudentIdAndSubjectId(studentId, subjectId).isPresent();
    }
    
    @Override
    public Score createScore(Student student, Subject subject, Double score) {
        if (!isValidScore(score)) {
            throw new IllegalArgumentException("成绩必须在0-100分之间");
        }
        
        if (existsByStudentAndSubject(student, subject)) {
            throw new IllegalArgumentException("该学生该课程已有成绩记录");
        }
        
        Score newScore = new Score();
        newScore.setStudent(student);
        newScore.setSubject(subject);
        newScore.setStuScore(score);
        
        return saveScore(newScore);
    }
    
    @Override
    public Score updateScore(Integer scoreId, Double newScore) {
        if (!isValidScore(newScore)) {
            throw new IllegalArgumentException("成绩必须在0-100分之间");
        }
        
        Score score = scoreRepository.findById(scoreId)
                .orElseThrow(() -> new IllegalArgumentException("成绩记录不存在"));
        
        score.setStuScore(newScore);
        return saveScore(score);
    }
    
    @Override
    public Score updateScoreByStudentAndSubject(Student student, Subject subject, Double newScore) {
        if (!isValidScore(newScore)) {
            throw new IllegalArgumentException("成绩必须在0-100分之间");
        }
        
        Score score = scoreRepository.findByStudentAndSubject(student, subject)
                .orElseThrow(() -> new IllegalArgumentException("成绩记录不存在"));
        
        score.setStuScore(newScore);
        return saveScore(score);
    }
    
    @Override
    public Score updateScoreByStudentIdAndSubjectId(Integer studentId, Long subjectId, Double newScore) {
        if (!isValidScore(newScore)) {
            throw new IllegalArgumentException("成绩必须在0-100分之间");
        }
        
        Score score = scoreRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new IllegalArgumentException("成绩记录不存在"));
        
        score.setStuScore(newScore);
        return saveScore(score);
    }
    
    // 批量操作
    @Override
    @Transactional
    public List<Score> batchCreateScores(List<Score> scores) {
        for (Score score : scores) {
            if (!isValidScore(score.getStuScore())) {
                throw new IllegalArgumentException("成绩必须在0-100分之间");
            }
        }
        return scoreRepository.saveAll(scores);
    }
    
    @Override
    @Transactional
    public List<Score> batchUpdateScores(List<Score> scores) {
        for (Score score : scores) {
            if (!isValidScore(score.getStuScore())) {
                throw new IllegalArgumentException("成绩必须在0-100分之间");
            }
        }
        return scoreRepository.saveAll(scores);
    }
    
    // 删除操作
    @Override
    public void deleteByStudent(Student student) {
        scoreRepository.deleteByStudent(student);
    }
    
    @Override
    public void deleteByStudentId(Integer studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        student.ifPresent(this::deleteByStudent);
    }
    
    @Override
    public void deleteBySubject(Subject subject) {
        scoreRepository.deleteBySubject(subject);
    }
    
    @Override
    public void deleteBySubjectId(Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        subject.ifPresent(this::deleteBySubject);
    }
    
    // 成绩验证
    @Override
    public boolean isValidScore(Double score) {
        return score != null && score >= 0.0 && score <= 100.0;
    }
    
    @Override
    public String getScoreGrade(Double score) {
        if (score == null) {
            return "无成绩";
        }
        
        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 70) {
            return "中等";
        } else if (score >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }
}
