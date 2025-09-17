package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Score;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Service.ScoreService.IScoreService;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.jamesliu.stumanagement.student_management.dto.ScoreDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;

/**
 * 成绩管理控制器
 * 提供成绩相关的 REST API 接口，支持完整的成绩管理功能
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
 * <p>权限控制：</p>
 * <ul>
 *   <li>管理员权限 - 创建、更新、删除成绩</li>
 *   <li>教师权限 - 查询、录入、更新成绩</li>
 *   <li>使用@PreAuthorize注解进行方法级权限控制</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-25
 */
@RestController
@RequestMapping("/scores")
@CrossOrigin(origins = "*")
public class ScoreController {
    
    @Autowired
    private IScoreService scoreService;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    /**
     * 添加成绩
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> addScore(@RequestBody Score score) {
        try {
            Score savedScore = scoreService.saveScore(score);
            return ResponseMessage.success(DtoMapper.toDto(savedScore));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    
    /**
     * 创建成绩（通过学生ID和课程ID）
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> createScore(
            @RequestParam Integer studentId,
            @RequestParam Long subjectId,
            @RequestParam Double score) {
        try {
            Optional<Student> student = studentRepository.findById(studentId);
            Optional<Subject> subject = subjectRepository.findById(subjectId);
            
            if (student.isEmpty()) {
                return ResponseMessage.error("学生不存在");
            }
            if (subject.isEmpty()) {
                return ResponseMessage.error("课程不存在");
            }
            
            Score newScore = scoreService.createScore(student.get(), subject.get(), score);
            return ResponseMessage.success(DtoMapper.toDto(newScore));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    
    /**
     * 更新成绩
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> updateScore(
            @PathVariable Integer id,
            @RequestParam Double newScore) {
        try {
            Score updatedScore = scoreService.updateScore(id, newScore);
            return ResponseMessage.success(DtoMapper.toDto(updatedScore));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    
    /**
     * 通过学生ID和课程ID更新成绩
     */
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> updateScoreByStudentAndSubject(
            @RequestParam Integer studentId,
            @RequestParam Long subjectId,
            @RequestParam Double newScore) {
        try {
            Score updatedScore = scoreService.updateScoreByStudentIdAndSubjectId(studentId, subjectId, newScore);
            return ResponseMessage.success(DtoMapper.toDto(updatedScore));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    
    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteScore(@PathVariable Integer id) {
        scoreService.deleteById(id);
        return ResponseMessage.success("成绩删除成功");
    }
    
    /**
     * 根据ID查询成绩
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> getScoreById(@PathVariable Integer id) {
        Optional<Score> score = scoreService.findById(id);
        return score.map(s -> ResponseMessage.success(DtoMapper.toDto(s)))
                .orElse(ResponseMessage.error("成绩不存在"));
    }
    
    /**
     * 查询所有成绩
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> getAllScores() {
        List<Score> scores = scoreService.findAll();
        return ResponseMessage.success(scores.stream().map(DtoMapper::toDto).toList());
    }
    
    /**
     * 分页查询成绩
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<ScoreDTO>> getScoresByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scoreId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Score> scores = scoreService.findAll(pageable);
        return ResponseMessage.success(scores.map(DtoMapper::toDto));
    }
    
    /**
     * 根据学生ID查询成绩
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> getScoresByStudentId(@PathVariable Integer studentId) {
        List<Score> scores = scoreService.findByStudentId(studentId);
        return ResponseMessage.success(scores.stream().map(DtoMapper::toDto).toList());
    }
    
    /**
     * 根据课程ID查询成绩
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> getScoresBySubjectId(@PathVariable Long subjectId) {
        List<Score> scores = scoreService.findBySubjectId(subjectId);
        return ResponseMessage.success(scores.stream().map(DtoMapper::toDto).toList());
    }
    
    /**
     * 根据学生ID和课程ID查询成绩
     */
    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<ScoreDTO> getScoreByStudentAndSubject(
            @PathVariable Integer studentId,
            @PathVariable Long subjectId) {
        Optional<Score> score = scoreService.findByStudentIdAndSubjectId(studentId, subjectId);
        return score.map(s -> ResponseMessage.success(DtoMapper.toDto(s)))
                .orElse(ResponseMessage.error("成绩不存在"));
    }
    
    /**
     * 获取学生平均分
     */
    @GetMapping("/student/{studentId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Double> getAverageScoreByStudent(@PathVariable Integer studentId) {
        Double average = scoreService.getAverageScoreByStudentId(studentId);
        return ResponseMessage.success(average);
    }
    
    /**
     * 获取课程平均分
     */
    @GetMapping("/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Double> getAverageScoreBySubject(@PathVariable Long subjectId) {
        Double average = scoreService.getAverageScoreBySubjectId(subjectId);
        return ResponseMessage.success(average);
    }
    
    /**
     * 获取学生最高分
     */
    @GetMapping("/student/{studentId}/max")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Double> getMaxScoreByStudent(@PathVariable Integer studentId) {
        Double maxScore = scoreService.getMaxScoreByStudentId(studentId);
        return ResponseMessage.success(maxScore);
    }
    
    /**
     * 获取学生最低分
     */
    @GetMapping("/student/{studentId}/min")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Double> getMinScoreByStudent(@PathVariable Integer studentId) {
        Double minScore = scoreService.getMinScoreByStudentId(studentId);
        return ResponseMessage.success(minScore);
    }
    
    /**
     * 获取学生及格成绩
     */
    @GetMapping("/student/{studentId}/passing")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> getPassingScoresByStudent(@PathVariable Integer studentId) {
        List<Score> scores = scoreService.getPassingScoresByStudentId(studentId);
        return ResponseMessage.success(scores.stream().map(DtoMapper::toDto).toList());
    }
    
    /**
     * 获取学生不及格成绩
     */
    @GetMapping("/student/{studentId}/failing")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> getFailingScoresByStudent(@PathVariable Integer studentId) {
        List<Score> scores = scoreService.getFailingScoresByStudentId(studentId);
        return ResponseMessage.success(scores.stream().map(DtoMapper::toDto).toList());
    }
    
    /**
     * 统计学生及格成绩数量
     */
    @GetMapping("/student/{studentId}/passing/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countPassingScoresByStudent(@PathVariable Integer studentId) {
        Long count = scoreService.countPassingScoresByStudentId(studentId);
        return ResponseMessage.success(count);
    }
    
    /**
     * 统计学生不及格成绩数量
     */
    @GetMapping("/student/{studentId}/failing/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countFailingScoresByStudent(@PathVariable Integer studentId) {
        Long count = scoreService.countFailingScoresByStudentId(studentId);
        return ResponseMessage.success(count);
    }
    
    /**
     * 批量创建成绩
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<ScoreDTO>> batchCreateScores(@RequestBody List<Score> scores) {
        try {
            List<Score> savedScores = scoreService.batchCreateScores(scores);
            return ResponseMessage.success(savedScores.stream().map(DtoMapper::toDto).toList());
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }
    
    /**
     * 获取成绩等级
     */
    @GetMapping("/grade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<String> getScoreGrade(@RequestParam Double score) {
        String grade = scoreService.getScoreGrade(score);
        return ResponseMessage.success(grade);
    }
}
