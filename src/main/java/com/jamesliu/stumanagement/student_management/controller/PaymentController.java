package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.Service.PaymentService.IPaymentService;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final IPaymentService paymentService;
    private final StudentRepository studentRepository;

    public PaymentController(IPaymentService paymentService, 
                           StudentRepository studentRepository) {
        this.paymentService = paymentService;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> addPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.savePayment(payment);
            return ResponseMessage.success(savedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> updatePayment(
            @PathVariable Integer id, 
            @RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id,
                payment.getPaymentType(), payment.getAmount(), 
                payment.getPaymentStatus(), payment.getDescription());
            return ResponseMessage.success(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestBody PaymentStatusUpdateRequest request) {
        try {
            String status = request.isCompleted() ? "已缴费" : "未缴费";
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseMessage.success(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deletePayment(@PathVariable Integer id) {
        paymentService.deleteById(id);
        return ResponseMessage.success("缴费记录删除成功");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Payment> getPaymentById(@PathVariable Integer id) {
        Optional<Payment> payment = paymentService.findById(id);
        return payment.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("缴费记录不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        return ResponseMessage.success(payments);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Payment>> getPaymentsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findAll(pageable);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudent(@PathVariable Integer studentId) {
        List<Payment> payments = paymentService.findByStudentId(studentId);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.findByPaymentStatus(status);
        return ResponseMessage.success(payments);
    }

    // 内部类用于状态更新请求
    public static class PaymentStatusUpdateRequest {
        private boolean completed;
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
