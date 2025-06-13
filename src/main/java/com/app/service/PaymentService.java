package com.app.service;

import com.app.entity.PaymentHistory;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    void savePayment(PaymentHistory paymentHistory);

    BigDecimal getTotalPaid(Long studentId);

    BigDecimal getPendingAmount(Long studentId);

    List<PaymentHistory> getPaymentHistory(Long studentId);
}
