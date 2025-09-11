package com.jamesliu.stumanagement.student_management.Service.PaymentService;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.PaymentRepository;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费服务实现类
 * 提供缴费相关的业务逻辑操作，实现IPaymentService接口
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>缴费CRUD操作 - 创建、查询、更新、删除缴费记录</li>
 *   <li>条件查询服务 - 支持多条件组合查询</li>
 *   <li>分页查询服务 - 支持分页和排序</li>
 *   <li>统计服务 - 提供缴费金额统计</li>
 *   <li>业务验证 - 缴费金额、状态、类型验证</li>
 *   <li>事务管理 - 使用@Transactional确保数据一致性</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-15
 */
@Service
@Transactional
public class PaymentService implements IPaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    // 基础 CRUD 操作
    /**
     * 保存缴费记录
     * 在保存前会验证缴费金额、状态和类型的有效性
     * 
     * @param payment 要保存的缴费记录
     * @return 保存后的缴费记录
     * @throws IllegalArgumentException 当缴费金额、状态或类型无效时抛出
     * @since 2025-07-15
     */
    @Override
    public Payment savePayment(Payment payment) {
        if (!isValidAmount(payment.getAmount())) {
            throw new IllegalArgumentException("缴费金额必须大于0");
        }
        if (!isValidPaymentStatus(payment.getPaymentStatus())) {
            throw new IllegalArgumentException("缴费状态无效");
        }
        if (!isValidPaymentType(payment.getPaymentType())) {
            throw new IllegalArgumentException("缴费类型无效");
        }
        return paymentRepository.save(payment);
    }
    
    /**
     * 根据ID查询缴费记录
     * 
     * @param id 缴费记录ID
     * @return 缴费记录，如果不存在则返回空
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findById(Integer id) {
        return paymentRepository.findById(id);
    }
    
    /**
     * 查询所有缴费记录
     * 
     * @return 所有缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
    
    /**
     * 分页查询所有缴费记录
     * 
     * @param pageable 分页参数
     * @return 分页的缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    
    /**
     * 根据ID删除缴费记录
     * 
     * @param id 缴费记录ID
     * @since 2025-07-15
     */
    @Override
    public void deleteById(Integer id) {
        paymentRepository.deleteById(id);
    }
    
    /**
     * 删除缴费记录
     * 
     * @param payment 要删除的缴费记录
     * @since 2025-07-15
     */
    @Override
    public void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
    }
    
    // 学生缴费查询
    /**
     * 根据学生查询缴费记录
     * 
     * @param student 学生对象
     * @return 该学生的所有缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudent(Student student) {
        return paymentRepository.findByStudent(student);
    }
    
    /**
     * 根据学生分页查询缴费记录
     * 
     * @param student 学生对象
     * @param pageable 分页参数
     * @return 分页的缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findByStudent(Student student, Pageable pageable) {
        return paymentRepository.findByStudent(student, pageable);
    }
    
    /**
     * 根据学生ID查询缴费记录
     * 
     * @param studentId 学生ID
     * @return 该学生的所有缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudentId(Integer studentId) {
        return paymentRepository.findByStudentStuId(studentId);
    }
    
    // 缴费类型查询
    /**
     * 根据缴费类型查询缴费记录
     * 
     * @param paymentType 缴费类型
     * @return 指定类型的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByPaymentType(String paymentType) {
        return paymentRepository.findByPaymentType(paymentType);
    }
    
    /**
     * 根据缴费类型分页查询缴费记录
     * 
     * @param paymentType 缴费类型
     * @param pageable 分页参数
     * @return 分页的缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findByPaymentType(String paymentType, Pageable pageable) {
        return paymentRepository.findByPaymentType(paymentType, pageable);
    }
    
    // 缴费状态查询
    /**
     * 根据缴费状态查询缴费记录
     * 
     * @param paymentStatus 缴费状态
     * @return 指定状态的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByPaymentStatus(String paymentStatus) {
        return paymentRepository.findByPaymentStatus(paymentStatus);
    }
    
    /**
     * 根据缴费状态分页查询缴费记录
     * 
     * @param paymentStatus 缴费状态
     * @param pageable 分页参数
     * @return 分页的缴费记录
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findByPaymentStatus(String paymentStatus, Pageable pageable) {
        return paymentRepository.findByPaymentStatus(paymentStatus, pageable);
    }
    
    // 日期范围查询
    /**
     * 根据缴费日期范围查询缴费记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 指定日期范围内的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }
    
    /**
     * 根据学生和缴费日期范围查询缴费记录
     * 
     * @param student 学生对象
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 指定学生和日期范围内的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudentAndPaymentDateBetween(Student student, LocalDate startDate, LocalDate endDate) {
        return paymentRepository.findByStudentAndPaymentDateBetween(student, startDate, endDate);
    }
    
    // 金额范围查询
    /**
     * 根据缴费金额范围查询缴费记录
     * 
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 指定金额范围内的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findByAmountBetween(minAmount.doubleValue(), maxAmount.doubleValue());
    }
    
    /**
     * 根据学生和缴费金额范围查询缴费记录
     * 
     * @param student 学生对象
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 指定学生和金额范围内的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudentAndAmountBetween(Student student, BigDecimal minAmount, BigDecimal maxAmount) {
        return paymentRepository.findByStudentAndAmountBetween(student, minAmount.doubleValue(), maxAmount.doubleValue());
    }
    
    // 复合条件查询
    /**
     * 根据学生和缴费类型查询缴费记录
     * 
     * @param student 学生对象
     * @param paymentType 缴费类型
     * @return 指定学生和缴费类型的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudentAndPaymentType(Student student, String paymentType) {
        return paymentRepository.findByStudentAndPaymentType(student, paymentType);
    }
    
    /**
     * 根据学生和缴费状态查询缴费记录
     * 
     * @param student 学生对象
     * @param paymentStatus 缴费状态
     * @return 指定学生和缴费状态的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStudentAndPaymentStatus(Student student, String paymentStatus) {
        return paymentRepository.findByStudentAndPaymentStatus(student, paymentStatus);
    }
    
    /**
     * 根据缴费类型和缴费状态查询缴费记录
     * 
     * @param paymentType 缴费类型
     * @param paymentStatus 缴费状态
     * @return 指定缴费类型和状态的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByPaymentTypeAndPaymentStatus(String paymentType, String paymentStatus) {
        return paymentRepository.findByPaymentTypeAndPaymentStatus(paymentType, paymentStatus);
    }
    
    // 统计操作
    /**
     * 统计学生的缴费记录数量
     * 
     * @param student 学生对象
     * @return 该学生的缴费记录数量
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public long countByStudent(Student student) {
        return paymentRepository.countByStudent(student);
    }
    
    /**
     * 统计指定缴费类型的记录数量
     * 
     * @param paymentType 缴费类型
     * @return 指定缴费类型的记录数量
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public long countByPaymentType(String paymentType) {
        return paymentRepository.countByPaymentType(paymentType);
    }
    
    /**
     * 统计指定缴费状态的记录数量
     * 
     * @param paymentStatus 缴费状态
     * @return 指定缴费状态的记录数量
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public long countByPaymentStatus(String paymentStatus) {
        return paymentRepository.countByPaymentStatus(paymentStatus);
    }
    
    /**
     * 统计学生指定缴费状态的记录数量
     * 
     * @param student 学生对象
     * @param paymentStatus 缴费状态
     * @return 该学生指定缴费状态的记录数量
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public long countByStudentAndPaymentStatus(Student student, String paymentStatus) {
        return paymentRepository.countByStudentAndPaymentStatus(student, paymentStatus);
    }
    
    // 金额统计
    /**
     * 计算学生的总缴费金额
     * 
     * @param student 学生对象
     * @return 该学生的总缴费金额
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByStudent(Student student) {
        Double amount = paymentRepository.sumAmountByStudent(student);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    /**
     * 根据学生ID计算总缴费金额
     * 
     * @param studentId 学生ID
     * @return 该学生的总缴费金额
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByStudentId(Integer studentId) {
        Double amount = paymentRepository.sumAmountByStudentId(studentId);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    /**
     * 计算指定缴费类型的总金额
     * 
     * @param paymentType 缴费类型
     * @return 指定缴费类型的总金额
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByPaymentType(String paymentType) {
        Double amount = paymentRepository.sumAmountByPaymentType(paymentType);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    /**
     * 计算指定缴费状态的总金额
     * 
     * @param paymentStatus 缴费状态
     * @return 指定缴费状态的总金额
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByPaymentStatus(String paymentStatus) {
        Double amount = paymentRepository.sumAmountByPaymentStatus(paymentStatus);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    /**
     * 计算学生指定缴费类型的总金额
     * 
     * @param student 学生对象
     * @param paymentType 缴费类型
     * @return 该学生指定缴费类型的总金额
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByStudentAndPaymentType(Student student, String paymentType) {
        Double amount = paymentRepository.sumAmountByStudentAndPaymentType(student, paymentType);
        return amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO;
    }
    
    // 业务逻辑操作
    /**
     * 创建缴费记录
     * 
     * @param student 学生对象
     * @param paymentType 缴费类型
     * @param amount 缴费金额
     * @param paymentStatus 缴费状态
     * @param description 描述信息
     * @return 创建的缴费记录
     * @throws IllegalArgumentException 当参数无效时抛出
     * @since 2025-07-15
     */
    @Override
    public Payment createPayment(Student student, String paymentType, BigDecimal amount, String paymentStatus, String description) {
        if (!isValidAmount(amount)) {
            throw new IllegalArgumentException("缴费金额必须大于0");
        }
        if (!isValidPaymentStatus(paymentStatus)) {
            throw new IllegalArgumentException("缴费状态无效");
        }
        if (!isValidPaymentType(paymentType)) {
            throw new IllegalArgumentException("缴费类型无效");
        }
        
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setPaymentType(paymentType);
        payment.setAmount(amount.doubleValue());
        payment.setPaymentStatus(paymentStatus);
        payment.setDescription(description);
        payment.setPaymentDate(LocalDate.now());
        
        return savePayment(payment);
    }
    
    /**
     * 更新缴费记录
     * 
     * @param id 缴费记录ID
     * @param paymentType 缴费类型
     * @param amount 缴费金额
     * @param paymentStatus 缴费状态
     * @param description 描述信息
     * @return 更新后的缴费记录
     * @throws IllegalArgumentException 当记录不存在或参数无效时抛出
     * @since 2025-07-15
     */
    @Override
    public Payment updatePayment(Integer id, String paymentType, BigDecimal amount, String paymentStatus, String description) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("缴费记录不存在"));
        
        if (amount != null && !isValidAmount(amount)) {
            throw new IllegalArgumentException("缴费金额必须大于0");
        }
        if (paymentStatus != null && !isValidPaymentStatus(paymentStatus)) {
            throw new IllegalArgumentException("缴费状态无效");
        }
        if (paymentType != null && !isValidPaymentType(paymentType)) {
            throw new IllegalArgumentException("缴费类型无效");
        }
        
        if (paymentType != null) payment.setPaymentType(paymentType);
        if (amount != null) payment.setAmount(amount.doubleValue());
        if (paymentStatus != null) payment.setPaymentStatus(paymentStatus);
        if (description != null) payment.setDescription(description);
        
        return savePayment(payment);
    }
    
    /**
     * 更新缴费状态
     * 
     * @param id 缴费记录ID
     * @param paymentStatus 新的缴费状态
     * @return 更新后的缴费记录
     * @throws IllegalArgumentException 当记录不存在或状态无效时抛出
     * @since 2025-07-15
     */
    @Override
    public Payment updatePaymentStatus(Integer id, String paymentStatus) {
        if (!isValidPaymentStatus(paymentStatus)) {
            throw new IllegalArgumentException("缴费状态无效");
        }
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("缴费记录不存在"));
        
        payment.setPaymentStatus(paymentStatus);
        return savePayment(payment);
    }
    
    // 搜索功能
    /**
     * 搜索缴费记录
     * 
     * @param keyword 搜索关键词
     * @return 匹配的缴费记录列表
     * @since 2025-07-15
     */
    @Override
    @Transactional(readOnly = true)
    public List<Payment> searchPayments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        // 这里可以根据实际需求实现更复杂的搜索逻辑
        // 比如搜索缴费类型、描述等包含关键词的记录
        return paymentRepository.findByPaymentTypeContaining(keyword.trim());
    }
    
    // 验证功能
    /**
     * 验证缴费金额是否有效
     * 
     * @param amount 缴费金额
     * @return 如果金额大于0则返回true，否则返回false
     * @since 2025-07-15
     */
    @Override
    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 验证缴费状态是否有效
     * 
     * @param paymentStatus 缴费状态
     * @return 如果状态有效则返回true，否则返回false
     * @since 2025-07-15
     */
    @Override
    public boolean isValidPaymentStatus(String paymentStatus) {
        if (paymentStatus == null || paymentStatus.trim().isEmpty()) {
            return false;
        }
        
        String status = paymentStatus.trim();
        return "已缴费".equals(status) || "未缴费".equals(status) || "部分缴费".equals(status) || "已退费".equals(status);
    }
    
    /**
     * 验证缴费类型是否有效
     * 
     * @param paymentType 缴费类型
     * @return 如果类型有效则返回true，否则返回false
     * @since 2025-07-15
     */
    @Override
    public boolean isValidPaymentType(String paymentType) {
        if (paymentType == null || paymentType.trim().isEmpty()) {
            return false;
        }
        
        String type = paymentType.trim();
        return "学费".equals(type) || "住宿费".equals(type) || "教材费".equals(type) || "其他费用".equals(type);
    }
}
