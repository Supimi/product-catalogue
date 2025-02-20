package com.efuture.product.repository;

import com.efuture.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryAndStatus(String category, String status);

    List<Product> findByStatusAndPriceGreaterThanEqualOrderByPrice(String status, BigDecimal price);
}
