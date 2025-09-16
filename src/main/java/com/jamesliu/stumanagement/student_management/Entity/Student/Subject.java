package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 课程实体类
 * 管理学校的课程信息，包括课程名称、学分、所属学院等
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>课程基本信息管理 - 课程名称、学分、所属学院</li>
 *   <li>教学计划管理 - 支持按学院和学分查询课程</li>
 *   <li>课程统计功能 - 支持学分统计和课程数量统计</li>
 * </ul>
 * 
 * <p>关联关系：</p>
 * <ul>
 *   <li>多对一：课程属于学院(Academy) - 通过学院ID关联</li>
 *   <li>一对多：课程可以被多个教师教授</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：subject_table</li>
 *   <li>主键：subject_id (自增)</li>
 *   <li>字段：subject_name, academy_id, credit</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-05
 */
@Entity
@Table(name = "subject_table")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @Column(name = "credit")
    private Double credit;

    // Getters and Setters
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Academy getAcademy() {
        return academy;
    }

    public void setAcademy(Academy academy) {
        this.academy = academy;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", academy=" + (academy != null ? academy.getAcademyName() : null) +
                ", credit=" + credit +
                '}';
    }
}
