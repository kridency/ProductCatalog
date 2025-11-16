package org.example.productcatalog.repository;

import org.example.productcatalog.entity.Product;
import java.util.*;

public class ProductRepository implements CrudRepository<Product> {
    private final Map<Long, Product> products;
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
        return products.merge(product.getId(), product, (a, b) -> b);
    }

    @Override
    public Product delete(Product product) {
        return products.remove(product.getId());
    }

    public Collection<Product> getAll() { return products.values(); }

    public Optional<Product> getByItem(String item) {
        return products.values().stream().filter(product -> product.getItem().equals(item)).findFirst();
    }
}
