package com.efuture.product;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan("com.efuture.product")
@OpenAPIDefinition(
        info = @Info(
                title = "Product-catalogue API",
                version = "1.0",
                description = "This service handles product details"
        )
)
@EnableTransactionManagement
@SpringBootApplication
public class CatalogueServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogueServiceApplication.class, args);
    }

}
