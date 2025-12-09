package org.example.productcatalog.repository;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T> {
    T add(T t);
    T update(T t);

    T delete(T entity);

    Collection<T> getAll();

    Optional<T> getById(long id);

    Optional<T> getByKey(String key);
}