package com.jamesliu.stumanagement.student_management.Entity.Student;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import jakarta.persistence.*;
import java.util.List;

/**
 * 专业实体类
 * 管理学校的各个专业信息，是学院的下级组织结构
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>专业基本信息管理 - 专业名称、年级、辅导员信息</li>
 *   <li>层级关系管理 - 专业属于学院，包含多个班级</li>
 *   <li>教学管理 - 管理专业的教学相关信息</li>
 * </ul>
 * 
 * <p>关联关系：</p>
 * <ul>
 *   <li>多对一：多个专业属于一个学院(Academy)</li>
 *   <li>一对多：一个专业包含多个大班(TotalClass)</li>
 *   <li>多对一：专业有辅导员(Teacher)</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：major_table</li>
 *   <li>主键：major_id (自增)</li>
 *   <li>外键：academy_id, counselor_id</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-03
 */
@Entity
@Table(name = "major_table")
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Integer majorId;

    @Column(name = "major_name", nullable = false)
    private String majorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id")
    private Academy academy;

    @Column(name = "grade")
    private Integer grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counselor_id")
    private Teacher counselor;

    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL)
    private List<TotalClass> totalClasses;

    // Getters and Setters
    public Integer getMajorId() {
        return majorId;
    }

    public void setMajorId(Integer majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public Academy getAcademy() {
        return academy;
    }

    public void setAcademy(Academy academy) {
        this.academy = academy;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Teacher getCounselor() {
        return counselor;
    }

    public void setCounselor(Teacher counselor) {
        this.counselor = counselor;
    }

    public List<TotalClass> getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(List<TotalClass> totalClasses) {
        this.totalClasses = totalClasses;
    }

    @Override
    public String toString() {
        return "Major{" +
                "majorId=" + majorId +
                ", majorName='" + majorName + '\'' +
                ", academy='" + academy + '\'' +
                ", grade=" + grade +
                ", counselor=" + counselor +
                '}';
    }
}
