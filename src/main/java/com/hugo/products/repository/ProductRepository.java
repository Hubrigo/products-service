package com.hugo.products.repository;

import com.hugo.products.model.Product;
import com.hugo.products.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    Page<Product> findByStatusAndNameContainingIgnoreCaseOrStatusAndSkuContainingIgnoreCase(
            ProductStatus status1,
            String name,
            ProductStatus status2,
            String sku,
            Pageable pageable
    );

    Page<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
            String name,
            String sku,
            Pageable pageable
    );

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}