package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.FIELD)
@Named("ProductMapper")
public interface ProductMapper {
    @Mappings({
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price"),
    })
    ProductDto toDto(Product data);

    @Mappings({
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price")
    })
    Product fromDto(ProductDto data);
}
