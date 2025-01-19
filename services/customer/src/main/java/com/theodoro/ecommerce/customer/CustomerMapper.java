package com.theodoro.ecommerce.customer;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
public class CustomerMapper {
    public Customer toCustomer(@Valid CustomerRequest request) {
        if (request == null) {
            return null;
        }

        return Customer.builder()
                .id(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .address(request.address())
                .build();
    }

    public List<CustomerResponse> toListResponse(List<Customer> customer){
        return customer.stream().map(this::fromCustomer).toList();
    }

    public CustomerResponse fromCustomer(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getAddress()
        );
    }
}
