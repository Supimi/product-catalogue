package com.efuture.product.service;

import com.efuture.product.dto.CreateProductRequest;
import com.efuture.product.dto.ProductCreationEvent;
import com.efuture.product.dto.ProductInformation;
import com.efuture.product.dto.UpdateProductRequest;
import com.efuture.product.entity.Product;
import com.efuture.product.exception.InvalidRequestException;
import com.efuture.product.mapper.ProductMapper;
import com.efuture.product.repository.ProductRepository;
import com.efuture.product.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.efuture.product.util.Constants.ErrorDescription.PRODUCT_ALREADY_DELETED;
import static com.efuture.product.util.Constants.ErrorDescription.PRODUCT_NOT_FOUND_DESCRIPTION;
import static com.efuture.product.util.Constants.ErrorMsg.INVALID_PRODUCT_ID;
import static com.efuture.product.util.Constants.ErrorMsg.PRODUCT_NOT_FOUND;
import static com.efuture.product.util.Constants.PREMIUM_PRODUCT_PRICE_LIMIT;
import static com.efuture.product.util.Constants.ProductStatus.ACTIVE;
import static com.efuture.product.util.Constants.ProductStatus.DELETED;
import static com.efuture.product.util.Constants.ResponseCodes.STATUS_CREATED;
import static com.efuture.product.util.Constants.ResponseCodes.STATUS_OK;
import static com.efuture.product.util.Constants.ResponseMsg.SUCCESS;

@Slf4j
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final KafkaProducerService kafkaProducerService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          KafkaProducerService kafkaProducerService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Save products to db
     * @param createProductRequest product create request
     * @return product creation response
     */
    public Response<ProductInformation> createProduct(CreateProductRequest createProductRequest) {
        log.info("create product request:{}", createProductRequest);
        //map the request to db record and save
        Product product = productMapper.mapToProduct(createProductRequest);
        product.setStatus(ACTIVE.getValue());
        Product saved = productRepository.save(product);

        //map the request to kafka event message and send it into the configured topic
        ProductInformation productInformation = productMapper.mapToProductInformation(saved);
        ProductCreationEvent event = productMapper.mapToProductCreationEvent(saved);
        kafkaProducerService.sendMessage(event);

        return Response.<ProductInformation>builder()
                .status(STATUS_CREATED)
                .message(SUCCESS)
                .data(productInformation)
                .build();
    }

    /**
     * Update existing products
     * @param productId Product Id
     * @param updateProductRequest Update product request
     * @return Update product response
     */
    public Response<ProductInformation> updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
        log.info("update product request:{}", updateProductRequest);

        //find the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidRequestException(PRODUCT_NOT_FOUND,
                        String.format(PRODUCT_NOT_FOUND_DESCRIPTION, productId)));
        if (DELETED.getValue().equals(product.getStatus())) {
            throw new InvalidRequestException(INVALID_PRODUCT_ID,
                    String.format(PRODUCT_ALREADY_DELETED, productId));
        }
        //map update product request body to existing product record
        productMapper.mapUpdateRequestToProduct(updateProductRequest, product);
        Product saved = productRepository.save(product);

        //map to response data
        ProductInformation productInformation = productMapper.mapToProductInformation(saved);

        return Response.<ProductInformation>builder()
                .status(STATUS_OK)
                .message(SUCCESS)
                .data(productInformation)
                .build();
    }

    /**
     * Delete products (Soft Delete) -mark product status as 'D'
     * @param productId product id
     * @return response data
     */
    public Response<ProductInformation> deleteProduct(Long productId) {
        log.info("delete product request:{}", productId);

        //retrieve the existing product in the db
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidRequestException(PRODUCT_NOT_FOUND,
                        String.format(PRODUCT_NOT_FOUND_DESCRIPTION, productId)));

        String message;
        //handle already deleted products
        if (DELETED.getValue().equals(product.getStatus())) {
            log.info("product is already deleted");
            message = String.format(PRODUCT_ALREADY_DELETED, productId);
        } else {
            product.setStatus(DELETED.getValue());
            product = productRepository.save(product);
            message = SUCCESS;
        }

        //map to response data
        ProductInformation productInformation = productMapper.mapToProductInformation(product);
        return Response.<ProductInformation>builder()
                .status(STATUS_OK)
                .message(message)
                .data(productInformation)
                .build();
    }

    /**
     * Retrieve all products by category
     * @param category category
     * @return product list
     */
    public Response<List<ProductInformation>> getProductByCategory(String category) {
        log.info("get active products by category:{}", category);

        //find active products by category
        List<Product> products = productRepository.findByCategoryAndStatus(category, ACTIVE.getValue());
        //map to response list
        List<ProductInformation> productInformationList = productMapper.mapToProductInformationList(products);
        return Response.<List<ProductInformation>>builder()
                .status(STATUS_OK)
                .message(SUCCESS)
                .data(productInformationList)
                .build();
    }

    /**
     * Retrieve premium products
     * If the product price is greater than 500, it is considered as premium product
     * @return product list
     */
    public Response<List<ProductInformation>> getPremiumProducts() {
        log.info("get active premium products");
        //find all active premium products
        List<Product> products = productRepository
                .findByStatusAndPriceGreaterThanEqualOrderByPrice(ACTIVE.getValue(), PREMIUM_PRODUCT_PRICE_LIMIT);
        //map to response
        List<ProductInformation> productInformationList = productMapper.mapToProductInformationList(products);
        return Response.<List<ProductInformation>>builder()
                .status(STATUS_OK)
                .message(SUCCESS)
                .data(productInformationList)
                .build();
    }
}
