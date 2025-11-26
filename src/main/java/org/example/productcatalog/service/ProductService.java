package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class ProductService implements CrudService<Product, String> {
    private final CrudRepository<Product> repository;
    private final ProductCacheManager productCacheManager;

    public ProductService() {
        repository = new ProductRepository();
        productCacheManager = new ProductCacheManager();
    }

    @Override
    public Product create(Product product) {
        return Optional.ofNullable(product).map(x -> productCacheManager.put(repository.getByKey(x.getItem())
                .orElse(repository.add(x))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Product update(Product product) {
        var newProduct = Optional.ofNullable(product).map(x -> productCacheManager.put(repository.getByKey(x.getItem())
                        .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));

        newProduct.setBrand(product.getBrand());
        newProduct.setTitle(product.getTitle());
        newProduct.setCategory(product.getCategory());
        newProduct.setPrice(product.getPrice());
        return repository.update(newProduct);
    }

    @Override
    public Product remove(Product product) {
        return Optional.ofNullable(product).map(x -> repository.delete(productCacheManager.clear(x.getItem()).orElseGet(() ->
                repository.getByKey(x.getItem()).orElseThrow(() ->
                        new ApplicationException(PRODUCT_NOT_FOUND)))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Collection<Product> findFiltered(Product product) {
        return new Specification<>(product).apply(repository.getAll());
    }

    @Override
    public Collection<Product> findAll() { return repository.getAll(); }

    @Override
    public Product find(String item) {
        return Optional.ofNullable(item).map(x -> productCacheManager.get(x).orElse(repository.getByKey(x).orElseThrow(() ->
                new ApplicationException(PRODUCT_NOT_FOUND))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Product findById(long id) {
        return repository.getById(id)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND));
    }
}
