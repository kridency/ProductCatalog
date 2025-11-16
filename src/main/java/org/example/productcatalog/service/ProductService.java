package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.cache.ProductCacheManager;
import org.example.productcatalog.util.specification.Specification;

import java.util.*;

import static org.example.productcatalog.preset.ProductCatalogInit.PRODUCT_NOT_FOUND;

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
        return productCacheManager.put(productRepository.getByItem(product.getItem())
                .orElse(productRepository.add(product)));
    }

    @Override
    public Product update(Product product) {
        var obj = productCacheManager.put(productRepository.getByItem(product.getItem()).orElseThrow(() ->
                        new ApplicationException(PRODUCT_NOT_FOUND)));

        obj.setBrand(product.getBrand());
        obj.setTitle(product.getTitle());
        obj.setCategory(product.getCategory());
        obj.setPrice(product.getPrice());
        return productRepository.update(obj);
    }

    @Override
    public Product remove(Product product) {
        return productRepository.delete(productCacheManager.clear(product.getItem()).orElseGet(() ->
                productRepository.getByItem(product.getItem()).orElseThrow(() ->
                        new ApplicationException(PRODUCT_NOT_FOUND))));
    }

    @Override
    public Collection<Product> findFiltered(Product product) {
        return new Specification<>(product).apply(productRepository.getAll());
    }

    @Override
    public Collection<Product> findAll() { return productRepository.getAll(); }

    @Override
    public Product find(String item) {
        return productCacheManager.get(item).orElse(productRepository.getByItem(item).orElseThrow(() ->
                new ApplicationException(PRODUCT_NOT_FOUND)));
    }
}
