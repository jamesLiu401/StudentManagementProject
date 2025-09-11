package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Service.StudentService.IStudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 学生管理控制器
 * 提供学生信息的增删改查功能，支持批量导入和智能数据关联
 * 
 * 主要功能：
 * 1. 学生信息管理 - 增删改查操作
 * 2. 分页查询 - 支持排序和筛选
 * 3. 批量导入 - CSV文件批量导入学生
 * 4. 智能关联 - 自动创建班级、专业等关联数据
 * 
 * 权限控制：
 * - 增删改操作：仅ADMIN
 * - 查询操作：ADMIN和TEACHER
 * - 批量导入：仅ADMIN
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * 添加学生信息
     * 支持智能创建关联的班级、专业数据
     * 
     * 智能特性：
     * - 如果班级不存在，自动创建Major→TotalClass→SubClass
     * - 根据专业和年级自动生成班级名称
     * - 设置默认学院和部门信息
     * 
     * @param student 学生信息对象
     * @return 保存成功的学生信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Student> addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        return ResponseMessage.success(savedStudent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Student> updateStudent(
            @PathVariable Integer id, 
            @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseMessage.success(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseMessage.success("学生删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Student> getStudentById(@PathVariable Integer id) {
        Student student = studentService.getStudentById(id);
        return ResponseMessage.success(student);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseMessage.success(students);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Student>> getStudentsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stuId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> students = studentService.getStudentsByPage(pageable);
        return ResponseMessage.success(students);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Student>> searchStudentsByName(
            @RequestParam String name) {
        List<Student> students = studentService.searchStudentsByName(name);
        return ResponseMessage.success(students);
    }

    /**
     * 批量导入学生信息
     * 通过CSV文件批量导入学生数据
     * 
     * CSV文件格式要求：
     * 姓名,性别,专业,年级,电话,地址,班级ID
     * 张三,true,计算机科学,2023,13800138000,北京市海淀区,1
     * 
     * 注意事项：
     * - 仅支持CSV格式文件
     * - 文件不能为空
     * - 支持智能创建关联数据
     * 
     * @param file CSV文件
     * @return 导入结果和学生列表
     */
    @PostMapping("/batch/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<List<Student>> batchImportFromCsv(
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseMessage.error("请选择要上传的文件");
        }
        
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            return ResponseMessage.error("请上传CSV格式的文件");
        }
        
        return studentService.batchImportFromCsv(file);
    }
}
