package com.theodoro.ecommerce.order;

import com.theodoro.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "Order amount should be positive")
        BigDecimal amount,
        @NotNull(message = "Payment methode should be precised")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer must be present")
        @NotEmpty(message = "Customer must be present")
        @NotBlank(message = "Customer must be present")
        String customerId,
        @NotEmpty(message = "You should at least purchase on product")
        List<PurchaseRequest> products
) {
}
