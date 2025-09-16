package com.jamesliu.stumanagement.student_management.dto;

import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Entity.Student.Subject;
import com.jamesliu.stumanagement.student_management.Entity.Teacher.Teacher;
import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.Student.Score;
import com.jamesliu.stumanagement.student_management.Entity.Student.Academy;

/**
 * 统一的实体到DTO映射工具
 */
public class DtoMapper {

    public static MajorDTO toDto(Major major) {
        if (major == null) return null;
        MajorDTO dto = new MajorDTO();
        dto.setMajorId(major.getMajorId());
        dto.setMajorName(major.getMajorName());
        dto.setGrade(major.getGrade());
        dto.setAcademyId(major.getAcademy() != null ? major.getAcademy().getAcademyId() : null);
        dto.setCounselorId(major.getCounselor() != null ? major.getCounselor().getTeacherId() : null);
        return dto;
    }

    public static TotalClassDTO toDto(TotalClass entity) {
        if (entity == null) return null;
        TotalClassDTO dto = new TotalClassDTO();
        dto.setTotalClassId(entity.getTotalClassId());
        dto.setTotalClassName(entity.getTotalClassName());
        dto.setMajorId(entity.getMajor() != null ? entity.getMajor().getMajorId() : null);
        return dto;
    }

    public static SubClassDTO toDto(SubClass entity) {
        if (entity == null) return null;
        SubClassDTO dto = new SubClassDTO();
        dto.setSubClassId(entity.getSubClassId());
        dto.setSubClassName(entity.getSubClassName());
        dto.setTotalClassId(entity.getTotalClass() != null ? entity.getTotalClass().getTotalClassId() : null);
        return dto;
    }

    public static StudentDTO toDto(Student entity) {
        if (entity == null) return null;
        StudentDTO dto = new StudentDTO();
        dto.setStuId(entity.getStuId());
        dto.setStuName(entity.getStuName());
        dto.setStuGender(entity.isStuGender());
        dto.setStuMajor(entity.getStuMajor());
        dto.setStuGrade(entity.getGrade());
        dto.setStuTel(entity.getStuTel());
        dto.setStuAddress(entity.getStuAddress());
        if (entity.getStuClassId() != null) {
            dto.setStuClassId(entity.getStuClassId().getSubClassId());
            dto.setStuClassName(entity.getStuClassId().getSubClassName());
        }
        return dto;
    }

    public static TeacherDTO toDto(Teacher entity) {
        if (entity == null) return null;
        TeacherDTO dto = new TeacherDTO();
        dto.setTeacherId(entity.getTeacherId());
        dto.setTeacherNo(entity.getTeacherNo());
        dto.setTeacherName(entity.getTeacherName());
        dto.setDepartment(entity.getDepartment());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    public static TeacherDetailDTO toDetailDto(Teacher entity) {
        if (entity == null) return null;
        TeacherDetailDTO dto = new TeacherDetailDTO();
        dto.setTeacherId(entity.getTeacherId());
        dto.setTeacherNo(entity.getTeacherNo());
        dto.setTeacherName(entity.getTeacherName());
        dto.setDepartment(entity.getDepartment());
        dto.setTitle(entity.getTitle());
        
        // 映射关联的专业数据
        if (entity.getMajors() != null) {
            dto.setMajors(entity.getMajors().stream()
                .map(DtoMapper::toDto)
                .toList());
        }
        
        // 映射关联的课程班级数据
        if (entity.getSubjectClasses() != null) {
            dto.setSubjectClasses(entity.getSubjectClasses().stream()
                .map(DtoMapper::toDto)
                .toList());
        }
        
        return dto;
    }

    public static SubjectClassDTO toDto(com.jamesliu.stumanagement.student_management.Entity.Teacher.SubjectClass entity) {
        if (entity == null) return null;
        SubjectClassDTO dto = new SubjectClassDTO();
        dto.setSubjectClassId(entity.getSubjectClassId());
        dto.setSemester(entity.getSemester());
        dto.setSchoolYear(entity.getSchoolYear());
        
        if (entity.getSubject() != null) {
            dto.setSubjectId(Math.toIntExact(entity.getSubject().getSubjectId()));
            dto.setSubjectName(entity.getSubject().getSubjectName());
            dto.setAcademyId(entity.getSubject().getAcademy() != null ? entity.getSubject().getAcademy().getAcademyId() : null);
            dto.setAcademyName(entity.getSubject().getAcademy() != null ? entity.getSubject().getAcademy().getAcademyName() : null);
            dto.setCredit(entity.getSubject().getCredit());
        }
        
        if (entity.getSubClass() != null) {
            dto.setSubClassId(entity.getSubClass().getSubClassId());
            dto.setSubClassName(entity.getSubClass().getSubClassName());
        }
        
        return dto;
    }

    public static PaymentDTO toDto(Payment entity) {
        if (entity == null) return null;
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(entity.getPaymentId());
        dto.setAmount(entity.getAmount());
        dto.setPaymentType(entity.getPaymentType());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setDescription(entity.getDescription());
        dto.setPaymentDate(entity.getPaymentDate());
        if (entity.getStudent() != null) {
            dto.setStuId(entity.getStudent().getStuId());
        }
        return dto;
    }

    public static ScoreDTO toDto(Score entity) {
        if (entity == null) return null;
        ScoreDTO dto = new ScoreDTO();
        dto.setScoreId(entity.getScoreId());
        dto.setScore(entity.getStuScore());
        if (entity.getStudent() != null) {
            dto.setStuId(entity.getStudent().getStuId());
            dto.setStuName(entity.getStudent().getStuName());
        }
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getSubjectId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }
        return dto;
    }

    public static AcademyDTO toDto(Academy entity) {
        if (entity == null) return null;
        AcademyDTO dto = new AcademyDTO();
        dto.setAcademyId(entity.getAcademyId());
        dto.setAcademyName(entity.getAcademyName());
        dto.setAcademyCode(entity.getAcademyCode());
        dto.setDescription(entity.getDescription());
        dto.setDeanName(entity.getDeanName());
        dto.setContactPhone(entity.getContactPhone());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    public static SubjectDTO toDto(Subject entity) {
        if (entity == null) return null;
        SubjectDTO dto = new SubjectDTO();
        dto.setSubjectId(entity.getSubjectId());
        dto.setSubjectName(entity.getSubjectName());
        dto.setAcademyId(entity.getAcademy() != null ? entity.getAcademy().getAcademyId() : null);
        dto.setAcademyName(entity.getAcademy() != null ? entity.getAcademy().getAcademyName() : null);
        dto.setCredit(entity.getCredit());
        return dto;
    }
}


