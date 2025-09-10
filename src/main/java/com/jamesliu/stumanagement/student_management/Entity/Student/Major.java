package com.jamesliu.stumanagement.student_management.Entity.Student;

import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "major_table")
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Integer majorId;

    @Column(name = "major_name", nullable = false)
    private String majorName;

    @Column(name = "academy", nullable = false)
    private String academy;

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

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
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
