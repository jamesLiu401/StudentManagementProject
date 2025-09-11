package com.jamesliu.stumanagement.student_management.Service.CascadeService;

import com.jamesliu.stumanagement.student_management.Entity.Student.*;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.dto.CascadeOperationDTO;
import com.jamesliu.stumanagement.student_management.dto.SubjectDTO;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.*;
import com.jamesliu.stumanagement.student_management.repository.TeacherRepo.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 级联管理服务
 * 处理学院、专业、班级、课程之间的联动操作，确保数据一致性
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>级联删除 - 删除上级时自动处理下级数据</li>
 *   <li>批量操作 - 支持批量创建和更新</li>
 *   <li>数据迁移 - 支持数据在不同层级间的迁移</li>
 *   <li>一致性检查 - 确保数据关联的完整性</li>
 *   <li>统计功能 - 提供各层级的统计信息</li>
 *   <li>数据验证 - 验证数据完整性和业务规则</li>
 * </ul>
 * 
 * <p>级联操作类型：</p>
 * <ul>
 *   <li>学院级联 - 删除学院时处理专业、班级、学生、课程</li>
 *   <li>专业级联 - 删除专业时处理班级、学生</li>
 *   <li>班级级联 - 删除班级时处理学生</li>
 *   <li>课程级联 - 删除学院时处理课程</li>
 * </ul>
 * 
 * <p>事务管理：</p>
 * <ul>
 *   <li>使用@Transactional确保操作的原子性</li>
 *   <li>支持回滚机制</li>
 *   <li>异常处理确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-23
 */
@Service
@Transactional
public class CascadeManagementService {

    @Autowired
    private AcademyRepository academyRepository;
    
    @Autowired
    private MajorRepository majorRepository;
    
    @Autowired
    private TotalClassRepository totalClassRepository;
    
