package com.jamesliu.stumanagement.student_management.dto;

import java.util.List;

/**
 * 教师详情DTO - 包含关联数据
 */
public class TeacherDetailDTO {
    private Integer teacherId;
    private String teacherNo;
    private String teacherName;
    private String department;
    private String title;
    private List<MajorDTO> majors;
    private List<SubjectClassDTO> subjectClasses;

    // Getters and Setters
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    
    public String getTeacherNo() { return teacherNo; }
    public void setTeacherNo(String teacherNo) { this.teacherNo = teacherNo; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<MajorDTO> getMajors() { return majors; }
    public void setMajors(List<MajorDTO> majors) { this.majors = majors; }
    
    public List<SubjectClassDTO> getSubjectClasses() { return subjectClasses; }
    public void setSubjectClasses(List<SubjectClassDTO> subjectClasses) { this.subjectClasses = subjectClasses; }
}
