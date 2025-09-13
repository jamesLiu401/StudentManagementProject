package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;

@Entity
@Table(name="score_table")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private int scoreId;

    @ManyToOne
    @JoinColumn(name = "stu_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "stu_score")
    private Double stuScore;

    // Getters and Setters
    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Double getStuScore() {
        return stuScore;
    }

    public void setStuScore(Double stuScore) {
        this.stuScore = stuScore;
    }

    @Override
    public String toString() {
        return "Score{" +
                "scoreId=" + scoreId +
                ", student=" + student +
                ", subject=" + subject +
                ", stuScore=" + stuScore +
                '}';
    }
}
