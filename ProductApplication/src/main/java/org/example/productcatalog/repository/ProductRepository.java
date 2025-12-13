package org.example.productcatalog.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.productcatalog.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepository implements CrudRepository<Product> {
    private final EntityManager entityManager;

    @Autowired
    public ProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Product add(Product product) {
        entityManager.persist(product);
        entityManager.flush();
        entityManager.refresh(product);
        return product;
    }

    @Override
    public Product update(Product product) {
        return getByKey(product.getItem())
                .map(x -> {
                    x.setItem(product.getItem());
                    x.setBrand(product.getBrand());
                    x.setTitle(product.getTitle());
                    x.setCategory(product.getCategory());
                    x.setPrice(product.getPrice());
                    return x;
                }).orElse(null);
    }

    @Override
    public Product delete(Product product) {
        entityManager.remove(product);
        return product;
    }

    public Collection<Product> getAll() {
        return entityManager.createQuery("FROM Product", Product.class).getResultList();
    }

    @Override
    public Optional<Product> getByKey(String item) {
        try {
            return Optional.of(entityManager.createQuery("FROM Product WHERE item=?1", Product.class)
                    .setParameter(1, item).getSingleResult());
        } catch(NoResultException noresult) {
            return Optional.empty();
        }
    }

    public synchronized Optional<Product> getById(long id) {
        return Optional.of(entityManager.find(Product.class, id));
    }
}
