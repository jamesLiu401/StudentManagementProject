package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_table")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_academy")
    private String subjectAcademy;

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

    public String getSubjectAcademy() {
        return subjectAcademy;
    }

    public void setSubjectAcademy(String subjectAcademy) {
        this.subjectAcademy = subjectAcademy;
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
                ", subjectAcademy='" + subjectAcademy + '\'' +
                ", credit=" + credit +
                '}';
    }
}
