package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Service.PaymentService.IPaymentService;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByType(@PathVariable String type) {
        List<Payment> payments = paymentService.findByPaymentType(type);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/type/{type}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Payment>> getPaymentsByTypePage(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findByPaymentType(type, pageable);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/status/{status}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<Payment>> getPaymentsByStatusPage(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findByPaymentStatus(status, pageable);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            List<Payment> payments = paymentService.findByPaymentDateBetween(start, end);
            return ResponseMessage.success(payments);
        } catch (Exception e) {
            return ResponseMessage.error("日期格式错误，请使用yyyy-MM-dd格式");
        }
    }

    @GetMapping("/amount-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByAmountRange(
            @RequestParam java.math.BigDecimal minAmount,
            @RequestParam java.math.BigDecimal maxAmount) {
        List<Payment> payments = paymentService.findByAmountBetween(minAmount, maxAmount);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/student/{studentId}/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudentAndType(
            @PathVariable Integer studentId,
            @PathVariable String type) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndPaymentType(student, type);
            return ResponseMessage.success(payments);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudentAndStatus(
            @PathVariable Integer studentId,
            @PathVariable String status) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndPaymentStatus(student, status);
            return ResponseMessage.success(payments);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/type/{type}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByTypeAndStatus(
            @PathVariable String type,
            @PathVariable String status) {
        List<Payment> payments = paymentService.findByPaymentTypeAndPaymentStatus(type, status);
        return ResponseMessage.success(payments);
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudentAndDateRange(
            @PathVariable Integer studentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            List<Payment> payments = paymentService.findByStudentAndPaymentDateBetween(student, start, end);
            return ResponseMessage.success(payments);
        } catch (Exception e) {
            return ResponseMessage.error("学生不存在或日期格式错误");
        }
    }

    @GetMapping("/student/{studentId}/amount-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> getPaymentsByStudentAndAmountRange(
            @PathVariable Integer studentId,
            @RequestParam java.math.BigDecimal minAmount,
            @RequestParam java.math.BigDecimal maxAmount) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndAmountBetween(student, minAmount, maxAmount);
            return ResponseMessage.success(payments);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    // 统计相关API
    @GetMapping("/student/{studentId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countPaymentsByStudent(@PathVariable Integer studentId) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            long count = paymentService.countByStudent(student);
            return ResponseMessage.success(count);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/type/{type}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countPaymentsByType(@PathVariable String type) {
        long count = paymentService.countByPaymentType(type);
        return ResponseMessage.success(count);
    }

    @GetMapping("/status/{status}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countPaymentsByStatus(@PathVariable String status) {
        long count = paymentService.countByPaymentStatus(status);
        return ResponseMessage.success(count);
    }

    @GetMapping("/student/{studentId}/status/{status}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Long> countPaymentsByStudentAndStatus(
            @PathVariable Integer studentId,
            @PathVariable String status) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            long count = paymentService.countByStudentAndPaymentStatus(student, status);
            return ResponseMessage.success(count);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    // 金额统计相关API
    @GetMapping("/student/{studentId}/total-amount")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<java.math.BigDecimal> getTotalAmountByStudent(@PathVariable Integer studentId) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            java.math.BigDecimal total = paymentService.sumAmountByStudent(student);
            return ResponseMessage.success(total);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}/total-amount-by-id")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<java.math.BigDecimal> getTotalAmountByStudentId(@PathVariable Integer studentId) {
        java.math.BigDecimal total = paymentService.sumAmountByStudentId(studentId);
        return ResponseMessage.success(total);
    }

    @GetMapping("/type/{type}/total-amount")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<java.math.BigDecimal> getTotalAmountByType(@PathVariable String type) {
        java.math.BigDecimal total = paymentService.sumAmountByPaymentType(type);
        return ResponseMessage.success(total);
    }

    @GetMapping("/status/{status}/total-amount")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<java.math.BigDecimal> getTotalAmountByStatus(@PathVariable String status) {
        java.math.BigDecimal total = paymentService.sumAmountByPaymentStatus(status);
        return ResponseMessage.success(total);
    }

    @GetMapping("/student/{studentId}/type/{type}/total-amount")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<java.math.BigDecimal> getTotalAmountByStudentAndType(
            @PathVariable Integer studentId,
            @PathVariable String type) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            java.math.BigDecimal total = paymentService.sumAmountByStudentAndPaymentType(student, type);
            return ResponseMessage.success(total);
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    // 搜索功能
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<Payment>> searchPayments(@RequestParam String keyword) {
        List<Payment> payments = paymentService.searchPayments(keyword);
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
