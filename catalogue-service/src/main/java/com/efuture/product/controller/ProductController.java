package com.efuture.product.controller;

import com.efuture.product.dto.CreateProductRequest;
import com.efuture.product.dto.ProductInformation;
import com.efuture.product.dto.UpdateProductRequest;
import com.efuture.product.service.ProductService;
import com.efuture.product.util.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String health() {
        return "SUCCESS";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Response<ProductInformation> createProduct(
            @Valid @RequestBody CreateProductRequest createProductRequest) {
        return productService.createProduct(createProductRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{product_id}")
    public Response<ProductInformation> updateProduct(
            @PathVariable("product_id") Long productId,
            @Valid @RequestBody UpdateProductRequest updateProductRequest) {
        return productService.updateProduct(productId, updateProductRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{product_id}")
    public Response<ProductInformation> deleteProduct(
            @PathVariable("product_id") Long productId) {
        return productService.deleteProduct(productId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/category/{category}")
    public Response<List<ProductInformation>> getProductsByCategory(
            @NotBlank(message = "Category is mandatory") @PathVariable("category") String category) {
        return productService.getProductByCategory(category);
    }

    @GetMapping("/premium")
    public Response<List<ProductInformation>> getPremiumProducts() {
        return productService.getPremiumProducts();
    }


}
