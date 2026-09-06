package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Named("ProductMapper")
public interface ProductMapper {
    ProductDto toDto(Product data);

    @Mapping(target = "id", ignore = true)
    Product fromDto(ProductDto data);

    void updateEntityFromDto(ProductDto data, @MappingTarget Product entity);
}
