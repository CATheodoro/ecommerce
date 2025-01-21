package com.theodoro.ecommerce.payment;

import com.theodoro.ecommerce.customer.CustomerResponse;
import com.theodoro.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
