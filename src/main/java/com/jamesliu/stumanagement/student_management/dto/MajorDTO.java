package com.jamesliu.stumanagement.student_management.dto;

/**
 * 专业 DTO
 * 仅暴露必要的标识与基本字段，避免懒加载关联导致的序列化问题
 */
public class MajorDTO {
    private Integer majorId;
    private String majorName;
    private Integer grade;
    private Integer academyId;
    private Integer counselorId;

    public Integer getMajorId() { return majorId; }
    public void setMajorId(Integer majorId) { this.majorId = majorId; }

    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public Integer getAcademyId() { return academyId; }
    public void setAcademyId(Integer academyId) { this.academyId = academyId; }

    public Integer getCounselorId() { return counselorId; }
    public void setCounselorId(Integer counselorId) { this.counselorId = counselorId; }
}


