package com.jamesliu.stumanagement.student_management.dto;

/**
 * 子班级 DTO
 */
public class SubClassDTO {
    private Integer subClassId;
    private String subClassName;
    private Integer totalClassId;

    public Integer getSubClassId() { return subClassId; }
    public void setSubClassId(Integer subClassId) { this.subClassId = subClassId; }

    public String getSubClassName() { return subClassName; }
    public void setSubClassName(String subClassName) { this.subClassName = subClassName; }

    public Integer getTotalClassId() { return totalClassId; }
    public void setTotalClassId(Integer totalClassId) { this.totalClassId = totalClassId; }
}


