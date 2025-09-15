package com.jamesliu.stumanagement.student_management.Entity.Teacher;
import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "teacher_table")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "teacher_name", nullable = false)
    private String teacherName;

    @Column(name = "teacher_no", unique = true, nullable = false)
    private String teacherNo;

    @Column(name = "department")
    private String department;

    @Column(name = "title")
    private String title; // 职称

    @OneToMany(mappedBy = "counselor")
    @JsonIgnore
    private List<Major> majors;

    @ManyToMany
    @JoinTable(
        name = "teacher_subject_class",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_class_id")
    )
    @JsonIgnore
    private List<SubjectClass> subjectClasses;

    // Getters and Setters
    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Major> getMajors() {
        return majors;
    }

    public void setMajors(List<Major> majors) {
        this.majors = majors;
    }

    public List<SubjectClass> getSubjectClasses() {
        return subjectClasses;
    }

    public void setSubjectClasses(List<SubjectClass> subjectClasses) {
        this.subjectClasses = subjectClasses;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", teacherName='" + teacherName + '\'' +
                ", teacherNo='" + teacherNo + '\'' +
                ", department='" + department + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
