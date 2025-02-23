package com.efuture.product.dto;

import jakarta.validation.constraints.Min;

public record UpdateProductRequest(
        String name,
        String description,
        @Min(value = 0, message = "Price value should be grater than or equal to zero")
        double price
) {
}
