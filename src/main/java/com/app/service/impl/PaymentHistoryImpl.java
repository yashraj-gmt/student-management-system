package com.app.service.impl;

import com.app.entity.PaymentHistory;
import com.app.entity.Standard;
import com.app.entity.Student; // assuming you have this entity
import com.app.repository.PaymentHistoryRepository;
import com.app.repository.StandardRepository;
import com.app.repository.StudentRepository; // to get student + standard
import com.app.service.EmailService;
import com.app.service.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentHistoryImpl implements PaymentService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final StandardRepository standardRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;

    public PaymentHistoryImpl(PaymentHistoryRepository paymentHistoryRepository,
                              StandardRepository standardRepository,
                              StudentRepository studentRepository,
                              EmailService emailService) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.standardRepository = standardRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

    @Override
    public void savePayment(PaymentHistory payment) {
        if (payment.getStandard() == null || payment.getStandard().getId() == null) {
            throw new IllegalArgumentException("Standard or Standard ID cannot be null");
        }

        Standard standard = standardRepository.findById(payment.getStandard().getId())
                .orElseThrow(() -> new RuntimeException("Standard not found for id: " + payment.getStandard().getId()));

        if (payment.getStudent() == null || payment.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student or Student ID cannot be null");
        }

        List<PaymentHistory> existingPayments = paymentHistoryRepository.findByStudentId(payment.getStudent().getId());

        // Total discount applied so far - assuming discount only applies once, or you want total discounts summed
        BigDecimal totalDiscount = existingPayments.stream()
                .map(ph -> ph.getDiscountOpt() != null ? ph.getDiscountOpt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add discount from current payment only if not applied before
        if (payment.getDiscountOpt() != null && payment.getDiscountOpt().compareTo(BigDecimal.ZERO) > 0) {
            // Check if discount already applied
            if (totalDiscount.compareTo(BigDecimal.ZERO) == 0) {
                totalDiscount = totalDiscount.add(payment.getDiscountOpt());
            }
            // else discount already applied in earlier payment(s), so ignore current discount
        }

        BigDecimal totalFeesAfterDiscount = standard.getFees().subtract(totalDiscount);

        // Sum of amounts paid so far (excluding current payment)
        BigDecimal totalPaidSoFar = existingPayments.stream()
                .map(PaymentHistory::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total paid including this payment
        BigDecimal newTotalPaid = totalPaidSoFar.add(payment.getAmountPaid());

        // Calculate pending amount
        BigDecimal amountPending = totalFeesAfterDiscount.subtract(newTotalPaid);

        // Make sure pending is not negative (in case of overpayment)
        if (amountPending.compareTo(BigDecimal.ZERO) < 0) {
            amountPending = BigDecimal.ZERO;
        }

        payment.setAmountPending(amountPending);
        payment.setDate(LocalDateTime.now());
        payment.setStandard(standard);

        paymentHistoryRepository.save(payment);

        // ✅ Send confirmation email after saving payment
        Student student = studentRepository.findById(payment.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String to = student.getEmail();
        String subject = "Payment Confirmation - Student Management System";
        String body = "Dear " + student.getFirstName() + " " + student.getLastName() + ",\n\n"
                + "We have received your payment.\n\n"
                + "🧾 Payment Details:\n"
                + "Date: " + payment.getDate().toLocalDate() + "\n"
                + "Amount Paid: ₹" + payment.getAmountPaid() + "\n"
                + "Discount: ₹" + (payment.getDiscountOpt() != null ? payment.getDiscountOpt() : "0.00") + "\n"
                + "Pending Amount: ₹" + amountPending + "\n"
                + "Payment Mode: " + payment.getPaymentMode() + "\n\n"
                + "📚 Standard: " + standard.getName() + "\n"
//                + "📅 Academic Year: " + standard.getAcademicYear() + "\n\n"
                + "Thank you for your payment.\n\n"
                + "Regards,\n"
                + "Student Management System";

        if (to != null && !to.isBlank()) {
            emailService.sendEmail(to, subject, body);
        }
    }

    @Override
    public BigDecimal getTotalPaid(Long studentId) {
        return paymentHistoryRepository.findByStudentId(studentId)
                .stream().map(PaymentHistory::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getPendingAmount(Long studentId) {
        List<PaymentHistory> payments = paymentHistoryRepository.findByStudentId(studentId);

        // If no payments exist, return full fee for the student's standard
        if (payments.isEmpty()) {
            // Find student to get standard
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found for id: " + studentId));

            Standard standard = student.getStandard();
            if (standard == null) {
                throw new RuntimeException("Standard not found for student id: " + studentId);
            }

            return standard.getFees();
        }

        // Sum of discounts applied so far
        BigDecimal totalDiscount = payments.stream()
                .map(ph -> ph.getDiscountOpt() != null ? ph.getDiscountOpt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total fees after discount (use standard from any payment)
        Standard standard = payments.get(0).getStandard();
        BigDecimal totalFeesAfterDiscount = standard.getFees().subtract(totalDiscount);

        BigDecimal totalPaid = getTotalPaid(studentId);

        BigDecimal pending = totalFeesAfterDiscount.subtract(totalPaid);

        return pending.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : pending;
    }

    @Override
    public List<PaymentHistory> getPaymentHistory(Long studentId) {
        return paymentHistoryRepository.findByStudentId(studentId);
    }
}
