package org.example.productcatalog.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.productcatalog.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

@Repository
public class ProductRepository extends AbstractRepository<Product> {

    @Inject
    public ProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Product> get(Specification<Product> spec, Pageable pageable) {
        return findAll(spec, pageable, Product.class);
    }

    @Override
    public Optional<Product> getByKey(String item) {
        try {
            return Optional.of(entityManager.createQuery("FROM Product WHERE item=?1", Product.class)
                    .setParameter(1, item).getSingleResult());
        } catch(NoResultException __) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<Product> getById(long id) {
        return Optional.of(entityManager.find(Product.class, id));
    }
}
