package org.example.productcatalog.service;

import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.CrudRepository;
import org.example.productcatalog.repository.UserRepository;
import org.example.productcatalog.util.specification.Specification;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserService implements CrudService<User, String> {
    private final CrudRepository<User> repository;

    public UserService() {
        repository = new UserRepository();
    }

    @Override
    public User create(User user) {
        return Optional.ofNullable(user).map(x -> repository.getByKey(x.getEmail())
                .orElse(repository.add(user))).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public User update(User user) {
        return Optional.ofNullable(user).flatMap(x -> repository.getByKey(x.getEmail()).map(value -> repository.update(x)))
                .orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public User remove(User user) {
        return Optional.ofNullable(user).flatMap(x -> repository.getByKey(x.getEmail()).map(repository::delete))
                .orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public Collection<User> findAll() { return repository.getAll(); }

    @Override
    public User find(String email) {
        return Optional.ofNullable(email).flatMap(repository::getByKey).orElseThrow(() ->
                new ApplicationException(USER_NOT_FOUND));
    }

    @Override
    public User findById(long id) {
        return repository.getById(id).orElse(null);
    }

    @Override
    public Collection<User> findFiltered(User user) {
        return new Specification<>(user).apply(repository.getAll());
    }
}
