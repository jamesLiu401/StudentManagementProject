package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "total_class_table")
public class TotalClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "total_class_id")
    private Integer totalClassId;

    @Column(name = "total_class_name", nullable = false)
    private String totalClassName;

    @ManyToOne
    @JoinColumn(name = "major_id")
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    //@JsonIgnore
    private Major major;

    @OneToMany(mappedBy = "totalClass", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SubClass> subClasses;

    // Getters and Setters
    public Integer getTotalClassId() {
        return totalClassId;
    }

    public void setTotalClassId(Integer totalClassId) {
        this.totalClassId = totalClassId;
    }

    public String getTotalClassName() {
        return totalClassName;
    }

    public void setTotalClassName(String totalClassName) {
        this.totalClassName = totalClassName;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public List<SubClass> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(List<SubClass> subClasses) {
        this.subClasses = subClasses;
    }

    @Override
    public String toString() {
        return "TotalClass{" +
                "totalClassId=" + totalClassId +
                ", totalClassName='" + totalClassName + '\'' +
                ", major=" + getMajorNameSafely() +
                '}';
    }
    
    /**
     * 安全地获取专业名称，避免懒加载异常
     */
    private String getMajorNameSafely() {
        if (major == null) {
            return "null";
        }
        try {
            return major.getMajorName();
        } catch (Exception e) {
            return "LazyLoaded";
        }
    }
}
