package com.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    private Standard standard;

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

