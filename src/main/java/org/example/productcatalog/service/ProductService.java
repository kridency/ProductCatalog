package org.example.productcatalog.service;

import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductService implements CrudService<ProductDto, String> {
    private final CrudRepository<Product> repository;
    private final ProductMapper mapper;
    private final ProductCacheManager productCacheManager;

    public ProductService() {
        repository = new ProductRepository();
        mapper = ProductMapper.getInstance();
        productCacheManager = new ProductCacheManager();
    }

    @Override
    public ProductDto create(ProductDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository))
                .map(repository::add).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public ProductDto update(ProductDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository))
                .map(repository::update).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public ProductDto remove(ProductDto data) {
        return Optional.ofNullable(data).map(x -> mapper.fromDto(x, repository))
                .map(x -> productCacheManager.clear(x.getItem())).map(repository::delete).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Collection<ProductDto> findFiltered(ProductDto data) {
        return new Specification<>(mapper.fromDto(data, repository)).apply(repository.getAll()).stream()
                .map(mapper::toDto).toList();
    }

    @Override
    public Collection<ProductDto> findAll() { return repository.getAll().stream().map(mapper::toDto).toList(); }

    @Override
    public ProductDto find(String item) {
        return Optional.ofNullable(item).map(x -> productCacheManager.get(x)
                        .orElse(repository.getByKey(x).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND))))
                .map(mapper::toDto).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public ProductDto findById(long id) {
        return repository.getById(id).map(mapper::toDto).orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }
}
