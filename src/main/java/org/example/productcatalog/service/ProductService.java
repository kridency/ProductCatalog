package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.ProductRepository;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.PRODUCT_NOT_FOUND;

public class ProductService implements CrudService<Product> {
    private static ProductService INSTANCE;
    private final ProductRepository productRepository;

    private ProductService() {
        productRepository = ProductRepository.getInstance();
    }

    public static ProductService getInstance() {
        if(INSTANCE == null) INSTANCE = new ProductService();
        return INSTANCE;
    }
    @Override
    public Product create(Product product) {
        return productRepository.getByItem(product.getItem()).orElseGet(() -> productRepository.save(product));
    }

    @Override
    public Product update(Product product) {
        return productRepository.getByItem(product.getItem()).map(value -> productRepository.save(product)).orElse(null);
    }

    @Override
    public Product remove(Product product) {
        return productRepository.getByItem(product.getItem()).map(productRepository::delete).orElse(null);
    }

    public Product findByItem(String item) {
        return Optional.ofNullable(item).map(value -> productRepository.getByItem(item)
                .orElseThrow(() -> new ApplicationException(PRODUCT_NOT_FOUND))).orElse(null);
    }

    @Override
    public Collection<Product> findAll() { return productRepository.getAll(); }
}
