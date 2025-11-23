package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.ProductService;
import org.example.productcatalog.service.UserService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.INPUT_ERROR;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Named("TransactionMapper")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Named("getTransactionMapper")
    static ProductMapper getInstance() {
        return INSTANCE;
    }

    @Named("getTransactionId")
    default long getProductId(ProductDto data) {
        return ProductService.getInstance().find(data.getItem()).getId();
    }

    @Mappings({
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price"),
    })
    ProductDto productToProductDto(Product data);

    @Mappings({
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price")
    })
    Product productDtoToProduct(ProductDto data);
}
