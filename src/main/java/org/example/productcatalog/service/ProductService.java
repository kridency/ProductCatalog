package org.example.productcatalog.service;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.repository.ProductRepository;
import org.example.productcatalog.util.specification.Specification;

import java.util.Collection;
import java.util.Map;

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

    public Collection<Product> findFiltered(Map<String, ? extends Comparable<?>> criteria) {
        return new Specification<Product>(criteria).apply(productRepository.getAll());
    }

    @Override
    public Collection<Product> findAll() { return productRepository.getAll(); }
}
