package org.example.productcatalog.repository;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CrudRepository<T> {
    T add(T t);
    T update(T t);

    T delete(T entity);

    Page<T> get(@Nullable Specification<T> spec, @Nullable Pageable pageable);

    Optional<T> getById(long id);

    Optional<T> getByKey(String key);

    default List<Order> orderBy(CriteriaBuilder cb, Root<T> dataRoot, Pageable pageable) {
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