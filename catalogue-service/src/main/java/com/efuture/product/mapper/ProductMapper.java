package com.efuture.product.mapper;

import com.efuture.product.dto.CreateProductRequest;
import com.efuture.product.dto.ProductInformation;
import com.efuture.product.dto.UpdateProductRequest;
import com.efuture.product.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    Product mapToProduct(CreateProductRequest createProductRequest);

    @Mapping(target = "productId", source = "id")
    ProductInformation mapToProductInformation(Product product);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapUpdateRequestToProduct(UpdateProductRequest dto, @MappingTarget Product product);

    List<ProductInformation> mapToProductInformationList(List<Product> productList);
}
