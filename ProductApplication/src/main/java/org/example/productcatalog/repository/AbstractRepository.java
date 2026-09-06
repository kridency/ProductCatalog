package org.example.productcatalog.repository;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractRepository<T> {
    protected EntityManager entityManager;
    protected Class<T> entityClass;

    public AbstractRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public T add(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.refresh(entity);
        return entity;
    }

    public T update(T entity) {
        entityManager.merge(entity);
        entityManager.flush();
        return entity;
    }

    public T delete(T entity) {
        entityManager.remove(entity);
        return entity;
    }

    protected Optional<T> getByKey(String key, String value) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> query = cb.createQuery(entityClass);

        Root<T> root = query.from(entityClass);
        query.select(root).where(cb.equal(root.get(key), value));

        try {
            return Optional.of(entityManager.createQuery(query).getSingleResult());
        } catch(NoResultException __) {
            return Optional.empty();
        }
    }

    public synchronized Optional<T> getById(Long id) {
        return Optional.of(entityManager.find(entityClass, id));
    }

    public Page<T> findAll(@Nullable Specification<T> spec, @Nullable Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        Pageable actualPageable = pageable != null ? pageable : Pageable.unpaged();

        final CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        Optional.ofNullable(spec).map(x -> x.toPredicate(root, query, cb)).ifPresent(query::where);

        List<Order> orders = Optional.of(actualPageable).filter(Pageable::isPaged)
                .map(x -> orderBy(cb, root, x)).orElse(List.of());

        TypedQuery<T> typedQuery = entityManager.createQuery(query.orderBy(orders));
        typedQuery = Optional.of(actualPageable).filter(Pageable::isPaged)
                .map(Pageable::getOffset).map(Number::intValue).map(typedQuery::setFirstResult)
                .orElse(typedQuery);
        typedQuery = Optional.of(actualPageable).filter(Pageable::isPaged)
                .map(Pageable::getPageSize).map(Number::intValue).map(typedQuery::setMaxResults)
                .orElse(typedQuery);

        List<T> content = typedQuery.getResultList();

        return new PageImpl<>(content, actualPageable, actualPageable.isUnpaged() ? content.size() : count(spec));
    }

    private long count(@Nullable Specification<T> spec) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));

        Optional.ofNullable(spec).map(x -> x.toPredicate(countRoot, countQuery, cb))
                .ifPresent(countQuery::where);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Order> orderBy(CriteriaBuilder cb, Root<T> dataRoot, Pageable pageable) {
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