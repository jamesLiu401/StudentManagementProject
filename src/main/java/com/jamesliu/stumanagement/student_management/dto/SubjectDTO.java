package com.jamesliu.stumanagement.student_management.dto;

/**
 * 课程数据传输对象
 * 用于课程相关的数据传输和操作，提供完整的DTO支持
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>请求DTO - 封装客户端请求数据</li>
 *   <li>响应DTO - 封装服务端响应数据</li>
 *   <li>批量操作DTO - 支持批量操作</li>
 *   <li>统计DTO - 封装统计结果</li>
 *   <li>导入导出DTO - 支持数据导入导出</li>
 * </ul>
 * 
 * <p>DTO类型：</p>
 * <ul>
 *   <li>SubjectCreateRequest - 课程创建请求</li>
 *   <li>SubjectUpdateRequest - 课程更新请求</li>
 *   <li>SubjectQueryRequest - 课程查询请求</li>
 *   <li>SubjectBatchRequest - 课程批量操作请求</li>
 *   <li>SubjectStatisticsResponse - 课程统计响应</li>
 *   <li>SubjectResponse - 课程响应</li>
 *   <li>SubjectImportRequest/Response - 课程导入请求/响应</li>
 * </ul>
 * 
 * <p>设计原则：</p>
 * <ul>
 *   <li>数据封装 - 封装复杂的数据结构</li>
 *   <li>类型安全 - 提供类型安全的参数传递</li>
 *   <li>验证支持 - 支持数据验证注解</li>
 *   <li>序列化支持 - 支持JSON序列化</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-26
 */
public class SubjectDTO {
    private Long subjectId;
    private String subjectName;
    private Integer academyId;
    private String academyName;
    private Double credit;

    // 默认构造函数
    public SubjectDTO() {}

    // 带参构造函数
    public SubjectDTO(Long subjectId, String subjectName, Integer academyId, String academyName, Double credit) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.academyId = academyId;
        this.academyName = academyName;
        this.credit = credit;
    }

    // Getters and Setters
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

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

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "SubjectDTO{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", academyId=" + academyId +
                ", academyName='" + academyName + '\'' +
                ", credit=" + credit +
                '}';
    }
}