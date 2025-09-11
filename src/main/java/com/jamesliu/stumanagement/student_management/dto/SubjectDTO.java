package com.jamesliu.stumanagement.student_management.dto;

import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;

import java.util.List;

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
    
    /**
     * 课程创建请求
     */
    public static class SubjectCreateRequest {
        private String subjectName;
        private String subjectAcademy;
        private Double credit;
        
        // Getters and Setters
        public String getSubjectName() {
            return subjectName;
        }
        
        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }
        
        public String getSubjectAcademy() {
            return subjectAcademy;
        }
        
        public void setSubjectAcademy(String subjectAcademy) {
            this.subjectAcademy = subjectAcademy;
        }
        
        public Double getCredit() {
            return credit;
        }
        
        public void setCredit(Double credit) {
            this.credit = credit;
        }
    }
    
    /**
     * 课程更新请求
     */
    public static class SubjectUpdateRequest {
        private Long subjectId;
        private String subjectName;
        private String subjectAcademy;
        private Double credit;
        
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
        
        public String getSubjectAcademy() {
            return subjectAcademy;
        }
        
        public void setSubjectAcademy(String subjectAcademy) {
            this.subjectAcademy = subjectAcademy;
        }
        
        public Double getCredit() {
            return credit;
        }
        
        public void setCredit(Double credit) {
            this.credit = credit;
        }
    }
    
    /**
     * 课程查询请求
     */
    public static class SubjectQueryRequest {
        private String academy;
        private String subjectName;
        private Double minCredit;
        private Double maxCredit;
        private Integer page;
        private Integer size;
        private String sortBy;
        private String sortDir;
        
        // Getters and Setters
        public String getAcademy() {
            return academy;
        }
        
        public void setAcademy(String academy) {
            this.academy = academy;
        }
        
        public String getSubjectName() {
            return subjectName;
        }
        
        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }
        
        public Double getMinCredit() {
            return minCredit;
        }
        
        public void setMinCredit(Double minCredit) {
            this.minCredit = minCredit;
        }
        
        public Double getMaxCredit() {
            return maxCredit;
        }
        
        public void setMaxCredit(Double maxCredit) {
            this.maxCredit = maxCredit;
        }
        
        public Integer getPage() {
            return page;
        }
        
        public void setPage(Integer page) {
            this.page = page;
        }
        
        public Integer getSize() {
            return size;
        }
        
        public void setSize(Integer size) {
            this.size = size;
        }
        
        public String getSortBy() {
            return sortBy;
        }
        
        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }
        
        public String getSortDir() {
            return sortDir;
        }
        
        public void setSortDir(String sortDir) {
            this.sortDir = sortDir;
        }
    }
    
    /**
     * 课程批量操作请求
     */
    public static class SubjectBatchRequest {
        private List<Long> subjectIds;
        private String operation; // DELETE, UPDATE_ACADEMY, UPDATE_CREDIT
        private String newAcademy;
        private Double newCredit;
        
        // Getters and Setters
        public List<Long> getSubjectIds() {
            return subjectIds;
        }
        
        public void setSubjectIds(List<Long> subjectIds) {
            this.subjectIds = subjectIds;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public void setOperation(String operation) {
            this.operation = operation;
        }
        
        public String getNewAcademy() {
            return newAcademy;
        }
        
        public void setNewAcademy(String newAcademy) {
            this.newAcademy = newAcademy;
        }
        
        public Double getNewCredit() {
            return newCredit;
        }
        
        public void setNewCredit(Double newCredit) {
            this.newCredit = newCredit;
        }
    }
    
    /**
     * 课程统计响应
     */
    public static class SubjectStatisticsResponse {
        private long totalSubjects;
        private long subjectsByAcademy;
        private long subjectsByCredit;
        private Double totalCredits;
        private List<String> academies;
        private List<Double> credits;
        
        // Getters and Setters
        public long getTotalSubjects() {
            return totalSubjects;
        }
        
        public void setTotalSubjects(long totalSubjects) {
            this.totalSubjects = totalSubjects;
        }
        
        public long getSubjectsByAcademy() {
            return subjectsByAcademy;
        }
        
        public void setSubjectsByAcademy(long subjectsByAcademy) {
            this.subjectsByAcademy = subjectsByAcademy;
        }
        
        public long getSubjectsByCredit() {
            return subjectsByCredit;
        }
        
        public void setSubjectsByCredit(long subjectsByCredit) {
            this.subjectsByCredit = subjectsByCredit;
        }
        
        public Double getTotalCredits() {
            return totalCredits;
        }
        
        public void setTotalCredits(Double totalCredits) {
            this.totalCredits = totalCredits;
        }
        
        public List<String> getAcademies() {
            return academies;
        }
        
        public void setAcademies(List<String> academies) {
            this.academies = academies;
        }
        
        public List<Double> getCredits() {
            return credits;
        }
        
        public void setCredits(List<Double> credits) {
            this.credits = credits;
        }
    }
    
    /**
     * 课程响应
     */
    public static class SubjectResponse {
        private Long subjectId;
        private String subjectName;
        private String subjectAcademy;
        private Double credit;
        
        // 构造函数
        public SubjectResponse() {}
        
        public SubjectResponse(Subject subject) {
            this.subjectId = subject.getSubjectId();
            this.subjectName = subject.getSubjectName();
            this.subjectAcademy = subject.getSubjectAcademy();
            this.credit = subject.getCredit();
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
        
        public String getSubjectAcademy() {
            return subjectAcademy;
        }
        
        public void setSubjectAcademy(String subjectAcademy) {
            this.subjectAcademy = subjectAcademy;
        }
        
        public Double getCredit() {
            return credit;
        }
        
        public void setCredit(Double credit) {
            this.credit = credit;
        }
    }
    
    /**
     * 课程导入请求
     */
    public static class SubjectImportRequest {
        private List<SubjectCreateRequest> subjects;
        private boolean overwriteExisting;
        
        // Getters and Setters
        public List<SubjectCreateRequest> getSubjects() {
            return subjects;
        }
        
        public void setSubjects(List<SubjectCreateRequest> subjects) {
            this.subjects = subjects;
        }
        
        public boolean isOverwriteExisting() {
            return overwriteExisting;
        }
        
        public void setOverwriteExisting(boolean overwriteExisting) {
            this.overwriteExisting = overwriteExisting;
        }
    }
    
    /**
     * 课程导入响应
     */
    public static class SubjectImportResponse {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private List<String> errors;
        
        // Getters and Setters
        public int getTotalCount() {
            return totalCount;
        }
        
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }
        
        public int getFailureCount() {
            return failureCount;
        }
        
        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
}
