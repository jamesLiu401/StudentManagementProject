package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;

@Entity
@Table(name="stu_table")
public class Student {
    //学号
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="stu_id")
    private Integer stuId;

    //姓名
    @Column(name="stu_name", nullable = false)
    private String stuName;

    //性别
    @Column(name="stu_gender")
    private boolean stuGender;

    //专业
    @Column(name="stu_major")
    private String stuMajor;

    //班级
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SubClass stuClass;

    //年级
    @Column(name="stu_grade")
    private Integer grade;

    //电话
    @Column(name="stu_telephone_no")
    private String stuTel;

    //地址
    @Column(name="stu_address")
    private String stuAddress;

    // Getters and Setters
    public Integer getStuId() {
        return stuId;
    }

    public void setStuId(Integer stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public boolean isStuGender() {
        return stuGender;
    }

    public void setStuGender(boolean stuGender) {
        this.stuGender = stuGender;
    }

    public String getStuMajor() {
        return stuMajor;
    }

    public void setStuMajor(String stuMajor) {
        this.stuMajor = stuMajor;
    }

    public SubClass getStuClass() {
        return stuClass;
    }

    public void setStuClass(SubClass stuClass) {
        this.stuClass = stuClass;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getStuTel() {
        return stuTel;
    }

    public void setStuTel(String stuTel) {
        this.stuTel = stuTel;
    }

    public String getStuAddress() {
        return stuAddress;
    }

    public void setStuAddress(String stuAddress) {
        this.stuAddress = stuAddress;
    }

    @Override
    public String toString() {
        return "Student{" +
                "stuId=" + stuId +
                ", stuName='" + stuName + '\'' +
                ", stuGender=" + stuGender +
                ", stuMajor='" + stuMajor + '\'' +
                ", stuClass=" + stuClass +
                ", grade=" + grade +
                ", stuTel='" + stuTel + '\'' +
                ", stuAddress='" + stuAddress + '\'' +
                '}';
    }
}
