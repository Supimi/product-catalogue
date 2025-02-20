package com.efuture.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequest(
        @NotBlank(message = "Product name is mandatory")
        String name,
        String description,
        @Min(value = 0, message = "Price value should be grater than or equal to zero")
        double price
) {
}
