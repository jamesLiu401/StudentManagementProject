package com.jamesliu.stumanagement.student_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 缴费 DTO
 */
public class PaymentDTO {
    private Integer paymentId;
    private Integer stuId;
    private BigDecimal amount;
    private String paymentType;
    private String paymentStatus;
    private String description;
    private LocalDate paymentDate;

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public Integer getStuId() { return stuId; }
    public void setStuId(Integer stuId) { this.stuId = stuId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
}


