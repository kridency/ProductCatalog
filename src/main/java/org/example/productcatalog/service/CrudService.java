package org.example.productcatalog.service;

import java.util.Collection;

public interface CrudService<T, U> {
    T create(T entity);
    T update(T entity);
    T remove(T entity);
    Collection<T> findAll();
    Collection<T> findFiltered(T entity);
    T find(U unique);
}
