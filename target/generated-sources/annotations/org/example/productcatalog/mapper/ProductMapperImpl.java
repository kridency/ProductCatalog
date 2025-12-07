package org.example.productcatalog.mapper;

import javax.annotation.processing.Generated;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T22:51:35+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Red Hat, Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto toDto(Product data) {
        if ( data == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setItem( data.getItem() );
        productDto.setBrand( data.getBrand() );
        productDto.setTitle( data.getTitle() );
        productDto.setCategory( data.getCategory() );
        productDto.setPrice( data.getPrice() );

        return productDto;
    }

    @Override
    public Product fromDto(ProductDto data, CrudRepository<Product> repository) {
        if ( data == null ) {
            return null;
        }

        Product product = new Product();

        product.setId( getProductId( data.getItem(), repository ) );
        product.setItem( data.getItem() );
        product.setBrand( data.getBrand() );
        product.setTitle( data.getTitle() );
        product.setCategory( data.getCategory() );
        product.setPrice( data.getPrice() );

        return product;
    }
}
