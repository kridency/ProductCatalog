package org.example.productcatalog.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Collection;
import java.util.Map;

public interface CrudService<T, U> {
    T create(T entity);
    T update(T entity);
    T remove(T entity);
    Collection<T> findAll();
    Slice<T> findFiltered(Map<String, ? extends Comparable<?>> criteria, Pageable pageable);
    T findById(long id);
    T find(U unique);
}
