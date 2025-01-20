package com.theodoro.ecommerce.product;

import com.theodoro.ecommerce.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Integer createProduct(ProductRequest request) {
        return productRepository.save(productMapper.toProduct(request)).getId();
    }

    public ProductResponse findById(Integer productId) {
        return productRepository.findById(productId).map(productMapper::toProductResponse).orElseThrow(() -> new EntityNotFoundException("Product not found with the ID:: " + productId));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(productMapper::toProductResponse).toList();
    }

    public List<ProductPurchaseResponse> purchaseProducts(@Valid List<ProductPurchaseRequest> requests) {
        List<Integer> productIds = requests.stream().map(ProductPurchaseRequest::productId).toList();
        List<Product> storedProducts = productRepository.findAllByIdInOrderById(productIds);

        if(productIds.size() != storedProducts.size()){
            throw new ProductPurchaseException("One or more products does not exist");
        }

        List<ProductPurchaseRequest> storedRequests = requests.stream().sorted(Comparator.comparing(ProductPurchaseRequest::productId)).toList();
        List<ProductPurchaseResponse> productPurchaseResponses = new ArrayList<>();

        for (int i = 0; i<storedProducts.size(); i++){
            Product product = storedProducts.get(i);
            ProductPurchaseRequest productPurchaseRequest = storedRequests.get(i);

            if(product.getAvailableQuantity() < productPurchaseRequest.quantity()){
                throw new ProductPurchaseException("Insufficient stock for product with ID:: " + productPurchaseRequest.quantity());
            }
            double newAvailableQuantity = product.getAvailableQuantity() - productPurchaseRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            productRepository.save(product);
            productPurchaseResponses.add(productMapper.toProductPurchaseResponse(product, productPurchaseRequest.quantity()));
        }
        return productPurchaseResponses;
    }
}
