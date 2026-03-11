package com.hugo.products.dto;

import com.hugo.products.model.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private UUID id;
    private String sku;
    private String name;
    private BigDecimal price;
    private ProductStatus status;
    private Instant createdAt;
}