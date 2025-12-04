package org.example.productcatalog.mapper;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.service.ProductService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Named("ProductMapper")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Named("getProductMapper")
    static ProductMapper getInstance() {
        return INSTANCE;
    }

    @Named("getProductId")
    default long getProductId(String item, @Context CrudRepository<Product> repository) {
        return repository.getByKey(item).map(Product::getId).orElse(0L);
    }

    @Mappings({
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price"),
    })
    ProductDto toDto(Product data);

    @Mappings({
            @Mapping(source = "item", target = "id", qualifiedByName = "getProductId"),
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "brand", target = "brand"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "category", target = "category"),
            @Mapping(source = "price", target = "price")
    })
    Product fromDto(ProductDto data, @Context CrudRepository<Product> repository);
}
