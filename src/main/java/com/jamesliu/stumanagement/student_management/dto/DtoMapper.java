package com.jamesliu.stumanagement.student_management.dto;

import com.jamesliu.stumanagement.student_management.Entity.Student.Major;
import com.jamesliu.stumanagement.student_management.Entity.Student.TotalClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.SubClass;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
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

    /**
     * DTO -> Entity converters
     */
    public static Academy fromDto(AcademyDTO dto) {
        if (dto == null) return null;
        Academy entity = new Academy();
        entity.setAcademyId(dto.getAcademyId());
        entity.setAcademyName(dto.getAcademyName());
        entity.setAcademyCode(dto.getAcademyCode());
        entity.setDescription(dto.getDescription());
        entity.setDeanName(dto.getDeanName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    public static Teacher fromDto(TeacherDTO dto) {
        if (dto == null) return null;
        Teacher entity = new Teacher();
        entity.setTeacherId(dto.getTeacherId());
        entity.setTeacherNo(dto.getTeacherNo());
        entity.setTeacherName(dto.getTeacherName());
        entity.setDepartment(dto.getDepartment());
        entity.setTitle(dto.getTitle());
        return entity;
    }
}


