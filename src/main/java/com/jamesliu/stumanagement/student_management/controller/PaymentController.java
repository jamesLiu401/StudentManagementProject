package com.jamesliu.stumanagement.student_management.controller;

import com.jamesliu.stumanagement.student_management.Entity.Finance.Payment;
import com.jamesliu.stumanagement.student_management.Entity.ResponseMessage;
import com.jamesliu.stumanagement.student_management.Service.PaymentService.IPaymentService;
import com.jamesliu.stumanagement.student_management.repository.StuRepo.StudentRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.jamesliu.stumanagement.student_management.dto.PaymentDTO;
import com.jamesliu.stumanagement.student_management.dto.DtoMapper;

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
    public ResponseMessage<PaymentDTO> addPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.savePayment(payment);
            return ResponseMessage.success(DtoMapper.toDto(savedPayment));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<PaymentDTO> createPayment(@RequestBody PaymentCreateRequest request) {
        try {
            // 根据stuId查找学生
            var student = studentRepository.findById(request.getStuId())
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            
            // 创建Payment对象
            Payment payment = new Payment();
            payment.setStudent(student);
            payment.setPaymentType(request.getPaymentType());
            payment.setAmount(request.getAmount());
            payment.setPaymentStatus(request.getPaymentStatus());
            payment.setDescription(request.getDescription());
            payment.setPaymentDate(request.getPaymentDate());
            
            Payment savedPayment = paymentService.savePayment(payment);
            return ResponseMessage.success(DtoMapper.toDto(savedPayment));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<PaymentDTO> updatePayment(
            @PathVariable Integer id, 
            @RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id,
                payment.getPaymentType(), payment.getAmount(), 
                payment.getPaymentStatus(), payment.getDescription());
            return ResponseMessage.success(DtoMapper.toDto(updatedPayment));
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<PaymentDTO> updatePaymentStatus(
            @PathVariable Integer id,
            @RequestBody PaymentStatusUpdateRequest request) {
        try {
            String status = request.isCompleted() ? "已缴费" : "未缴费";
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseMessage.success(DtoMapper.toDto(updatedPayment));
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
    public ResponseMessage<PaymentDTO> getPaymentById(@PathVariable Integer id) {
        Optional<Payment> payment = paymentService.findById(id);
        return payment.map(p -> ResponseMessage.success(DtoMapper.toDto(p)))
                .orElse(ResponseMessage.error("缴费记录不存在"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getAllPayments() {
        List<Payment> payments = paymentService.findAll();
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<PaymentDTO>> getPaymentsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findAll(pageable);
        return ResponseMessage.success(payments.map(DtoMapper::toDto));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStudent(@PathVariable Integer studentId) {
        List<Payment> payments = paymentService.findByStudentId(studentId);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.findByPaymentStatus(status);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByType(@PathVariable String type) {
        List<Payment> payments = paymentService.findByPaymentType(type);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/type/{type}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<PaymentDTO>> getPaymentsByTypePage(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findByPaymentType(type, pageable);
        return ResponseMessage.success(payments.map(DtoMapper::toDto));
    }

    @GetMapping("/status/{status}/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<Page<PaymentDTO>> getPaymentsByStatusPage(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentService.findByPaymentStatus(status, pageable);
        return ResponseMessage.success(payments.map(DtoMapper::toDto));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            List<Payment> payments = paymentService.findByPaymentDateBetween(start, end);
            return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseMessage.error("日期格式错误，请使用yyyy-MM-dd格式");
        }
    }

    @GetMapping("/amount-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByAmountRange(
            @RequestParam java.math.BigDecimal minAmount,
            @RequestParam java.math.BigDecimal maxAmount) {
        List<Payment> payments = paymentService.findByAmountBetween(minAmount, maxAmount);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/student/{studentId}/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStudentAndType(
            @PathVariable Integer studentId,
            @PathVariable String type) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndPaymentType(student, type);
            return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStudentAndStatus(
            @PathVariable Integer studentId,
            @PathVariable String status) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndPaymentStatus(student, status);
            return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
        } catch (IllegalArgumentException e) {
            return ResponseMessage.error(e.getMessage());
        }
    }

    @GetMapping("/type/{type}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByTypeAndStatus(
            @PathVariable String type,
            @PathVariable String status) {
        List<Payment> payments = paymentService.findByPaymentTypeAndPaymentStatus(type, status);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
    }

    @GetMapping("/student/{studentId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStudentAndDateRange(
            @PathVariable Integer studentId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            List<Payment> payments = paymentService.findByStudentAndPaymentDateBetween(student, start, end);
            return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
        } catch (Exception e) {
            return ResponseMessage.error("学生不存在或日期格式错误");
        }
    }

    @GetMapping("/student/{studentId}/amount-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseMessage<List<PaymentDTO>> getPaymentsByStudentAndAmountRange(
            @PathVariable Integer studentId,
            @RequestParam java.math.BigDecimal minAmount,
            @RequestParam java.math.BigDecimal maxAmount) {
        try {
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));
            List<Payment> payments = paymentService.findByStudentAndAmountBetween(student, minAmount, maxAmount);
            return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
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
    public ResponseMessage<List<PaymentDTO>> searchPayments(@RequestParam String keyword) {
        List<Payment> payments = paymentService.searchPayments(keyword);
        return ResponseMessage.success(payments.stream().map(DtoMapper::toDto).toList());
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

    // 内部类用于创建缴费记录请求
    public static class PaymentCreateRequest {
        private Integer stuId;
        private String paymentType;
        private Double amount;
        private String paymentStatus;
        private String description;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private java.time.LocalDate paymentDate;
        
        // Getters and Setters
        public Integer getStuId() {
            return stuId;
        }
        
        public void setStuId(Integer stuId) {
            this.stuId = stuId;
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
        
        public java.time.LocalDate getPaymentDate() {
            return paymentDate;
        }
        
        public void setPaymentDate(java.time.LocalDate paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
}
