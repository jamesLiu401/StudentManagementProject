package com.jamesliu.stumanagement.student_management.dto;

import java.util.List;
import java.util.Map;

/**
 * 级联操作数据传输对象
 * 用于传输级联删除和批量操作的数据，支持复杂的级联管理操作
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>级联删除请求 - 封装级联删除操作的数据</li>
 *   <li>批量操作请求 - 封装批量操作的数据</li>
 *   <li>数据迁移请求 - 封装数据迁移操作的数据</li>
 *   <li>统计响应 - 封装统计结果数据</li>
 *   <li>验证响应 - 封装数据验证结果</li>
 * </ul>
 * 
 * <p>DTO类型：</p>
 * <ul>
 *   <li>CascadeDeleteRequest - 级联删除请求</li>
 *   <li>BatchOperationRequest - 批量操作请求</li>
 *   <li>DataMigrationRequest - 数据迁移请求</li>
 *   <li>AcademyStatisticsResponse - 学院统计响应</li>
 *   <li>ValidationResponse - 验证响应</li>
 * </ul>
 * 
 * <p>操作类型：</p>
 * <ul>
 *   <li>ACADEMY - 学院级联操作</li>
 *   <li>MAJOR - 专业级联操作</li>
 *   <li>TOTAL_CLASS - 大班级联操作</li>
 *   <li>SUB_CLASS - 小班级联操作</li>
 *   <li>STUDENT - 学生级联操作</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-08-27
 */
public class CascadeOperationDTO {
    
    /**
     * 级联删除请求
     */
    public static class CascadeDeleteRequest {
        private Integer sourceId;           // 源ID
        private Integer targetId;           // 目标ID（用于数据迁移）
        private boolean forceDelete;        // 是否强制删除
        private String operationType;       // 操作类型：ACADEMY, MAJOR, TOTAL_CLASS, SUB_CLASS
        
        // Getters and Setters
        public Integer getSourceId() {
            return sourceId;
        }
        
        public void setSourceId(Integer sourceId) {
            this.sourceId = sourceId;
        }
        
        public Integer getTargetId() {
            return targetId;
        }
        
        public void setTargetId(Integer targetId) {
            this.targetId = targetId;
        }
        
        public boolean isForceDelete() {
            return forceDelete;
        }
        
        public void setForceDelete(boolean forceDelete) {
            this.forceDelete = forceDelete;
        }
        
