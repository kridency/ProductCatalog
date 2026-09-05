package org.example.productcatalog.repository;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractRepository<T> {
    protected EntityManager entityManager;

    public T add(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        return entity;
    }

    public T update(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    public T delete(T entity) {
        entityManager.remove(entity);
        return entity;
    }

    abstract public Page<T> get(@Nullable Specification<T> spec, @Nullable Pageable pageable);

    abstract public Optional<T> getById(long id);

    abstract public Optional<T> getByKey(String key);

    protected Page<T> findAll(@Nullable Specification<T> spec, @Nullable Pageable pageable, Class<T> dtoClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> query = cb.createQuery(dtoClass);

        Root<T> root = query.from(dtoClass);

        Optional.ofNullable(spec).map(x -> x.toPredicate(root, query, cb)).ifPresent(query::where);

        List<Order> orders = Optional.ofNullable(pageable).map(x -> orderBy(cb, root, x)).orElse(List.of());

        TypedQuery<T> typedQuery = entityManager.createQuery(query.orderBy(orders));

        typedQuery = Optional.ofNullable(pageable).map(Pageable::getOffset).map(Number::intValue)
                .map(typedQuery::setFirstResult)
                .orElse(typedQuery);

        typedQuery = Optional.ofNullable(pageable).map(Pageable::getPageSize).map(Number::intValue)
                .map(typedQuery::setMaxResults)
                .orElse(typedQuery);

        pageable = Optional.ofNullable(pageable).orElseGet(Pageable::unpaged);

        List<T> content = Optional.of(typedQuery.getResultList()).orElseGet(Collections::emptyList);

        return new PageImpl<>(content, pageable, content.size());
    }

    protected List<Order> orderBy(CriteriaBuilder cb, Root<T> dataRoot, Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(dataRoot.get(order.getProperty()))
                            : cb.desc(dataRoot.get(order.getProperty())))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}