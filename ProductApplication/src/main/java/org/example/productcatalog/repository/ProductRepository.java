package org.example.productcatalog.repository;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import org.example.productcatalog.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

@Repository
public class ProductRepository implements CrudRepository<Product> {
    private final EntityManager entityManager;

    @Inject
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
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    @Override
    public Product delete(Product product) {
        entityManager.remove(product);
        return product;
    }

    @Override
    public Page<Product> get(Specification<Product> spec, Pageable pageable) {
        Predicate predicate;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot));

        if (spec != null) {
            predicate = spec.toPredicate(countRoot, countQuery, cb);
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        countQuery = Optional.ofNullable(predicate).map(countQuery::where).orElse(countQuery);
        long total = entityManager.createQuery(countQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> dataRoot = dataQuery.from(Product.class);

        dataQuery = Optional.ofNullable(predicate).map(dataQuery::where).orElse(dataQuery);

        List<Order> orders = orderBy(cb, dataRoot, pageable);
        dataQuery = orders.isEmpty() ? dataQuery : dataQuery.orderBy(orders);

        List<Product> content = entityManager.createQuery(dataQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(content, pageable, total);
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

    public synchronized Optional<Product> getById(long id) {
        return Optional.of(entityManager.find(Product.class, id));
    }
}
