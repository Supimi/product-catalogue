package com.efuture.product.controller;

import com.efuture.product.config.CustomAccessDeniedHandler;
import com.efuture.product.config.CustomAuthenticationEntryPoint;
import com.efuture.product.config.SecurityConfig;
import com.efuture.product.dto.CreateProductRequest;
import com.efuture.product.dto.ProductInformation;
import com.efuture.product.dto.UpdateProductRequest;
import com.efuture.product.service.ProductService;
import com.efuture.product.util.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;


    @MockitoBean
    JwtDecoder jwtDecoder;


    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createProductShouldReturnCreatedResponse_forAdminRole() throws Exception {

        CreateProductRequest request = new CreateProductRequest("product-1", "p1", 10, "category1");
        ProductInformation productInformation = new ProductInformation();
        Response<ProductInformation> response = new Response<>(201, "SUCCESS", productInformation);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // Sample JSON body
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void createProductShouldReturnForbiddenResponse_forUserRole() throws Exception {

        CreateProductRequest request = new CreateProductRequest("product-1", "p1", 10, "category1");
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // Sample JSON body
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_User")))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access Denied"))
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource."))
        ;
    }


    @Test
    void updateProductShouldReturnOkResponse_forAdminRole() throws Exception {
        UpdateProductRequest updateRequest = new UpdateProductRequest("product-1", "p1", 10);
        ProductInformation updatedProduct = new ProductInformation();
        Response<ProductInformation> response = new Response<>(200, "SUCCESS", updatedProduct);

        when(productService.updateProduct(any(Long.class), any(UpdateProductRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void updateProductShouldReturnForbiddenResponse_forUserRole() throws Exception {
        UpdateProductRequest updateRequest = new UpdateProductRequest("product-1", "p1", 10);
        ProductInformation updatedProduct = new ProductInformation();
        Response<ProductInformation> response = new Response<>(200, "SUCCESS", updatedProduct);

        when(productService.updateProduct(any(Long.class), any(UpdateProductRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_User")))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProductShouldReturnOkResponse_forAdminRole() throws Exception {
        ProductInformation deletedProduct = new ProductInformation();
        Response<ProductInformation> response = new Response<>(200, "SUCCESS", deletedProduct);

        when(productService.deleteProduct(any(Long.class))).thenReturn(response);

        mockMvc.perform(delete("/api/products/1")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void deleteProductShouldReturnOkResponse_forUserRole() throws Exception {
        ProductInformation deletedProduct = new ProductInformation();
        Response<ProductInformation> response = new Response<>(200, "SUCCESS", deletedProduct);

        when(productService.deleteProduct(any(Long.class))).thenReturn(response);

        mockMvc.perform(delete("/api/products/1")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_User"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProductsByCategoryShouldReturnList_forUserRole() throws Exception {
        ProductInformation productInfo = new ProductInformation();
        List<ProductInformation> products = List.of(productInfo);
        Response<List<ProductInformation>> response = new Response<>(200, "SUCCESS", products);

        when(productService.getProductByCategory(any(String.class))).thenReturn(response);

        mockMvc.perform(get("/api/products/category/electronics")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void getPremiumProductsShouldReturnList() throws Exception {
        ProductInformation productInfo = new ProductInformation();
        List<ProductInformation> products = List.of(productInfo);
        Response<List<ProductInformation>> response = new Response<>(200, "SUCCESS", products);

        when(productService.getPremiumProducts()).thenReturn(response);

        mockMvc.perform(get("/api/products/premium")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void unauthorizedUserCannotAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/products/premium")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Product 1\", \"price\": 100.0}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Should fail without authentication
    }

    @Test
    void validateCreateProduct_emptyProductName() throws Exception {
        CreateProductRequest request = new CreateProductRequest("", "p1", 10, "category1");
        ProductInformation productInformation = new ProductInformation();
        Response<ProductInformation> response = new Response<>(201, "SUCCESS", productInformation);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // Sample JSON body
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data").value("name: Product name is mandatory"));
    }

    @Test
    void validateCreateProduct_emptyCategoryName() throws Exception {
        CreateProductRequest request = new CreateProductRequest("product1", "p1", 10, "");
        ProductInformation productInformation = new ProductInformation();
        Response<ProductInformation> response = new Response<>(201, "SUCCESS", productInformation);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // Sample JSON body
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data").value("category: Product category is mandatory"));
    }

    @Test
    void validateCreateProduct_negativePrice() throws Exception {
        CreateProductRequest request = new CreateProductRequest("product1", "p1", -10, "category1");
        ProductInformation productInformation = new ProductInformation();
        Response<ProductInformation> response = new Response<>(201, "SUCCESS", productInformation);

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // Sample JSON body
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_Admin")))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data").value("price: Price value should be grater than or equal to zero"));
    }
}