        public String getOperationType() {
            return operationType;
        }
        
        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }
    }
    
    /**
     * 批量创建请求
     */
    public static class BatchCreateRequest {
        private Integer academyId;                    // 学院ID
        private Integer grade;                        // 年级
        private List<String> majorNames;              // 专业名称列表
        private int classCountPerMajor;               // 每个专业的大班数量
        private int subClassCountPerTotalClass;       // 每个大班的小班数量
        
        // Getters and Setters
        public Integer getAcademyId() {
            return academyId;
        }
        
        public void setAcademyId(Integer academyId) {
            this.academyId = academyId;
        }
        
        public Integer getGrade() {
            return grade;
        }
        
        public void setGrade(Integer grade) {
            this.grade = grade;
        }
        
        public List<String> getMajorNames() {
            return majorNames;
        }
        
        public void setMajorNames(List<String> majorNames) {
            this.majorNames = majorNames;
        }
        
        public int getClassCountPerMajor() {
            return classCountPerMajor;
        }
        
        public void setClassCountPerMajor(int classCountPerMajor) {
            this.classCountPerMajor = classCountPerMajor;
        }
        
        public int getSubClassCountPerTotalClass() {
            return subClassCountPerTotalClass;
        }
        
        public void setSubClassCountPerTotalClass(int subClassCountPerTotalClass) {
            this.subClassCountPerTotalClass = subClassCountPerTotalClass;
        }
    }
    
    /**
     * 学生迁移请求
     */
    public static class StudentMigrationRequest {
        private List<Integer> studentIds;     // 学生ID列表
        private Integer targetSubClassId;     // 目标小班ID
        
        // Getters and Setters
        public List<Integer> getStudentIds() {
            return studentIds;
        }
        
        public void setStudentIds(List<Integer> studentIds) {
            this.studentIds = studentIds;
        }
        
        public Integer getTargetSubClassId() {
            return targetSubClassId;
        }
        
        public void setTargetSubClassId(Integer targetSubClassId) {
            this.targetSubClassId = targetSubClassId;
        }
    }
    
    /**
     * 层级结构DTO
     */
    public static class HierarchyStructureDTO {
        private Integer academyId;
        private String academyName;
        private List<MajorInfo> majors;
        
        // Getters and Setters
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
        
        public List<MajorInfo> getMajors() {
            return majors;
        }
        
        public void setMajors(List<MajorInfo> majors) {
            this.majors = majors;
        }
        
        public static class MajorInfo {
            private Integer majorId;
            private String majorName;
            private Integer grade;
            private List<TotalClassInfo> totalClasses;
            
            // Getters and Setters
            public Integer getMajorId() {
                return majorId;
            }
            
            public void setMajorId(Integer majorId) {
                this.majorId = majorId;
            }
            
            public String getMajorName() {
                return majorName;
            }
            
            public void setMajorName(String majorName) {
                this.majorName = majorName;
            }
            
            public Integer getGrade() {
                return grade;
            }
            
            public void setGrade(Integer grade) {
                this.grade = grade;
            }
            
            public List<TotalClassInfo> getTotalClasses() {
                return totalClasses;
            }
            
            public void setTotalClasses(List<TotalClassInfo> totalClasses) {
                this.totalClasses = totalClasses;
            }
            
            public static class TotalClassInfo {
                private Integer totalClassId;
                private String totalClassName;
                private List<SubClassInfo> subClasses;
                
                // Getters and Setters
                public Integer getTotalClassId() {
                    return totalClassId;
                }
                
                public void setTotalClassId(Integer totalClassId) {
                    this.totalClassId = totalClassId;
                }
                
                public String getTotalClassName() {
                    return totalClassName;
                }
                
                public void setTotalClassName(String totalClassName) {
                    this.totalClassName = totalClassName;
                }
                
                public List<SubClassInfo> getSubClasses() {
                    return subClasses;
                }
                
                public void setSubClasses(List<SubClassInfo> subClasses) {
                    this.subClasses = subClasses;
                }
                
                public static class SubClassInfo {
                    private Integer subClassId;
                    private String subClassName;
                    private int studentCount;
                    
                    // Getters and Setters
                    public Integer getSubClassId() {
                        return subClassId;
                    }
                    
                    public void setSubClassId(Integer subClassId) {
                        this.subClassId = subClassId;
                    }
                    
                    public String getSubClassName() {
                        return subClassName;
                    }
                    
                    public void setSubClassName(String subClassName) {
                        this.subClassName = subClassName;
                    }
                    
                    public int getStudentCount() {
                        return studentCount;
                    }
                    
                    public void setStudentCount(int studentCount) {
                        this.studentCount = studentCount;
                    }
                }
            }
        }
    }
    
    /**
     * 删除预览DTO
     */
    public static class DeletePreviewDTO {
        private String operationType;
        private Integer sourceId;
        private String sourceName;
        private int affectedRecords;
        private List<String> affectedItems;
        private boolean canDelete;
        private String warningMessage;
        
        // Getters and Setters
        public String getOperationType() {
            return operationType;
        }
        
        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }
        
        public Integer getSourceId() {
            return sourceId;
        }
        
        public void setSourceId(Integer sourceId) {
            this.sourceId = sourceId;
        }
        
        public String getSourceName() {
            return sourceName;
        }
        
        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
        
        public int getAffectedRecords() {
            return affectedRecords;
        }
        
        public void setAffectedRecords(int affectedRecords) {
            this.affectedRecords = affectedRecords;
        }
        
        public List<String> getAffectedItems() {
            return affectedItems;
        }
        
        public void setAffectedItems(List<String> affectedItems) {
            this.affectedItems = affectedItems;
        }
        
        public boolean isCanDelete() {
            return canDelete;
        }
        
        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }
        
        public String getWarningMessage() {
            return warningMessage;
        }
        
        public void setWarningMessage(String warningMessage) {
            this.warningMessage = warningMessage;
        }
    }
    
    /**
     * 批量更新请求DTO
     */
    public static class BatchUpdateRequest {
        private String updateType;           // 更新类型：ACADEMY, MAJOR, TOTAL_CLASS, SUB_CLASS
        private List<UpdateItem> updates;    // 更新项目列表
        
        // Getters and Setters
        public String getUpdateType() {
            return updateType;
        }
        
        public void setUpdateType(String updateType) {
            this.updateType = updateType;
        }
        
        public List<UpdateItem> getUpdates() {
            return updates;
        }
        
        public void setUpdates(List<UpdateItem> updates) {
            this.updates = updates;
        }
        
        public static class UpdateItem {
            private Integer id;              // 实体ID
            private String name;             // 新名称
            private Integer parentId;        // 新的父级ID
            private String description;      // 描述
            private Map<String, Object> additionalFields; // 其他字段
            
            // Getters and Setters
            public Integer getId() {
                return id;
            }
            
            public void setId(Integer id) {
                this.id = id;
            }
            
            public String getName() {
                return name;
            }
            
            public void setName(String name) {
                this.name = name;
            }
            
            public Integer getParentId() {
                return parentId;
            }
            
            public void setParentId(Integer parentId) {
                this.parentId = parentId;
            }
            
            public String getDescription() {
                return description;
            }
            
            public void setDescription(String description) {
                this.description = description;
            }
            
            public Map<String, Object> getAdditionalFields() {
                return additionalFields;
            }
            
            public void setAdditionalFields(Map<String, Object> additionalFields) {
                this.additionalFields = additionalFields;
            }
        }
    }
    
    /**
     * 学院统计响应
     */
    public static class AcademyStatisticsResponse {
        private long majorCount;
        private long totalClassCount;
        private long subClassCount;
        private long studentCount;
        private long subjectCount;
        private Double totalCredits;
        private List<Double> credits;
        
        // Getters and Setters
        public long getMajorCount() {
            return majorCount;
        }
        
        public void setMajorCount(long majorCount) {
            this.majorCount = majorCount;
        }
        
        public long getTotalClassCount() {
            return totalClassCount;
        }
        
        public void setTotalClassCount(long totalClassCount) {
            this.totalClassCount = totalClassCount;
        }
        
        public long getSubClassCount() {
            return subClassCount;
        }
        
        public void setSubClassCount(long subClassCount) {
            this.subClassCount = subClassCount;
        }
        
        public long getStudentCount() {
            return studentCount;
        }
        
        public void setStudentCount(long studentCount) {
            this.studentCount = studentCount;
        }
        
        public long getSubjectCount() {
            return subjectCount;
        }
        
        public void setSubjectCount(long subjectCount) {
            this.subjectCount = subjectCount;
        }
        
        public Double getTotalCredits() {
            return totalCredits;
        }
        
        public void setTotalCredits(Double totalCredits) {
            this.totalCredits = totalCredits;
        }
        
        public List<Double> getCredits() {
            return credits;
        }
        
        public void setCredits(List<Double> credits) {
            this.credits = credits;
        }
    }
}
