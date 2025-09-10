package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    // 根据学生ID查询缴费记录
    List<Payment> findByStudentStuId(Integer studentId);
    
    // 根据缴费状态查询
    List<Payment> findByIsCompleted(boolean isCompleted);
    
    // 根据学生ID和缴费状态查询
    List<Payment> findByStudentStuIdAndIsCompleted(Integer studentId, boolean isCompleted);
}
