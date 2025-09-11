package com.jamesliu.stumanagement.student_management.Entity.Finance;

import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 缴费记录实体类
 * 管理学生的缴费信息，包括学费、住宿费等各类费用
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>缴费记录管理 - 记录学生的各项缴费情况</li>
 *   <li>缴费状态跟踪 - 跟踪缴费完成状态</li>
 *   <li>财务统计功能 - 支持按学生、项目、时间等维度统计</li>
 * </ul>
 * 
 * <p>关联关系：</p>
 * <ul>
 *   <li>多对一：缴费记录属于学生(Student)</li>
 *   <li>级联操作：删除学生时处理相关缴费记录</li>
 * </ul>
 * 
 * <p>数据库映射：</p>
 * <ul>
 *   <li>表名：payment_table</li>
 *   <li>主键：payment_id (自增)</li>
 *   <li>外键：stu_id</li>
 * </ul>
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2025-07-06
 */
@Entity
@Table(name = "payment_table")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stu_id")
    private Student student;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;
    
    @Column(name = "description")
    private String description;

    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", student=" + student +
                ", paymentType='" + paymentType + '\'' +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
