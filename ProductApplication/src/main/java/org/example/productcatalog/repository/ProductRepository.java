package org.example.productcatalog.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.example.productcatalog.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository extends AbstractRepository<Product> {

    @Inject
    public ProductRepository(EntityManager entityManager) {
        super(entityManager, Product.class);
    }

    public Optional<Product> getByKey(String item) { return getByKey("item", item); }
}
