package com.hugo.products.mapper;

import com.hugo.products.dto.ProductRequest;
import com.hugo.products.dto.ProductResponse;
import com.hugo.products.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }
}