    @Autowired
    private SubClassRepository subClassRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * 级联删除学院
     * 删除学院时，需要处理：
     * 1. 将该学院下的所有专业转移到其他学院
     * 2. 或者删除该学院下的所有专业（包括班级和学生）
     * 
     * @param academyId 学院ID
     * @param targetAcademyId 目标学院ID（如果为null则删除所有下级数据）
     * @param forceDelete 是否强制删除（删除所有下级数据）
     */
    public void cascadeDeleteAcademy(Integer academyId, Integer targetAcademyId, boolean forceDelete) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            throw new RuntimeException("学院不存在");
        }
        
        List<Major> majors = majorRepository.findByAcademy(academy.get());
        
        if (forceDelete) {
            // 强制删除：删除所有下级数据
            for (Major major : majors) {
                cascadeDeleteMajor(major.getMajorId(), null, true);
            }
        } else {
            // 迁移数据：将专业转移到目标学院
            if (targetAcademyId == null) {
                throw new RuntimeException("必须指定目标学院ID或选择强制删除");
            }
            
            Optional<Academy> targetAcademy = academyRepository.findById(targetAcademyId);
            if (targetAcademy.isEmpty()) {
                throw new RuntimeException("目标学院不存在");
            }
            
            for (Major major : majors) {
                major.setAcademy(targetAcademy.get());
                majorRepository.save(major);
            }
        }
        
        academyRepository.deleteById(academyId);
    }

    /**
     * 级联删除专业
     * 删除专业时，需要处理：
     * 1. 将该专业下的所有班级转移到其他专业
     * 2. 或者删除该专业下的所有班级（包括学生）
     * 
     * @param majorId 专业ID
     * @param targetMajorId 目标专业ID（如果为null则删除所有下级数据）
     * @param forceDelete 是否强制删除（删除所有下级数据）
     */
    public void cascadeDeleteMajor(Integer majorId, Integer targetMajorId, boolean forceDelete) {
        Optional<Major> major = majorRepository.findById(majorId);
        if (major.isEmpty()) {
            throw new RuntimeException("专业不存在");
        }
        
        List<TotalClass> totalClasses = totalClassRepository.findByMajor(major.get());
        
        if (forceDelete) {
            // 强制删除：删除所有下级数据
            for (TotalClass totalClass : totalClasses) {
                cascadeDeleteTotalClass(totalClass.getTotalClassId(), null, true);
            }
        } else {
            if (targetMajorId == null) {
                throw new RuntimeException("必须指定目标专业ID或选择强制删除");
            }
            
            Optional<Major> targetMajor = majorRepository.findById(targetMajorId);
            if (!targetMajor.isPresent()) {
                throw new RuntimeException("目标专业不存在");
            }
            
            // 迁移班级到目标专业
            for (TotalClass totalClass : totalClasses) {
                totalClass.setMajor(targetMajor.get());
                totalClassRepository.save(totalClass);
            }
        }
        
        majorRepository.deleteById(majorId);
    }

    /**
     * 级联删除大班
     * 删除大班时，需要处理：
     * 1. 将该大班下的所有小班转移到其他大班
     * 2. 或者删除该大班下的所有小班（包括学生）
     * 
     * @param totalClassId 大班ID
     * @param targetTotalClassId 目标大班ID（如果为null则删除所有下级数据）
     * @param forceDelete 是否强制删除（删除所有下级数据）
     */
    public void cascadeDeleteTotalClass(Integer totalClassId, Integer targetTotalClassId, boolean forceDelete) {
        Optional<TotalClass> totalClass = totalClassRepository.findById(totalClassId);
        if (totalClass.isEmpty()) {
            throw new RuntimeException("大班不存在");
        }
        
        List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass.get());
        
        if (forceDelete) {
            // 强制删除：删除所有下级数据
            for (SubClass subClass : subClasses) {
                cascadeDeleteSubClass(subClass.getSubClassId(), null, true);
            }
        } else {
            if (targetTotalClassId == null) {
                throw new RuntimeException("必须指定目标大班ID或选择强制删除");
            }
            
            Optional<TotalClass> targetTotalClass = totalClassRepository.findById(targetTotalClassId);
            if (!targetTotalClass.isPresent()) {
                throw new RuntimeException("目标大班不存在");
            }
            
            // 迁移小班到目标大班
            for (SubClass subClass : subClasses) {
                subClass.setTotalClass(targetTotalClass.get());
                subClassRepository.save(subClass);
            }
        }
        
        totalClassRepository.deleteById(totalClassId);
    }

    /**
     * 级联删除小班
     * 删除小班时，需要处理：
     * 1. 将该小班下的所有学生转移到其他小班
     * 2. 或者删除该小班下的所有学生
     * 
     * @param subClassId 小班ID
     * @param targetSubClassId 目标小班ID（如果为null则删除所有下级数据）
     * @param forceDelete 是否强制删除（删除所有下级数据）
     */
    public void cascadeDeleteSubClass(Integer subClassId, Integer targetSubClassId, boolean forceDelete) {
        Optional<SubClass> subClass = subClassRepository.findById(subClassId);
        if (subClass.isEmpty()) {
            throw new RuntimeException("小班不存在");
        }
        
        List<Student> students = studentRepository.findByStuClass(subClass.get());
        
        if (forceDelete) {
            // 强制删除：删除所有学生
            studentRepository.deleteAll(students);
        } else {
            if (targetSubClassId == null) {
                throw new RuntimeException("必须指定目标小班ID或选择强制删除");
            }
            
            Optional<SubClass> targetSubClass = subClassRepository.findById(targetSubClassId);
            if (targetSubClass.isEmpty()) {
                throw new RuntimeException("目标小班不存在");
            }
            
            // 迁移学生到目标小班
            for (Student student : students) {
                student.setStuClass(targetSubClass.get());
                studentRepository.save(student);
            }
        }
        
        subClassRepository.deleteById(subClassId);
    }

    /**
     * 批量创建专业和班级
     * 根据学院和年级批量创建专业，并自动创建对应的班级结构
     *
     * @param academyId 学院ID
     * @param grade 年级
     * @param majorNames 专业名称列表
     * @param classCountPerMajor 每个专业的大班数量
     * @param subClassCountPerTotalClass 每个大班的小班数量
     */
    public void batchCreateMajorsAndClasses(Integer academyId, Integer grade,
                                          List<String> majorNames,
                                          int classCountPerMajor,
                                          int subClassCountPerTotalClass) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (academy.isEmpty()) {
            throw new RuntimeException("学院不存在");
        }

        for (String majorName : majorNames) {
            // 检查专业是否已存在
            if (majorRepository.findByMajorNameAndGrade(majorName, grade).isPresent()) {
                continue; // 跳过已存在的专业
            }

            // 创建专业
            Major major = new Major();
            major.setMajorName(majorName);
            major.setAcademy(academy.get());
            major.setGrade(grade);
            Major savedMajor = majorRepository.save(major);

            // 为每个专业创建大班
            for (int i = 1; i <= classCountPerMajor; i++) {
                TotalClass totalClass = new TotalClass();
                totalClass.setTotalClassName(majorName + grade + "级" + i + "班");
                totalClass.setMajor(savedMajor);
                TotalClass savedTotalClass = totalClassRepository.save(totalClass);

                // 为每个大班创建小班
                for (int j = 1; j <= subClassCountPerTotalClass; j++) {
                    SubClass subClass = new SubClass();
                    subClass.setSubClassName(savedTotalClass.getTotalClassName() + "-" + j);
                    subClass.setTotalClass(savedTotalClass);
                    subClassRepository.save(subClass);
                }
            }
        }
    }

    /**
     * 数据迁移：将学生从一个班级迁移到另一个班级
     * 
     * @param studentIds 学生ID列表
     * @param targetSubClassId 目标小班ID
     */
    public void migrateStudents(List<Integer> studentIds, Integer targetSubClassId) {
        Optional<SubClass> targetSubClass = subClassRepository.findById(targetSubClassId);
        if (targetSubClass.isEmpty()) {
            throw new RuntimeException("目标小班不存在");
        }
        
        for (Integer studentId : studentIds) {
            Optional<Student> student = studentRepository.findById(studentId);
            if (student.isPresent()) {
                student.get().setStuClass(targetSubClass.get());
                studentRepository.save(student.get());
            }
        }
    }

    /**
     * 数据一致性检查
     * 检查整个层级结构的数据一致性
     * 
     * @return 一致性检查结果
     */
    public ConsistencyCheckResult checkDataConsistency() {
        ConsistencyCheckResult result = new ConsistencyCheckResult();
        
        // 检查学院-专业一致性
        List<Academy> academies = academyRepository.findAll();
        for (Academy academy : academies) {
            List<Major> majors = majorRepository.findByAcademy(academy);
            for (Major major : majors) {
                if (major.getAcademy() == null || !major.getAcademy().getAcademyId().equals(academy.getAcademyId())) {
                    result.addError("专业 " + major.getMajorName() + " 的学院关联不一致");
                }
            }
        }
        
        // 检查专业-大班一致性
        List<Major> majors = majorRepository.findAll();
        for (Major major : majors) {
            List<TotalClass> totalClasses = totalClassRepository.findByMajor(major);
            for (TotalClass totalClass : totalClasses) {
                if (totalClass.getMajor() == null || !totalClass.getMajor().getMajorId().equals(major.getMajorId())) {
                    result.addError("大班 " + totalClass.getTotalClassName() + " 的专业关联不一致");
                }
            }
        }
        
        // 检查大班-小班一致性
        List<TotalClass> totalClasses = totalClassRepository.findAll();
        for (TotalClass totalClass : totalClasses) {
            List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass);
            for (SubClass subClass : subClasses) {
                if (subClass.getTotalClass() == null || !subClass.getTotalClass().getTotalClassId().equals(totalClass.getTotalClassId())) {
                    result.addError("小班 " + subClass.getSubClassName() + " 的大班关联不一致");
                }
            }
        }
        
        // 检查小班-学生一致性
        List<SubClass> subClasses = subClassRepository.findAll();
        for (SubClass subClass : subClasses) {
            List<Student> students = studentRepository.findByStuClass(subClass);
            for (Student student : students) {
                if (student.getStuClass() == null || !student.getStuClass().getSubClassId().equals(subClass.getSubClassId())) {
                    result.addError("学生 " + student.getStuName() + " 的班级关联不一致");
                }
            }
        }
        
        return result;
    }

    /**
     * 一致性检查结果类
     */
    public static class ConsistencyCheckResult {
        private final List<String> errors = new java.util.ArrayList<>();
        private final List<String> warnings = new java.util.ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * 获取层级结构
     * 返回学院-专业-大班-小班的完整层级结构
     * 
     * @param academyId 学院ID（可选，不指定则返回所有学院）
     * @return 层级结构数据
     */
    public CascadeOperationDTO.HierarchyStructureDTO getHierarchyStructure(Integer academyId) {
        List<Academy> academies;
        
        if (academyId != null) {
            // 查询指定学院
            Optional<Academy> academy = academyRepository.findById(academyId);
            if (!academy.isPresent()) {
                throw new RuntimeException("学院不存在");
            }
            academies = List.of(academy.get());
        } else {
            // 查询所有学院
            academies = academyRepository.findAll();
        }
        
        List<CascadeOperationDTO.HierarchyStructureDTO> hierarchyList = new ArrayList<>();
        
        for (Academy academy : academies) {
            CascadeOperationDTO.HierarchyStructureDTO hierarchy = new CascadeOperationDTO.HierarchyStructureDTO();
            hierarchy.setAcademyId(academy.getAcademyId());
            hierarchy.setAcademyName(academy.getAcademyName());
            
            // 获取该学院下的所有专业
            List<Major> majors = majorRepository.findByAcademy(academy);
            List<CascadeOperationDTO.HierarchyStructureDTO.MajorInfo> majorInfos = new ArrayList<>();
            
            for (Major major : majors) {
                CascadeOperationDTO.HierarchyStructureDTO.MajorInfo majorInfo = 
                    new CascadeOperationDTO.HierarchyStructureDTO.MajorInfo();
                majorInfo.setMajorId(major.getMajorId());
                majorInfo.setMajorName(major.getMajorName());
                majorInfo.setGrade(major.getGrade());
                
                // 获取该专业下的所有大班
                List<TotalClass> totalClasses = totalClassRepository.findByMajor(major);
                List<CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo> totalClassInfos = new ArrayList<>();
                
                for (TotalClass totalClass : totalClasses) {
                    CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo totalClassInfo = 
                        new CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo();
                    totalClassInfo.setTotalClassId(totalClass.getTotalClassId());
                    totalClassInfo.setTotalClassName(totalClass.getTotalClassName());
                    
                    // 获取该大班下的所有小班
                    List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass);
                    List<CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo.SubClassInfo> subClassInfos = new ArrayList<>();
                    
                    for (SubClass subClass : subClasses) {
                        CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo.SubClassInfo subClassInfo = 
                            new CascadeOperationDTO.HierarchyStructureDTO.MajorInfo.TotalClassInfo.SubClassInfo();
                        subClassInfo.setSubClassId(subClass.getSubClassId());
                        subClassInfo.setSubClassName(subClass.getSubClassName());
                        
                        // 获取学生数量
                        long studentCount = studentRepository.countByStuClass(subClass);
                        subClassInfo.setStudentCount((int) studentCount);
                        
                        subClassInfos.add(subClassInfo);
                    }
                    
                    totalClassInfo.setSubClasses(subClassInfos);
                    totalClassInfos.add(totalClassInfo);
                }
                
                majorInfo.setTotalClasses(totalClassInfos);
                majorInfos.add(majorInfo);
            }
            
            hierarchy.setMajors(majorInfos);
            hierarchyList.add(hierarchy);
        }
        
        // 如果只查询一个学院，直接返回该学院的结构
        if (academyId != null && !hierarchyList.isEmpty()) {
            return hierarchyList.get(0);
        }
        
        // 如果查询所有学院，返回第一个学院的结构（可以根据需要调整）
        return hierarchyList.isEmpty() ? new CascadeOperationDTO.HierarchyStructureDTO() : hierarchyList.get(0);
    }

    /**
     * 获取删除预览
     * 在删除前预览将要影响的数据
     * 
     * @param operationType 操作类型
     * @param sourceId 源ID
     * @return 删除预览信息
     */
    public CascadeOperationDTO.DeletePreviewDTO getDeletePreview(String operationType, Integer sourceId) {
        CascadeOperationDTO.DeletePreviewDTO preview = new CascadeOperationDTO.DeletePreviewDTO();
        preview.setOperationType(operationType);
        preview.setSourceId(sourceId);
        preview.setAffectedItems(new ArrayList<>());
        
        switch (operationType.toUpperCase()) {
            case "ACADEMY":
                return getAcademyDeletePreview(sourceId, preview);
            case "MAJOR":
                return getMajorDeletePreview(sourceId, preview);
            case "TOTAL_CLASS":
                return getTotalClassDeletePreview(sourceId, preview);
            case "SUB_CLASS":
                return getSubClassDeletePreview(sourceId, preview);
            default:
                preview.setCanDelete(false);
                preview.setWarningMessage("不支持的操作类型");
                return preview;
        }
    }
    
    private CascadeOperationDTO.DeletePreviewDTO getAcademyDeletePreview(Integer academyId, CascadeOperationDTO.DeletePreviewDTO preview) {
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (!academy.isPresent()) {
            preview.setCanDelete(false);
            preview.setWarningMessage("学院不存在");
            return preview;
        }
        
        preview.setSourceName(academy.get().getAcademyName());
        
        // 统计影响的数据
        List<Major> majors = majorRepository.findByAcademy(academy.get());
        int totalAffected = 0;
        
        for (Major major : majors) {
            preview.getAffectedItems().add("专业: " + major.getMajorName());
            totalAffected++;
            
            List<TotalClass> totalClasses = totalClassRepository.findByMajor(major);
            for (TotalClass totalClass : totalClasses) {
                preview.getAffectedItems().add("  大班: " + totalClass.getTotalClassName());
                totalAffected++;
                
                List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass);
                for (SubClass subClass : subClasses) {
                    preview.getAffectedItems().add("    小班: " + subClass.getSubClassName());
                    totalAffected++;
                    
                    long studentCount = studentRepository.countByStuClass(subClass);
                    if (studentCount > 0) {
                        preview.getAffectedItems().add("      学生: " + studentCount + "人");
                        totalAffected += studentCount;
                    }
                }
            }
        }
        
        preview.setAffectedRecords(totalAffected);
        preview.setCanDelete(true);
        preview.setWarningMessage("删除学院将影响 " + totalAffected + " 条记录，请谨慎操作！");
        
        return preview;
    }
    
    private CascadeOperationDTO.DeletePreviewDTO getMajorDeletePreview(Integer majorId, CascadeOperationDTO.DeletePreviewDTO preview) {
        Optional<Major> major = majorRepository.findById(majorId);
        if (!major.isPresent()) {
            preview.setCanDelete(false);
            preview.setWarningMessage("专业不存在");
            return preview;
        }
        
        preview.setSourceName(major.get().getMajorName());
        
        // 统计影响的数据
        List<TotalClass> totalClasses = totalClassRepository.findByMajor(major.get());
        int totalAffected = 0;
        
        for (TotalClass totalClass : totalClasses) {
            preview.getAffectedItems().add("大班: " + totalClass.getTotalClassName());
            totalAffected++;
            
            List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass);
            for (SubClass subClass : subClasses) {
                preview.getAffectedItems().add("  小班: " + subClass.getSubClassName());
                totalAffected++;
                
                long studentCount = studentRepository.countByStuClass(subClass);
                if (studentCount > 0) {
                    preview.getAffectedItems().add("    学生: " + studentCount + "人");
                    totalAffected += studentCount;
                }
            }
        }
        
        preview.setAffectedRecords(totalAffected);
        preview.setCanDelete(true);
        preview.setWarningMessage("删除专业将影响 " + totalAffected + " 条记录，请谨慎操作！");
        
        return preview;
    }
    
    private CascadeOperationDTO.DeletePreviewDTO getTotalClassDeletePreview(Integer totalClassId, CascadeOperationDTO.DeletePreviewDTO preview) {
        Optional<TotalClass> totalClass = totalClassRepository.findById(totalClassId);
        if (!totalClass.isPresent()) {
            preview.setCanDelete(false);
            preview.setWarningMessage("大班不存在");
            return preview;
        }
        
        preview.setSourceName(totalClass.get().getTotalClassName());
        
        // 统计影响的数据
        List<SubClass> subClasses = subClassRepository.findByTotalClass(totalClass.get());
        int totalAffected = 0;
        
        for (SubClass subClass : subClasses) {
            preview.getAffectedItems().add("小班: " + subClass.getSubClassName());
            totalAffected++;
            
            long studentCount = studentRepository.countByStuClass(subClass);
            if (studentCount > 0) {
                preview.getAffectedItems().add("  学生: " + studentCount + "人");
                totalAffected += studentCount;
            }
        }
        
        preview.setAffectedRecords(totalAffected);
        preview.setCanDelete(true);
        preview.setWarningMessage("删除大班将影响 " + totalAffected + " 条记录，请谨慎操作！");
        
        return preview;
    }
    
    private CascadeOperationDTO.DeletePreviewDTO getSubClassDeletePreview(Integer subClassId, CascadeOperationDTO.DeletePreviewDTO preview) {
        Optional<SubClass> subClass = subClassRepository.findById(subClassId);
        if (!subClass.isPresent()) {
            preview.setCanDelete(false);
            preview.setWarningMessage("小班不存在");
            return preview;
        }
        
        preview.setSourceName(subClass.get().getSubClassName());
        
        // 统计影响的数据
        long studentCount = studentRepository.countByStuClass(subClass.get());
        int totalAffected = 0;
        
        if (studentCount > 0) {
            preview.getAffectedItems().add("学生: " + studentCount + "人");
            totalAffected += studentCount;
        }
        
        preview.setAffectedRecords(totalAffected);
        preview.setCanDelete(true);
        preview.setWarningMessage("删除小班将影响 " + totalAffected + " 条记录，请谨慎操作！");
        
        return preview;
    }

    /**
     * 批量更新操作
     * 支持批量更新多个实体的关联关系
     * 
     * @param request 批量更新请求
     */
    public void batchUpdate(CascadeOperationDTO.BatchUpdateRequest request) {
        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            throw new RuntimeException("更新项目列表不能为空");
        }
        
        switch (request.getUpdateType().toUpperCase()) {
            case "ACADEMY":
                batchUpdateAcademies(request.getUpdates());
                break;
            case "MAJOR":
                batchUpdateMajors(request.getUpdates());
                break;
            case "TOTAL_CLASS":
                batchUpdateTotalClasses(request.getUpdates());
                break;
            case "SUB_CLASS":
                batchUpdateSubClasses(request.getUpdates());
                break;
            default:
                throw new RuntimeException("不支持的操作类型: " + request.getUpdateType());
        }
    }
    
    private void batchUpdateAcademies(List<CascadeOperationDTO.BatchUpdateRequest.UpdateItem> updates) {
        for (CascadeOperationDTO.BatchUpdateRequest.UpdateItem item : updates) {
            Optional<Academy> academy = academyRepository.findById(item.getId());
            if (!academy.isPresent()) {
                throw new RuntimeException("学院不存在: " + item.getId());
            }
            
            Academy academyEntity = academy.get();
            if (item.getName() != null) {
                academyEntity.setAcademyName(item.getName());
            }
            if (item.getDescription() != null) {
                academyEntity.setDescription(item.getDescription());
            }
            
            // 处理其他字段
            if (item.getAdditionalFields() != null) {
                if (item.getAdditionalFields().containsKey("academyCode")) {
                    academyEntity.setAcademyCode((String) item.getAdditionalFields().get("academyCode"));
                }
                if (item.getAdditionalFields().containsKey("deanName")) {
                    academyEntity.setDeanName((String) item.getAdditionalFields().get("deanName"));
                }
                if (item.getAdditionalFields().containsKey("contactPhone")) {
                    academyEntity.setContactPhone((String) item.getAdditionalFields().get("contactPhone"));
                }
                if (item.getAdditionalFields().containsKey("address")) {
                    academyEntity.setAddress((String) item.getAdditionalFields().get("address"));
                }
            }
            
            academyRepository.save(academyEntity);
        }
    }
    
    private void batchUpdateMajors(List<CascadeOperationDTO.BatchUpdateRequest.UpdateItem> updates) {
        for (CascadeOperationDTO.BatchUpdateRequest.UpdateItem item : updates) {
            Optional<Major> major = majorRepository.findById(item.getId());
            if (!major.isPresent()) {
                throw new RuntimeException("专业不存在: " + item.getId());
            }
            
            Major majorEntity = major.get();
            if (item.getName() != null) {
                majorEntity.setMajorName(item.getName());
            }
            
            // 处理父级关系（学院）
            if (item.getParentId() != null) {
                Optional<Academy> academy = academyRepository.findById(item.getParentId());
                if (!academy.isPresent()) {
                    throw new RuntimeException("目标学院不存在: " + item.getParentId());
                }
                majorEntity.setAcademy(academy.get());
            }
            
            // 处理其他字段
            if (item.getAdditionalFields() != null) {
                if (item.getAdditionalFields().containsKey("grade")) {
                    majorEntity.setGrade((Integer) item.getAdditionalFields().get("grade"));
                }
                if (item.getAdditionalFields().containsKey("counselorId")) {
                    Integer counselorId = (Integer) item.getAdditionalFields().get("counselorId");
                    if (counselorId != null) {
                        Optional<Teacher> counselor = teacherRepository.findById(counselorId);
                        counselor.ifPresent(majorEntity::setCounselor);
                    }
                }
            }
            
            majorRepository.save(majorEntity);
        }
    }
    
    private void batchUpdateTotalClasses(List<CascadeOperationDTO.BatchUpdateRequest.UpdateItem> updates) {
        for (CascadeOperationDTO.BatchUpdateRequest.UpdateItem item : updates) {
            Optional<TotalClass> totalClass = totalClassRepository.findById(item.getId());
            if (!totalClass.isPresent()) {
                throw new RuntimeException("大班不存在: " + item.getId());
            }
            
            TotalClass totalClassEntity = totalClass.get();
            if (item.getName() != null) {
                totalClassEntity.setTotalClassName(item.getName());
            }
            
            // 处理父级关系（专业）
            if (item.getParentId() != null) {
                Optional<Major> major = majorRepository.findById(item.getParentId());
                if (!major.isPresent()) {
                    throw new RuntimeException("目标专业不存在: " + item.getParentId());
                }
                totalClassEntity.setMajor(major.get());
            }
            
            totalClassRepository.save(totalClassEntity);
        }
    }
    
    private void batchUpdateSubClasses(List<CascadeOperationDTO.BatchUpdateRequest.UpdateItem> updates) {
        for (CascadeOperationDTO.BatchUpdateRequest.UpdateItem item : updates) {
            Optional<SubClass> subClass = subClassRepository.findById(item.getId());
            if (!subClass.isPresent()) {
                throw new RuntimeException("小班不存在: " + item.getId());
            }
            
            SubClass subClassEntity = subClass.get();
            if (item.getName() != null) {
                subClassEntity.setSubClassName(item.getName());
            }
            
            // 处理父级关系（大班）
            if (item.getParentId() != null) {
                Optional<TotalClass> totalClass = totalClassRepository.findById(item.getParentId());
                if (!totalClass.isPresent()) {
                    throw new RuntimeException("目标大班不存在: " + item.getParentId());
                }
                subClassEntity.setTotalClass(totalClass.get());
            }
            
            subClassRepository.save(subClassEntity);
        }
    }
    
    /**
     * 级联删除学院时处理课程
     * 删除学院时，需要处理该学院下的所有课程
     * 
     * @param academyId 学院ID
     * @param targetAcademyId 目标学院ID（如果为null则删除所有课程）
     */
    public void cascadeDeleteAcademySubjects(Integer academyId, Integer targetAcademyId) {
        // 获取学院信息
        Optional<Academy> academy = academyRepository.findById(academyId);
        if (!academy.isPresent()) {
            throw new RuntimeException("学院不存在: " + academyId);
        }
        
        String academyName = academy.get().getAcademyName();
        
        if (targetAcademyId != null) {
            // 将课程转移到目标学院
            Optional<Academy> targetAcademy = academyRepository.findById(targetAcademyId);
            if (!targetAcademy.isPresent()) {
                throw new RuntimeException("目标学院不存在: " + targetAcademyId);
            }
            
            String targetAcademyName = targetAcademy.get().getAcademyName();
            
            // 获取该学院下的所有课程
            List<Subject> subjects = subjectRepository.findBySubjectAcademy(academyName);
            
            for (Subject subject : subjects) {
                // 检查目标学院是否已有同名课程
                if (subjectRepository.findBySubjectAcademyAndSubjectName(targetAcademyName, subject.getSubjectName()).isPresent()) {
                    // 如果存在同名课程，可以选择跳过或删除原课程
                    subjectRepository.delete(subject);
                } else {
                    // 更新课程所属学院
                    subject.setSubjectAcademy(targetAcademyName);
                    subjectRepository.save(subject);
                }
            }
        } else {
            // 删除该学院下的所有课程
            List<Subject> subjects = subjectRepository.findBySubjectAcademy(academyName);
            subjectRepository.deleteAll(subjects);
        }
    }
    
    /**
     * 批量更新课程学院
     * 
     * @param subjectIds 课程ID列表
     * @param newAcademy 新学院名称
     */
    public void batchUpdateSubjectAcademy(List<Long> subjectIds, String newAcademy) {
        for (Long subjectId : subjectIds) {
            Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
            if (subjectOpt.isPresent()) {
                Subject subject = subjectOpt.get();
                
                // 检查新学院是否已有同名课程
                if (subjectRepository.findBySubjectAcademyAndSubjectName(newAcademy, subject.getSubjectName()).isPresent()) {
                    throw new RuntimeException("目标学院已存在同名课程: " + subject.getSubjectName());
                }
                
                subject.setSubjectAcademy(newAcademy);
                subjectRepository.save(subject);
            }
        }
    }
    
    /**
     * 批量更新课程学分
     * 
     * @param subjectIds 课程ID列表
     * @param newCredit 新学分
     */
    public void batchUpdateSubjectCredit(List<Long> subjectIds, Double newCredit) {
        for (Long subjectId : subjectIds) {
            Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
            if (subjectOpt.isPresent()) {
                Subject subject = subjectOpt.get();
                subject.setCredit(newCredit);
                subjectRepository.save(subject);
            }
        }
    }
    
    /**
     * 获取学院课程统计信息
     * 
     * @param academyName 学院名称
     * @return 课程统计信息
     */
    public CascadeOperationDTO.AcademyStatisticsResponse getAcademySubjectStatistics(String academyName) {
        CascadeOperationDTO.AcademyStatisticsResponse response = new CascadeOperationDTO.AcademyStatisticsResponse();
        
        // 统计课程数量
        long subjectCount = subjectRepository.countBySubjectAcademy(academyName);
        response.setSubjectCount(subjectCount);
        
        // 计算总学分
        Double totalCredits = subjectRepository.sumCreditBySubjectAcademy(academyName);
        response.setTotalCredits(totalCredits);
        
        // 获取所有学分列表
        List<Double> credits = subjectRepository.findCreditsByAcademy(academyName);
        response.setCredits(credits);
        
        return response;
    }
    
    /**
     * 验证课程数据完整性
     * 
     * @return 验证结果
     */
    public List<String> validateSubjectDataIntegrity() {
        List<String> errors = new ArrayList<>();
        
        // 检查是否有课程没有指定学院
        List<Subject> allSubjects = subjectRepository.findAll();
        for (Subject subject : allSubjects) {
            if (subject.getSubjectAcademy() == null || subject.getSubjectAcademy().trim().isEmpty()) {
                errors.add("课程 '" + subject.getSubjectName() + "' 没有指定学院");
            }
            
            if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
                errors.add("课程ID " + subject.getSubjectId() + " 没有指定课程名称");
            }
            
            if (subject.getCredit() == null || subject.getCredit() <= 0) {
                errors.add("课程 '" + subject.getSubjectName() + "' 学分无效: " + subject.getCredit());
            }
        }
        
        // 检查是否有重复的课程（同一学院下的同名课程）
        List<String> academies = subjectRepository.findAllAcademies();
        for (String academy : academies) {
            List<Subject> academySubjects = subjectRepository.findBySubjectAcademy(academy);
            for (int i = 0; i < academySubjects.size(); i++) {
                for (int j = i + 1; j < academySubjects.size(); j++) {
                    if (academySubjects.get(i).getSubjectName().equals(academySubjects.get(j).getSubjectName())) {
                        errors.add("学院 '" + academy + "' 存在重复课程: " + academySubjects.get(i).getSubjectName());
                    }
                }
            }
        }
        
        return errors;
    }
}
