package com.jamesliu.stumanagement.student_management.Entity.Student;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * 学院实体类
 * 管理学校的各个学院信息，是学生管理系统的顶层组织结构
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>学院基本信息管理 - 学院名称、代码、院长信息</li>
 *   <li>层级关系管理 - 学院下包含多个专业</li>
 *   <li>组织架构维护 - 维护学校的组织架构</li>
 * </ul>
 * 
 * <p>关联关系：</p>
 * <ul>
 *   <li>一对多：一个学院包含多个专业(Major)</li>
 *   <li>级联操作：删除学院时处理下属专业</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：academy_table</li>
 *   <li>主键：academy_id (自增)</li>
 *   <li>唯一约束：academy_name, academy_code</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-02
 */
@Entity
@Table(name = "academy_table")
public class Academy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "academy_id")
    private Integer academyId;

    @Column(name = "academy_name", nullable = false, unique = true)
    private String academyName;

    @Column(name = "academy_code", unique = true)
    private String academyCode; // 学院代码，如：CS、EE等

    @Column(name = "description")
    private String description; // 学院描述

    @Column(name = "dean_name")
    private String deanName; // 院长姓名

    @Column(name = "contact_phone")
    private String contactPhone; // 联系电话

    @Column(name = "address")
    private String address; // 学院地址

    @OneToMany(mappedBy = "academy", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Major> majors;

    // Getters and Setters
    public Integer getAcademyId() {
        return academyId;
    }

    public void setAcademyId(Integer academyId) {
        this.academyId = academyId;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public String getAcademyCode() {
        return academyCode;
    }

    public void setAcademyCode(String academyCode) {
        this.academyCode = academyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeanName() {
        return deanName;
    }

    public void setDeanName(String deanName) {
        this.deanName = deanName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Major> getMajors() {
        return majors;
    }

    public void setMajors(List<Major> majors) {
        this.majors = majors;
    }

    @Override
    public String toString() {
        return "Academy{" +
                "academyId=" + academyId +
                ", academyName='" + academyName + '\'' +
                ", academyCode='" + academyCode + '\'' +
                ", description='" + description + '\'' +
                ", deanName='" + deanName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
