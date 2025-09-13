package com.jamesliu.stumanagement.student_management.dto;

/**
 * 成绩 DTO
 */
public class ScoreDTO {
    private Integer scoreId;
    private Integer stuId;
    private String stuName;
    private Long subjectId;
    private String subjectName;
    private Double score;

    public Integer getScoreId() { return scoreId; }
    public void setScoreId(Integer scoreId) { this.scoreId = scoreId; }
    public Integer getStuId() { return stuId; }
    public void setStuId(Integer stuId) { this.stuId = stuId; }
    public String getStuName() { return stuName; }
    public void setStuName(String stuName) { this.stuName = stuName; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}


