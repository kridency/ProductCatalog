package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ProductService implements CrudService<Product> {
    private static ProductService INSTANCE;
    private final ProductRepository productRepository;
    private final ProductCacheManager productCacheManager;

    private ProductService() {
        productRepository = ProductRepository.getInstance();
        productCacheManager = ProductCacheManager.getInstance();
    }

    public static ProductService getInstance() {
        if(INSTANCE == null) INSTANCE = new ProductService();
        return INSTANCE;
    }

    @Override
    public Product create(Product product) {
        return productCacheManager.get(product.getItem())
                .orElseGet(() -> {
                    var value =  productRepository.getByItem(product.getItem()).orElseGet(() -> productRepository.save(product));
                    productCacheManager.put(product.getItem(), value);
                    return value;
                });
    }

    @Override
    public Product update(Product product) {
        return productCacheManager.get(product.getItem())
                .orElseGet(() -> {
                    var value = productRepository.getByItem(product.getItem()).map(productRepository::save).orElse(null);
                    productCacheManager.put(product.getItem(), value);
                    return value;
                });
    }

    @Override
    public Product remove(Product product) {
        return productCacheManager.get(product.getItem())
                .orElseGet(() -> {
                    var value = productRepository.getByItem(product.getItem()).map(productRepository::delete).orElse(null);
                    productCacheManager.clear(product.getItem());
                    return value;
                });
    }

    @Override
    public Collection<Product> findFiltered(Product product) {
        Map<String, Optional<?>> criteria =
                Arrays.stream(product.getClass().getDeclaredFields()).filter(field -> !field.getName().equals("id"))
                        .collect(Collectors.toMap(Field::getName, field -> {
                            try {
                                field.setAccessible(true);
                                return Optional.ofNullable(field.get(product));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }));

        return new Specification<Product>(criteria).apply(productRepository.getAll());
    }

    @Override
    public Collection<Product> findAll() { return productRepository.getAll(); }
}
