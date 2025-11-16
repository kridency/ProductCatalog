package org.example.productcatalog.repository;

public interface CrudRepository<T> {
    T save(T entity);

    T delete(T entity);
}