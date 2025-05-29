package com.app.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PaymentHistory {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private AcademicYear academicYear;

    private LocalDateTime date;
    private BigDecimal amountPaid;
    private BigDecimal amountPending;
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private BigDecimal discountOpt;
    private String referenceId;
    private String chequeNo;
    private String bankName;
    private String address;

    public enum PaymentMode {
        CASH, ONLINE, CHEQUE
    }
}

