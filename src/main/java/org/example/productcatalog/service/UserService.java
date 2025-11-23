package org.example.productcatalog.service;

import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public class UserService implements CrudService<User, String> {
    private static final UserService INSTANCE = new UserService();
    private final UserRepository userRepository;

    private UserService() {
        userRepository = UserRepository.getInstance();
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    @Override
    public User create(User user) {
        return Optional.ofNullable(user).map(x -> userRepository.getByEmail(x.getEmail())
                .orElse(userRepository.add(user))).orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public User update(User user) {
        return Optional.ofNullable(user).map(x -> userRepository.getByEmail(x.getEmail()).map(value -> userRepository.update(x))
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)))
                .orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public User remove(User user) {
        return Optional.ofNullable(user).map(x -> userRepository.getByEmail(x.getEmail()).map(userRepository::delete)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)))
                .orElseThrow(() -> new ApplicationException(USER_NOT_SPECIFIED));
    }

    @Override
    public Collection<User> findAll() { return userRepository.getAll(); }

    public User findByEmail(String email) {
        return Optional.ofNullable(email).map(value -> userRepository.getByEmail(email)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)))
                .orElseThrow(() -> new ApplicationException(EMAIL_ERROR));
    }

    @Override
    public User find(String email) {
        return Optional.ofNullable(email).map(value -> userRepository.getByEmail(email)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND)))
                .orElseThrow(() -> new ApplicationException(EMAIL_ERROR));
    }

    @Override
    public Collection<User> findFiltered(User user) { return null; }
}
