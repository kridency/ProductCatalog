package org.example.productcatalog.repository;

import java.util.Collection;

public interface CrudRepository<T> {
    T save(T entity);

    T delete(T entity);

    Collection<T> getAll();
}