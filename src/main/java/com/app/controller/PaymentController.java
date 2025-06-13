package com.app.controller;

import com.app.entity.PaymentHistory;
import com.app.entity.Student;
import com.app.repository.PaymentHistoryRepository;
import com.app.repository.StudentRepository;
import com.app.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/payments")
public class PaymentController {

    private final StudentRepository studentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentService paymentService;

    public PaymentController(StudentRepository studentRepository,
                             PaymentHistoryRepository paymentHistoryRepository,
                             PaymentService paymentService) {
        this.studentRepository = studentRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.paymentService = paymentService;
    }

    // Show form to add new payment for a student
    @GetMapping("/new/{studentId}")
    public String newPaymentForm(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id: " + studentId));
        PaymentHistory payment = new PaymentHistory();
        payment.setStudent(student);

        if (student.getStandard() != null) {
            payment.setStandard(student.getStandard());
        }

        model.addAttribute("payment", payment);
        model.addAttribute("paymentModes", PaymentHistory.PaymentMode.values());
        return "/admin/payment_form";
    }

    // Save a payment record
    @PostMapping("/save")
    public String savePayment(@ModelAttribute PaymentHistory paymentHistory) {
        if (paymentHistory.getStudent() == null || paymentHistory.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student or Student ID is null in paymentHistory");
        }
        paymentService.savePayment(paymentHistory);
        return "redirect:/admin/payments/history/" + paymentHistory.getStudent().getId();
    }

    // View payment history for a student
  /*  @GetMapping("/history/{studentId}")
    public String viewHistory(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id: " + studentId));

        List<PaymentHistory> history = paymentService.getPaymentHistory(studentId);
        BigDecimal totalPaid = paymentService.getTotalPaid(studentId);
        BigDecimal pending = paymentService.getPendingAmount(studentId);

        model.addAttribute("student", student);
        model.addAttribute("history", history);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("pending", pending);
        return "/admin/payment_history";
    }*/

    // View payment history for a student (bifurcated by payment mode)
    @GetMapping("/history/{studentId}")
    public String viewHistory(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id: " + studentId));

        List<PaymentHistory> history = paymentService.getPaymentHistory(studentId);

        List<PaymentHistory> cashPayments = history.stream()
                .filter(p -> p.getPaymentMode() == PaymentHistory.PaymentMode.CASH)
                .collect(Collectors.toList());

        List<PaymentHistory> onlinePayments = history.stream()
                .filter(p -> p.getPaymentMode() == PaymentHistory.PaymentMode.ONLINE)
                .collect(Collectors.toList());

        List<PaymentHistory> chequePayments = history.stream()
                .filter(p -> p.getPaymentMode() == PaymentHistory.PaymentMode.CHEQUE)
                .collect(Collectors.toList());

        BigDecimal totalPaid = paymentService.getTotalPaid(studentId);
        BigDecimal pending = paymentService.getPendingAmount(studentId);

        model.addAttribute("student", student);
        model.addAttribute("cashPayments", cashPayments);
        model.addAttribute("onlinePayments", onlinePayments);
        model.addAttribute("chequePayments", chequePayments);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("pending", pending);

        return "/admin/payment_history";
    }

    // List all students with payment overview (paid, pending)
    @GetMapping("/students")
    public String allStudentsPaymentOverview(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<Student> studentPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            studentPage = studentRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrStandard_NameContainingIgnoreCase(
                            keyword, keyword, keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }

        Map<Long, BigDecimal> paidMap = studentPage.getContent().stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        s -> paymentService.getTotalPaid(s.getId())
                ));

        Map<Long, BigDecimal> pendingMap = studentPage.getContent().stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        s -> paymentService.getPendingAmount(s.getId())
                ));

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("paidMap", paidMap);
        model.addAttribute("pendingMap", pendingMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());

        return "/admin/all_students_payment";
    }
}
