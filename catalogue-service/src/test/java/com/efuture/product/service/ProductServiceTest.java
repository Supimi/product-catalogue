package com.efuture.product.service;

import com.efuture.product.dto.*;
import com.efuture.product.entity.Product;
import com.efuture.product.exception.InvalidRequestException;
import com.efuture.product.mapper.ProductMapper;
import com.efuture.product.repository.ProductRepository;
import com.efuture.product.util.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static com.efuture.product.util.Constants.*;
import static com.efuture.product.util.Constants.ErrorMsg.PRODUCT_NOT_FOUND;
import static com.efuture.product.util.Constants.ResponseCodes.STATUS_CREATED;
import static com.efuture.product.util.Constants.ResponseCodes.STATUS_OK;
import static com.efuture.product.util.Constants.ResponseMsg.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductInformation productInformation;
    private CreateProductRequest createProductRequest;
    private UpdateProductRequest updateProductRequest;
    private ProductCreationEvent productCreationEvent;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setStatus(ProductStatus.ACTIVE.getValue());

        productInformation = new ProductInformation();

        createProductRequest = new CreateProductRequest("product-1","p1",10,"category1");
        updateProductRequest = new UpdateProductRequest("product-1","p1",10);
        productCreationEvent = new ProductCreationEvent();
    }

    @Test
    void testCreateProduct() {
        when(productMapper.mapToProduct(createProductRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.mapToProductInformation(product)).thenReturn(productInformation);
        when(productMapper.mapToProductCreationEvent(product)).thenReturn(productCreationEvent);

        Response<ProductInformation> response = productService.createProduct(createProductRequest);

        assertEquals(STATUS_CREATED, response.getStatus());
        assertEquals(SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        verify(kafkaProducerService).sendMessage(productCreationEvent);
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> productService.updateProduct(1L, updateProductRequest));
        assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());

    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.mapToProductInformation(product)).thenReturn(productInformation);

        Response<ProductInformation> response = productService.updateProduct(1L, updateProductRequest);

        assertEquals(STATUS_OK, response.getStatus());
        assertEquals(SUCCESS, response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void testDeleteProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> productService.deleteProduct(1L));
        assertEquals(PRODUCT_NOT_FOUND, exception.getMessage());

    }

    @Test
    void testDeleteProduct_AlreadyDeleted() {
        product.setStatus(ProductStatus.DELETED.getValue());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Response<ProductInformation> response = productService.deleteProduct(1L);

        assertEquals(STATUS_OK, response.getStatus());
        assertEquals(String.format(ErrorDescription.PRODUCT_ALREADY_DELETED, 1L), response.getMessage());
    }

    @Test
    void testGetProductByCategory() {
        when(productRepository.findByCategoryAndStatus("Electronics", ProductStatus.ACTIVE.getValue()))
                .thenReturn(List.of(product));
        when(productMapper.mapToProductInformationList(List.of(product)))
                .thenReturn(List.of(productInformation));

        Response<List<ProductInformation>> response = productService.getProductByCategory("Electronics");

        assertEquals(STATUS_OK, response.getStatus());
        assertEquals(SUCCESS, response.getMessage());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void testGetPremiumProducts() {
        when(productRepository.findByStatusAndPriceGreaterThanEqualOrderByPrice(ProductStatus.ACTIVE.getValue(),
                PREMIUM_PRODUCT_PRICE_LIMIT)).thenReturn(List.of(product));
        when(productMapper.mapToProductInformationList(List.of(product)))
                .thenReturn(List.of(productInformation));

        Response<List<ProductInformation>> response = productService.getPremiumProducts();

        assertEquals(STATUS_OK, response.getStatus());
        assertEquals(SUCCESS, response.getMessage());
        assertFalse(response.getData().isEmpty());
    }
}
