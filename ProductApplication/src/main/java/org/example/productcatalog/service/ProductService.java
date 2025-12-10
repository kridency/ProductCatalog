package org.example.productcatalog.service;

import jakarta.transaction.Transactional;
import org.example.productcatalog.dto.ProductDto;
import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.mapper.ProductMapper;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

@Service("ProductService")
public class ProductService implements CrudService<ProductDto, String> {
    private final CrudRepository<Product> repository;
    private final ProductMapper mapper;
    private final ProductCacheManager productCacheManager;

    @Autowired
    public ProductService(CrudRepository<Product> repository, ProductMapper mapper, ProductCacheManager productCacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.productCacheManager = productCacheManager;
    }

    @Transactional
    @Override
    public ProductDto create(ProductDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto)
                .map(repository::add).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public ProductDto update(ProductDto data) {
        return Optional.ofNullable(data).map(mapper::fromDto)
                .map(repository::update).map(productCacheManager::put).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Transactional
    @Override
    public ProductDto remove(ProductDto data) {
        Optional.ofNullable(data).ifPresent(x -> productCacheManager.clear(x.getItem()));
        return Optional.ofNullable(data).map(mapper::fromDto)
                .map(repository::delete).map(mapper::toDto)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Collection<ProductDto> findFiltered(ProductDto data) {
        return new Specification<>(data).apply(findAll()).stream().toList();
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
        return repository.getById(id).map(mapper::toDto).orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND));
    }
}
