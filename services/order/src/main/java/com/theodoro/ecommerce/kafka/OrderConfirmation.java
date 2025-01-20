package com.theodoro.ecommerce.kafka;

import com.theodoro.ecommerce.customer.CustomerResponse;
import com.theodoro.ecommerce.order.PaymentMethod;
import com.theodoro.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
