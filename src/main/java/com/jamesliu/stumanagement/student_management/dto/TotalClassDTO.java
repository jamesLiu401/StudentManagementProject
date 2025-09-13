package com.jamesliu.stumanagement.student_management.dto;

/**
 * 总班级 DTO
 */
public class TotalClassDTO {
    private Integer totalClassId;
    private String totalClassName;
    private Integer majorId;

    public Integer getTotalClassId() { return totalClassId; }
    public void setTotalClassId(Integer totalClassId) { this.totalClassId = totalClassId; }

    public String getTotalClassName() { return totalClassName; }
    public void setTotalClassName(String totalClassName) { this.totalClassName = totalClassName; }

    public Integer getMajorId() { return majorId; }
    public void setMajorId(Integer majorId) { this.majorId = majorId; }
}


