package org.example.service;

public interface CrudService<T> {
    T create(T entity);
    T update(T entity);
    T remove(T entity);
}
