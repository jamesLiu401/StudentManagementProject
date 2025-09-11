package com.jamesliu.stumanagement.student_management.repository.StuRepo;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费记录数据访问接口
 * 提供缴费记录相关的数据库操作，包括基础CRUD和复杂查询
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作 - 继承JpaRepository提供标准操作</li>
 *   <li>条件查询 - 支持按学生、缴费状态、项目、金额、日期等条件查询</li>
 *   <li>分页查询 - 支持分页和排序</li>
 *   <li>统计查询 - 支持缴费数量统计和金额统计</li>
 *   <li>聚合查询 - 支持金额求和、最新记录查询等</li>
 *   <li>自定义查询 - 使用@Query注解实现复杂业务查询</li>
 * </ul>
 * 
 * <p>查询方法分类：</p>
 * <ul>
 *   <li>学生相关查询 - 按学生ID查询缴费记录</li>
 *   <li>状态相关查询 - 按缴费状态查询</li>
 *   <li>项目相关查询 - 按缴费项目查询</li>
 *   <li>金额相关查询 - 按金额范围查询</li>
 *   <li>日期相关查询 - 按缴费日期查询</li>
 *   <li>复合条件查询 - 多条件组合查询</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-13
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    // 根据学生ID查询缴费记录
    @NonNull
    List<Payment> findByStudentStuId(@NonNull Integer studentId);
    
    // 根据缴费状态查询
    @NonNull
    List<Payment> findByIsCompleted(boolean isCompleted);
    
    // 根据学生ID和缴费状态查询
    @NonNull
    List<Payment> findByStudentStuIdAndIsCompleted(@NonNull Integer studentId, boolean isCompleted);
    
    // 根据学生对象查询缴费记录
    @NonNull
    List<Payment> findByStudent(@NonNull Student student);
    
    // 根据学生对象和缴费状态查询
    @NonNull
    List<Payment> findByStudentAndIsCompleted(@NonNull Student student, boolean isCompleted);
    
    // 根据缴费项目查询
    @NonNull
    List<Payment> findByPaymentItem(@NonNull String paymentItem);
    
    // 根据缴费项目模糊查询
    @NonNull
    List<Payment> findByPaymentItemContaining(@NonNull String paymentItem);
    
    // 根据缴费项目和学生ID查询
    @NonNull
    List<Payment> findByPaymentItemAndStudentStuId(@NonNull String paymentItem, @NonNull Integer studentId);
    
    // 根据金额范围查询
    @NonNull
    List<Payment> findByAmountBetween(@NonNull Double minAmount, @NonNull Double maxAmount);
    
    // 根据缴费日期查询
    @NonNull
    List<Payment> findByPaymentDate(@NonNull LocalDate paymentDate);
    
    // 根据缴费日期范围查询
    @NonNull
    List<Payment> findByPaymentDateBetween(@NonNull LocalDate startDate, @NonNull LocalDate endDate);
    
    // 根据学生ID和缴费日期范围查询
    @NonNull
    List<Payment> findByStudentStuIdAndPaymentDateBetween(@NonNull Integer studentId, @NonNull LocalDate startDate, @NonNull LocalDate endDate);
    
    // 根据学生ID和缴费项目查询
    @NonNull
    List<Payment> findByStudentStuIdAndPaymentItem(@NonNull Integer studentId, @NonNull String paymentItem);
    
    // 根据学生ID、缴费状态和缴费项目查询
    @NonNull
    List<Payment> findByStudentStuIdAndIsCompletedAndPaymentItem(@NonNull Integer studentId, boolean isCompleted, @NonNull String paymentItem);
    
    // 分页查询所有缴费记录
    @NonNull
    Page<Payment> findAll(@NonNull Pageable pageable);
    
    // 根据缴费状态分页查询
    @NonNull
    Page<Payment> findByIsCompleted(boolean isCompleted, @NonNull Pageable pageable);
    
    // 根据学生ID分页查询
    @NonNull
    Page<Payment> findByStudentStuId(@NonNull Integer studentId, @NonNull Pageable pageable);
    
    // 根据学生ID和缴费状态分页查询
    @NonNull
    Page<Payment> findByStudentStuIdAndIsCompleted(@NonNull Integer studentId, boolean isCompleted, @NonNull Pageable pageable);
    
    // 统计学生未完成缴费数量
    long countByStudentStuIdAndIsCompleted(@NonNull Integer studentId, boolean isCompleted);
    
    // 统计学生总缴费数量
    long countByStudentStuId(@NonNull Integer studentId);
    
    // 统计指定缴费项目的数量
    long countByPaymentItem(@NonNull String paymentItem);
    
    // 统计指定缴费状态的数量
    long countByIsCompleted(boolean isCompleted);
    
    // 计算学生总缴费金额
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.stuId = :studentId AND p.isCompleted = true")
    @NonNull
    Double sumAmountByStudentStuIdAndIsCompleted(@Param("studentId") @NonNull Integer studentId);
    
    // 计算学生指定缴费项目的总金额
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.student.stuId = :studentId AND p.paymentItem = :paymentItem AND p.isCompleted = true")
    @NonNull
    Double sumAmountByStudentStuIdAndPaymentItemAndIsCompleted(@Param("studentId") @NonNull Integer studentId, @Param("paymentItem") @NonNull String paymentItem);
    
    // 查询学生最新的缴费记录
    @Query("SELECT p FROM Payment p WHERE p.student.stuId = :studentId ORDER BY p.paymentDate DESC")
    @NonNull
    List<Payment> findLatestPaymentsByStudentStuId(@Param("studentId") @NonNull Integer studentId, @NonNull Pageable pageable);
    
    // 查询指定日期范围内的缴费记录
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate DESC")
    @NonNull
    List<Payment> findPaymentsByDateRange(@Param("startDate") @NonNull LocalDate startDate, @Param("endDate") @NonNull LocalDate endDate);
    
    // 查询未完成缴费的学生ID列表
    @Query("SELECT DISTINCT p.student.stuId FROM Payment p WHERE p.isCompleted = false")
    @NonNull
    List<Integer> findStudentIdsWithIncompletePayments();
    
    // 查询指定缴费项目的未完成记录
    @Query("SELECT p FROM Payment p WHERE p.paymentItem = :paymentItem AND p.isCompleted = false")
    @NonNull
    List<Payment> findIncompletePaymentsByItem(@Param("paymentItem") @NonNull String paymentItem);
}
