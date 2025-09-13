package com.jamesliu.stumanagement.student_management.dto;

/**
 * 学院 DTO
 */
public class AcademyDTO {
    private Integer academyId;
    private String academyName;
    private String academyCode;
    private String description;
    private String deanName;
    private String contactPhone;
    private String address;

    public Integer getAcademyId() { return academyId; }
    public void setAcademyId(Integer academyId) { this.academyId = academyId; }
    public String getAcademyName() { return academyName; }
    public void setAcademyName(String academyName) { this.academyName = academyName; }
    public String getAcademyCode() { return academyCode; }
    public void setAcademyCode(String academyCode) { this.academyCode = academyCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDeanName() { return deanName; }
    public void setDeanName(String deanName) { this.deanName = deanName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}


