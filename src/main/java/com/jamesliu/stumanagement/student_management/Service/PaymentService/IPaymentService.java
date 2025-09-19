package com.jamesliu.stumanagement.student_management.Service.PaymentService;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费服务接口
 * 定义缴费相关的业务逻辑操作，提供统一的业务接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 缴费记录的增删改查</li>
 *   <li>条件查询操作 - 支持多条件组合查询</li>
 *   <li>分页查询操作 - 支持分页和排序</li>
 *   <li>统计操作 - 缴费金额统计</li>
 *   <li>业务逻辑操作 - 缴费记录创建、更新、搜索等</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-14
 */
public interface IPaymentService {
    
    // 基础 CRUD 操作
    Payment savePayment(Payment payment);
    Optional<Payment> findById(Integer id);
    List<Payment> findAll();
    Page<Payment> findAll(Pageable pageable);
    void deleteById(Integer id);
    void deletePayment(Payment payment);
    
    // 学生缴费查询
    List<Payment> findByStudent(Student student);
    Page<Payment> findByStudent(Student student, Pageable pageable);
    List<Payment> findByStudentId(Integer studentId);
    
    // 缴费类型查询
    List<Payment> findByPaymentType(String paymentType);
    Page<Payment> findByPaymentType(String paymentType, Pageable pageable);
    
    // 缴费状态查询
    List<Payment> findByPaymentStatus(String paymentStatus);
    Page<Payment> findByPaymentStatus(String paymentStatus, Pageable pageable);
    
    // 日期范围查询
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Payment> findByStudentAndPaymentDateBetween(Student student, LocalDate startDate, LocalDate endDate);
    
    // 金额范围查询
    List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    List<Payment> findByStudentAndAmountBetween(Student student, BigDecimal minAmount, BigDecimal maxAmount);
    
    // 复合条件查询
    List<Payment> findByStudentAndPaymentType(Student student, String paymentType);
    List<Payment> findByStudentAndPaymentStatus(Student student, String paymentStatus);
    List<Payment> findByPaymentTypeAndPaymentStatus(String paymentType, String paymentStatus);
    
    // 统计操作
    long countByStudent(Student student);
    long countByPaymentType(String paymentType);
    long countByPaymentStatus(String paymentStatus);
    long countByStudentAndPaymentStatus(Student student, String paymentStatus);
    
    // 金额统计
    BigDecimal sumAmountByStudent(Student student);
    BigDecimal sumAmountByStudentId(Integer studentId);
    BigDecimal sumAmountByPaymentType(String paymentType);
    BigDecimal sumAmountByPaymentStatus(String paymentStatus);
    BigDecimal sumAmountByStudentAndPaymentType(Student student, String paymentType);
    
    // 业务逻辑操作
    Payment createPayment(Student student, String paymentType, BigDecimal amount, String paymentStatus, String description);
    Payment updatePayment(Integer id, String paymentType, BigDecimal amount, String paymentStatus, String description);
    Payment updatePaymentStatus(Integer id, String paymentStatus);
    
    // 搜索功能
    List<Payment> searchPayments(String keyword);
    
    // 验证功能
    boolean isValidAmount(BigDecimal amount);
    boolean isValidPaymentStatus(String paymentStatus);
    boolean isValidPaymentType(String paymentType);
}
