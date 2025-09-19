package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Service.CascadeService.CascadeManagementService;
import com.jamesliu.stumanagement.student_management.dto.CascadeOperationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 级联管理控制器
 * 提供表联动管理的API接口
 * <p>
 * 主要功能：
 * 1. 级联删除 - 支持数据迁移和强制删除
 * 2. 批量创建 - 支持批量创建专业和班级结构
 * 3. 数据迁移 - 支持学生和班级的迁移
 * 4. 一致性检查 - 检查数据关联的完整性
 * 5. 层级结构查询 - 获取完整的层级结构
 * <p>
 * 权限控制：
 * - 所有操作：仅ADMIN权限
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-06-25
 */
@RestController
@RequestMapping("/cascade")
@PreAuthorize("hasRole('ADMIN')")
public class CascadeController {

    @Autowired
    private CascadeManagementService cascadeManagementService;

    /**
     * 级联删除操作
     * 支持学院、专业、大班、小班的级联删除
     * 
     * @param request 级联删除请求
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseMessage<String> cascadeDelete(@RequestBody CascadeOperationDTO.CascadeDeleteRequest request) {
        try {
            return switch (request.getOperationType().toUpperCase()) {
                case "ACADEMY" -> {
                    cascadeManagementService.cascadeDeleteAcademy(
                            request.getSourceId(),
                            request.getTargetId(),
                            request.isForceDelete()
                    );
                    yield ResponseMessage.success("学院级联删除成功");
                }
                case "MAJOR" -> {
                    cascadeManagementService.cascadeDeleteMajor(
                            request.getSourceId(),
                            request.getTargetId(),
                            request.isForceDelete()
                    );
                    yield ResponseMessage.success("专业级联删除成功");
                }
                case "TOTAL_CLASS" -> {
                    cascadeManagementService.cascadeDeleteTotalClass(
                            request.getSourceId(),
                            request.getTargetId(),
                            request.isForceDelete()
                    );
                    yield ResponseMessage.success("大班级联删除成功");
                }
                case "SUB_CLASS" -> {
                    cascadeManagementService.cascadeDeleteSubClass(
                            request.getSourceId(),
                            request.getTargetId(),
                            request.isForceDelete()
                    );
                    yield ResponseMessage.success("小班级联删除成功");
                }
                default -> ResponseMessage.error("不支持的操作类型");
            };
        } catch (Exception e) {
            return ResponseMessage.error("级联删除失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建专业和班级
     * 根据学院和年级批量创建完整的班级结构
     * 
     * @param request 批量创建请求
     * @return 操作结果
     */
    @PostMapping("/batch-create")
    public ResponseMessage<String> batchCreateMajorsAndClasses(
            @RequestBody CascadeOperationDTO.BatchCreateRequest request) {
        try {
            cascadeManagementService.batchCreateMajorsAndClasses(
                request.getAcademyId(),
                request.getGrade(),
                request.getMajorNames(),
                request.getClassCountPerMajor(),
                request.getSubClassCountPerTotalClass()
            );
            return ResponseMessage.success("批量创建成功");
        } catch (Exception e) {
            return ResponseMessage.error("批量创建失败: " + e.getMessage());
        }
    }

    /**
     * 学生迁移
     * 将学生从一个班级迁移到另一个班级
     * 
     * @param request 学生迁移请求
     * @return 操作结果
     */
    @PostMapping("/migrate-students")
    public ResponseMessage<String> migrateStudents(
            @RequestBody CascadeOperationDTO.StudentMigrationRequest request) {
        try {
            cascadeManagementService.migrateStudents(
                request.getStudentIds(),
                request.getTargetSubClassId()
            );
            return ResponseMessage.success("学生迁移成功");
        } catch (Exception e) {
            return ResponseMessage.error("学生迁移失败: " + e.getMessage());
        }
    }

    /**
     * 数据一致性检查
     * 检查整个层级结构的数据一致性
     * 
     * @return 一致性检查结果
     */
    @GetMapping("/consistency-check")
    public ResponseMessage<CascadeManagementService.ConsistencyCheckResult> checkDataConsistency() {
        try {
            CascadeManagementService.ConsistencyCheckResult result = 
                cascadeManagementService.checkDataConsistency();
            return ResponseMessage.success(result);
        } catch (Exception e) {
            return ResponseMessage.error("一致性检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取完整的层级结构
     * 返回学院-专业-大班-小班的完整层级结构
     * 
     * @param academyId 学院ID（可选，不指定则返回所有学院）
     * @return 层级结构数据
     */
    @GetMapping("/hierarchy")
    public ResponseMessage<CascadeOperationDTO.HierarchyStructureDTO> getHierarchyStructure(
            @RequestParam(required = false) Integer academyId) {
        try {
            CascadeOperationDTO.HierarchyStructureDTO hierarchy = 
                cascadeManagementService.getHierarchyStructure(academyId);
            return ResponseMessage.success(hierarchy);
        } catch (Exception e) {
            return ResponseMessage.error("获取层级结构失败: " + e.getMessage());
        }
    }

    /**
     * 获取级联删除预览
     * 在删除前预览将要影响的数据
     * 
     * @param operationType 操作类型
     * @param sourceId 源ID
     * @return 影响的数据预览
     */
    @GetMapping("/delete-preview")
    public ResponseMessage<CascadeOperationDTO.DeletePreviewDTO> getDeletePreview(
            @RequestParam String operationType,
            @RequestParam Integer sourceId) {
        try {
            CascadeOperationDTO.DeletePreviewDTO preview = 
                cascadeManagementService.getDeletePreview(operationType, sourceId);
            return ResponseMessage.success(preview);
        } catch (Exception e) {
            return ResponseMessage.error("获取删除预览失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新操作
     * 支持批量更新多个实体的关联关系
     * 
     * @param request 批量更新请求
     * @return 操作结果
     */
    @PostMapping("/batch-update")
    public ResponseMessage<String> batchUpdate(@RequestBody CascadeOperationDTO.BatchUpdateRequest request) {
        try {
            cascadeManagementService.batchUpdate(request);
            return ResponseMessage.success("批量更新成功");
        } catch (Exception e) {
            return ResponseMessage.error("批量更新失败: " + e.getMessage());
        }
    }
}
