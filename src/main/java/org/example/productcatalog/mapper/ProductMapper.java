package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.ProductService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ProductService.class})
@Named("ProductMapper")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Named("getProductMapper")
    static ProductMapper getInstance() {
        return INSTANCE;
    }

    @Named("getProductId")
    default long getProductId(ProductDto data) {
        try {
            return new ProductService().find(data.getItem()).getId();
        } catch (ApplicationException e) {
            return 0L;
        }
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
            @Mapping(target = "id", expression = "java(getProductId(data))", dependsOn = {"item"}),
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price")
    })
    Product productDtoToProduct(ProductDto data);
}
