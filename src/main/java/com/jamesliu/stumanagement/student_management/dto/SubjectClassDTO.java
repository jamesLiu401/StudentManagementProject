package com.jamesliu.stumanagement.student_management.dto;

/**
 * 课程班级DTO
 */
public class SubjectClassDTO {
    private Integer subjectClassId;
    private Integer subjectId;
    private String subjectName;
    private String academy;
    private Double credit;
    private Integer subClassId;
    private String subClassName;
    private String semester;
    private String schoolYear;

    // Getters and Setters
    public Integer getSubjectClassId() { return subjectClassId; }
    public void setSubjectClassId(Integer subjectClassId) { this.subjectClassId = subjectClassId; }
    
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getAcademy() { return academy; }
    public void setAcademy(String academy) { this.academy = academy; }
    
    public Double getCredit() { return credit; }
    public void setCredit(Double credit) { this.credit = credit; }
    
    public Integer getSubClassId() { return subClassId; }
    public void setSubClassId(Integer subClassId) { this.subClassId = subClassId; }
    
    public String getSubClassName() { return subClassName; }
    public void setSubClassName(String subClassName) { this.subClassName = subClassName; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }
}
