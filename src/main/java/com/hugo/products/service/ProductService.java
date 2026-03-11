package com.hugo.products.service;

import com.hugo.products.dto.ProductPageResponse;
import com.hugo.products.dto.ProductRequest;
import com.hugo.products.dto.ProductResponse;
import com.hugo.products.mapper.ProductMapper;
import com.hugo.products.model.Product;
import com.hugo.products.model.ProductStatus;
import com.hugo.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse create(ProductRequest request) {
        log.info("Creating product with sku={}, name={}", request.getSku(), request.getName());
        if (productRepository.existsBySku(request.getSku())) {
            log.warn("Product creation failed because sku={} already exists", request.getSku());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A product with this SKU already exists"
            );
        }

        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id={}, sku={}", savedProduct.getId(), savedProduct.getSku());

        return productMapper.toResponse(savedProduct);
    }

    public ProductPageResponse findAll(
            ProductStatus status,
            String search,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;

        boolean hasStatus = status != null;
        boolean hasSearch = search != null && !search.isBlank();

        if (hasStatus && hasSearch) {
            productPage = productRepository
                    .findByStatusAndNameContainingIgnoreCaseOrStatusAndSkuContainingIgnoreCase(
                            status, search, status, search, pageable
                    );
        } else if (hasStatus) {
            productPage = productRepository.findByStatus(status, pageable);
        } else if (hasSearch) {
            productPage = productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(
                    search, search, pageable
            );
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return ProductPageResponse.builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        return productMapper.toResponse(product);
    }

    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));

        if (!product.getSku().equals(request.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A product with this SKU already exists"
            );
        }

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public void validateExists(UUID id) {
        log.info("Validating existence of productId={}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Product not found for productId={}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }
    }

}