package org.example.productcatalog.repository;

import org.example.productcatalog.entity.Product;
import org.example.productcatalog.entity.User;

import java.util.*;

public class ProductRepository implements CrudRepository<Product> {
    private final Map<UUID, Product> products;
    private static ProductRepository INSTANCE;

    private ProductRepository() {
        products = new HashMap<>();
    }

    public static ProductRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ProductRepository();
        }
        return INSTANCE;
    }

    @Override
    public Product save(Product product) {
        return products.merge(product.getId(), product, (a, b) -> {
            a.setItem(b.getItem());
            a.setBrand(b.getBrand());
            a.setTitle(b.getTitle());
            a.setCategory(b.getCategory());
            a.setPrice(b.getPrice());
            return a;
        });
    }

    @Override
    public Product delete(Product product) {
        return products.remove(product.getId());
    }

    public Optional<Product> getByItem(String item) {
        return products.values().stream().filter(product -> product.getItem().equals(item)).findFirst();
    }

    public Collection<Product> getAll() { return products.values(); }
}
