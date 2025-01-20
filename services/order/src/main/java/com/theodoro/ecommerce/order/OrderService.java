package com.theodoro.ecommerce.order;

import com.theodoro.ecommerce.customer.CustomerClient;
import com.theodoro.ecommerce.customer.CustomerResponse;
import com.theodoro.ecommerce.exception.BusinessException;
import com.theodoro.ecommerce.kafka.OrderConfirmation;
import com.theodoro.ecommerce.kafka.OrderProducer;
import com.theodoro.ecommerce.orderline.OrderLineRequest;
import com.theodoro.ecommerce.orderline.OrderLineService;
import com.theodoro.ecommerce.product.ProductClient;
import com.theodoro.ecommerce.product.PurchaseRequest;
import com.theodoro.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(@Valid OrderRequest request) {
        CustomerResponse customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists no provide ID"));

        List<PurchaseResponse> purchasedProducts = this.productClient.purchaseProducts(request.products());

        Order order = this.orderRepository.save(orderMapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::fromOrder).toList();
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId).map(orderMapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID:: %d", orderId)));
    }
}
