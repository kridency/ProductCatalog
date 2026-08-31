package org.example.productcatalog.repository;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface CrudRepository<T> {
    T add(T t);
    T update(T t);

    T delete(T entity);

    Page<T> get(@Nullable Specification<T> spec, @Nullable Pageable pageable);

    Optional<T> getById(long id);

    Optional<T> getByKey(String key);
}