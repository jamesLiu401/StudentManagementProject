package com.jamesliu.stumanagement.student_management.Service.StudentService;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.*;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.SubClassRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.TotalClassRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.MajorRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.AcademyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 学生服务实现类
 * 提供学生相关的业务逻辑操作，实现IStudentService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>学生CRUD操作 - 创建、查询、更新、删除学生</li>
 *   <li>智能数据关联 - 自动创建班级、专业、学院等关联数据</li>
 *   <li>批量导入功能 - 支持CSV文件批量导入学生</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>搜索功能 - 支持按姓名模糊搜索</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-09-10
 */
@Service
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;
    private final SubClassRepository subClassRepository;
    private final TotalClassRepository totalClassRepository;
    private final MajorRepository majorRepository;
    private final AcademyRepository academyRepository;

    public StudentService(StudentRepository studentRepository,
                         SubClassRepository subClassRepository,
                         TotalClassRepository totalClassRepository,
                         MajorRepository majorRepository,
                         AcademyRepository academyRepository) {
        this.studentRepository = studentRepository;
        this.subClassRepository = subClassRepository;
        this.totalClassRepository = totalClassRepository;
        this.majorRepository = majorRepository;
        this.academyRepository = academyRepository;
    }

    @Override
    @Transactional
    public Student addStudent(Student student) {
        // 智能创建关联数据
        SubClass subClass = ensureClassExists(student);
        student.setStuClass(subClass);
        
        return studentRepository.save(student);
    }
    
    /**
     * 确保班级存在，如果不存在则自动创建
     */
    private SubClass ensureClassExists(Student student) {
        // 如果学生已经指定了班级，直接返回
        if (student.getStuClass() != null && student.getStuClass().getSubClassId() != null) {
            return subClassRepository.findById(student.getStuClass().getSubClassId())
                    .orElseThrow(() -> new RuntimeException("指定的班级不存在"));
        }
        
        // 根据学生信息自动创建班级
        String className = generateClassName(student);
        String majorName = student.getStuMajor();
        Integer grade = student.getGrade();
        
        // 1. 确保专业存在
        Major major = ensureMajorExists(majorName, grade);
        
        // 2. 确保大班存在
        TotalClass totalClass = ensureTotalClassExists(major, grade);
        
        // 3. 确保小班存在
        return ensureSubClassExists(totalClass, className);
    }
    
    /**
     * 生成班级名称
     */
    private String generateClassName(Student student) {
        String major = student.getStuMajor();
        Integer grade = student.getGrade();
        
        // 生成班级名称，如：计科2301
        if (major != null && grade != null) {
            return major.substring(0, Math.min(2, major.length())) + grade.toString().substring(2) + "01";
        }
        return "默认班级";
    }
    
    /**
     * 确保专业存在
     */
    private Major ensureMajorExists(String majorName, Integer grade) {
        if (majorName == null) {
            majorName = "默认专业";
        }
        
        // 查找现有专业
        Optional<Major> existingMajor = majorRepository.findByMajorNameAndGrade(majorName, grade);
        if (existingMajor.isPresent()) {
            return existingMajor.get();
        }
        
        // 创建新专业
        Major major = new Major();
        major.setMajorName(majorName);
        major.setGrade(grade);
        major.setAcademy(ensureAcademyExists("默认学院"));
        return majorRepository.save(major);
    }
    
    /**
     * 确保大班存在
     */
    private TotalClass ensureTotalClassExists(Major major, Integer grade) {
        String totalClassName = major.getMajorName() + grade + "级";
        
        // 查找现有大班
        Optional<TotalClass> existingTotalClass = totalClassRepository.findByTotalClassNameAndMajor(totalClassName, major);
        if (existingTotalClass.isPresent()) {
            return existingTotalClass.get();
        }
        
        // 创建新大班
        TotalClass totalClass = new TotalClass();
        totalClass.setTotalClassName(totalClassName);
        totalClass.setMajor(major);
        return totalClassRepository.save(totalClass);
    }
    
    /**
     * 确保小班存在
     */
    private SubClass ensureSubClassExists(TotalClass totalClass, String className) {
        // 查找现有小班
        Optional<SubClass> existingSubClass = subClassRepository.findBySubClassNameAndTotalClass(className, totalClass);
        if (existingSubClass.isPresent()) {
            return existingSubClass.get();
        }
        
        // 创建新小班
        SubClass subClass = new SubClass();
        subClass.setSubClassName(className);
        subClass.setTotalClass(totalClass);
        return subClassRepository.save(subClass);
    }

    @Override
    public Student updateStudent(Integer id, Student student) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setStuName(student.getStuName());
                    existingStudent.setStuGender(student.isStuGender());
                    existingStudent.setStuMajor(student.getStuMajor());
                    existingStudent.setStuClass(student.getStuClass());
                    existingStudent.setGrade(student.getGrade());
                    existingStudent.setStuTel(student.getStuTel());
                    existingStudent.setStuAddress(student.getStuAddress());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + id));
    }

    @Override
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("学生不存在，ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + id));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Page<Student> getStudentsByPage(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByStuNameContaining(name);
    }

    @Override
    public ResponseMessage<List<Student>> batchImportFromCsv(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            // 跳过表头
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    Student student = new Student();
                    // 假设CSV格式：姓名,性别,专业,班级ID,年级,电话,地址
                    student.setStuName(data[0]);
                    student.setStuGender("男".equals(data[1]) || "true".equals(data[1]));
                    student.setStuMajor(data[2]);
                    // 班级ID处理需要额外逻辑，这里简化处理
                    student.setGrade(Integer.parseInt(data[4]));
                    student.setStuTel(data[5]);
                    student.setStuAddress(data[6]);
                    
                    students.add(student);
                }
            }
            
            studentRepository.saveAll(students);
            return ResponseMessage.success("批量导入成功，共导入 " + students.size() + " 条记录", students);
            
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("批量导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 确保学院存在
     */
    private Academy ensureAcademyExists(String academyName) {
        if (academyName == null) {
            academyName = "默认学院";
        }
        
        // 查找现有学院
        Optional<Academy> existingAcademy = academyRepository.findByAcademyName(academyName);
        if (existingAcademy.isPresent()) {
            return existingAcademy.get();
        }
        
        // 创建新学院
        Academy academy = new Academy();
        academy.setAcademyName(academyName);
        academy.setAcademyCode("DEFAULT");
        academy.setDescription("默认学院");
        return academyRepository.save(academy);
    }
}
