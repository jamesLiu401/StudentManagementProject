package com.jamesliu.stumanagement.student_management.Entity.Teacher;

import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import jakarta.persistence.*;

@Entity
@Table(name = "subject_class_table")
public class SubjectClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_class_id")
    private Integer subjectClassId;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "sub_class_id")
    private SubClass subClass;

    @Column(name = "semester")
    private String semester;

    @Column(name = "school_year")
    private String schoolYear;

    // Getters and Setters
    public Integer getSubjectClassId() {
        return subjectClassId;
    }

    public void setSubjectClassId(Integer subjectClassId) {
        this.subjectClassId = subjectClassId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public SubClass getSubClass() {
        return subClass;
    }

    public void setSubClass(SubClass subClass) {
        this.subClass = subClass;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    @Override
    public String toString() {
        return "SubjectClass{" +
                "subjectClassId=" + subjectClassId +
                ", subject=" + subject +
                ", subClass=" + subClass +
                ", semester='" + semester + '\'' +
                ", schoolYear='" + schoolYear + '\'' +
                '}';
    }
}
