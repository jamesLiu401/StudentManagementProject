package com.jamesliu.stumanagement.student_management.dto;

/**
 * 学生 DTO
 */
public class StudentDTO {
    private Integer stuId;

    private String stuName;

    private boolean stuGender;

    private String stuMajor;

    private Integer stuGrade;

    private String stuTel;

    private String stuAddress;

    private Integer stuClassId;

    private String stuClassName;

    public Integer getStuId() { return stuId; }
    public void setStuId(Integer stuId) { this.stuId = stuId; }
    public String getStuName() { return stuName; }
    public void setStuName(String stuName) { this.stuName = stuName; }
    public boolean isStuGender() { return stuGender; }
    public void setStuGender(boolean stuGender) { this.stuGender = stuGender; }
    public String getStuMajor() { return stuMajor; }
    public void setStuMajor(String stuMajor) { this.stuMajor = stuMajor; }
    public Integer getStuGrade() { return stuGrade; }
    public void setStuGrade(Integer stuGrade) { this.stuGrade = stuGrade; }
    public String getStuTel() { return stuTel; }
    public void setStuTel(String stuTel) { this.stuTel = stuTel; }
    public String getStuAddress() { return stuAddress; }
    public void setStuAddress(String stuAddress) { this.stuAddress = stuAddress; }
    public Integer getStuClassId() { return stuClassId; }
    public void setStuClassId(Integer subClassId) { this.stuClassId = subClassId; }
    public String getStuClassName() { return stuClassName; }
    public void setStuClassName(String subClassName) { this.stuClassName = subClassName; }
}


