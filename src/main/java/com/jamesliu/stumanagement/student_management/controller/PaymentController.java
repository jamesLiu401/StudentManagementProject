package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Entity.Student.Student;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.PaymentRepository;
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

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    public PaymentController(PaymentRepository paymentRepository, 
                           StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> addPayment(@RequestBody Payment payment) {
        // 智能验证学生是否存在
        if (payment.getStudent() == null || payment.getStudent().getStuId() == null) {
            return ResponseMessage.error("学生ID不能为空");
        }
        
        Optional<Student> student = studentRepository.findById(payment.getStudent().getStuId());
        if (!student.isPresent()) {
            return ResponseMessage.error("学生不存在");
        }
        
        // 设置学生信息
        payment.setStudent(student.get());
        
        // 设置默认值
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseMessage.success(savedPayment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> updatePayment(
            @PathVariable Integer id, 
            @RequestBody Payment payment) {
        return paymentRepository.findById(id)
                .map(existingPayment -> {
                    existingPayment.setPaymentItem(payment.getPaymentItem());
                    existingPayment.setAmount(payment.getAmount());
                    existingPayment.setPaymentDate(payment.getPaymentDate());
                    existingPayment.setCompleted(payment.isCompleted());
                    return paymentRepository.save(existingPayment);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("缴费记录不存在"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Payment> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestBody PaymentStatusUpdateRequest request) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setCompleted(request.isCompleted());
                    if (request.isCompleted() && payment.getPaymentDate() == null) {
                        payment.setPaymentDate(LocalDate.now());
                    }
                    return paymentRepository.save(payment);
                })
                .map(ResponseMessage::success)
                .orElse(ResponseMessage.error("缴费记录不存在"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<String> deletePayment(@PathVariable Integer id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseMessage.success("缴费记录删除成功");
        }
        return ResponseMessage.error("缴费记录不存在");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Payment> getPaymentById(@PathVariable Integer id) {
        Optional<Payment> payment = paymentRepository.findById(id);
        return payment.map(ResponseMessage::success)
                .orElse(ResponseMessage.error("缴费记录不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
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
        Page<Payment> payments = paymentRepository.findAll(pageable);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudent(@PathVariable Integer studentId) {
        List<Payment> payments = paymentRepository.findByStudentStuId(studentId);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/status/{completed}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStatus(@PathVariable boolean completed) {
        List<Payment> payments = paymentRepository.findByIsCompleted(completed);
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
