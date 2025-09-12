package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Entity
@Table(name = "sub_class_table")
public class SubClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_class_id")
    private Integer subClassId;

    @Column(name = "sub_class_name", nullable = false)
    private String subClassName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "total_class_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TotalClass totalClass;

    @OneToMany(mappedBy = "stuClass", cascade = CascadeType.ALL)
    private List<Student> students;

    // Getters and Setters
    public Integer getSubClassId() {
        return subClassId;
    }

    public void setSubClassId(Integer subClassId) {
        this.subClassId = subClassId;
    }

    public String getSubClassName() {
        return subClassName;
    }

    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }

    public TotalClass getTotalClass() {
        return totalClass;
    }

    public void setTotalClass(TotalClass totalClass) {
        this.totalClass = totalClass;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    @Override
    public String toString() {
        return "SubClass{" +
                "subClassId=" + subClassId +
                ", subClassName='" + subClassName + '\'' +
                ", totalClass=" + totalClass +
                '}';
    }
}
