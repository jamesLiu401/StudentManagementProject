package com.jamesliu.stumanagement.student_management.Service.StudentService;

import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student addStudent(Student student) {
        return studentRepository.save(student);
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
}
