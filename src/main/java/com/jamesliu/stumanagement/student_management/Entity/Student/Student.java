package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 学生实体类
 * 管理学生的基本信息和学籍信息
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>学生基本信息管理 - 姓名、学号、联系方式等</li>
 *   <li>学籍信息管理 - 年级、专业、班级归属</li>
 *   <li>关联关系管理 - 与班级、专业、缴费记录等关联</li>
 * </ul>
 * 
 * <p>关联关系：</p>
 * <ul>
 *   <li>多对一：学生属于小班(SubClass)</li>
 *   <li>多对一：学生属于专业(Major)</li>
 *   <li>一对多：学生有多个缴费记录(Payment)</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：stu_table</li>
 *   <li>主键：stu_id (自增)</li>
 *   <li>外键：sub_class_id, major_id</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-04
 */
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
    @ManyToOne()
    @JoinColumn(name = "class_id")
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubClass stuClassId;

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

    public SubClass getStuClassId() {
        return stuClassId;
    }

    public void setStuClassId(SubClass stuClassId) {
        this.stuClassId = stuClassId;
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
                ", stuClass=" + stuClassId +
                ", grade=" + grade +
                ", stuTel='" + stuTel + '\'' +
                ", stuAddress='" + stuAddress + '\'' +
                '}';
    }
}
