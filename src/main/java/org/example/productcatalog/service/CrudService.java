package org.example.productcatalog.service;

import java.util.Collection;

public interface CrudService<T> {
    T create(T entity);
    T update(T entity);
    T remove(T entity);
    Collection<T> findAll();
}
