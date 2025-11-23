package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.PRODUCT_NOT_FOUND;
import static org.example.productcatalog.preset.ProductCatalogInit.PRODUCT_NOT_SPECIFIED;

public class ProductService implements CrudService<Product, String> {
    private static ProductService INSTANCE;
    private final ProductRepository productRepository;
    private final ProductCacheManager productCacheManager;

    private ProductService() {
        productRepository = ProductRepository.getInstance();
        productCacheManager = new ProductCacheManager();
    }

    public static ProductService getInstance() {
        if(INSTANCE == null) INSTANCE = new ProductService();
        return INSTANCE;
    }

    @Override
    public Product create(Product product) {
        return Optional.ofNullable(product).map(x -> productCacheManager.put(productRepository.getByItem(x.getItem())
                .orElse(productRepository.add(x))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Product update(Product product) {
        var newProduct = Optional.ofNullable(product).map(x -> productCacheManager.put(productRepository.getByItem(x.getItem())
                        .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));

        newProduct.setBrand(product.getBrand());
        newProduct.setTitle(product.getTitle());
        newProduct.setCategory(product.getCategory());
        newProduct.setPrice(product.getPrice());
        return productRepository.update(newProduct);
    }

    @Override
    public Product remove(Product product) {
        return Optional.ofNullable(product).map(x -> productRepository.delete(productCacheManager.clear(x.getItem()).orElseGet(() ->
                productRepository.getByItem(x.getItem()).orElseThrow(() ->
                        new ApplicationException(PRODUCT_NOT_FOUND)))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }

    @Override
    public Collection<Product> findFiltered(Product product) {
        return new Specification<>(product).apply(productRepository.getAll());
    }

    @Override
    public Collection<Product> findAll() { return productRepository.getAll(); }

    @Override
    public Product find(String item) {
        return Optional.ofNullable(item).map(x -> productCacheManager.get(x).orElse(productRepository.getByItem(x).orElseThrow(() ->
                new ApplicationException(PRODUCT_NOT_FOUND))))
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_SPECIFIED));
    }
}
