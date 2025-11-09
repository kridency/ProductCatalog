package org.example.productcatalog.service;

import org.example.productcatalog.entity.User;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.example.productcatalog.preset.ProductCatalogInit.USER_NOT_FOUND;

public class UserService implements CrudService<User> {
    private static UserService INSTANCE;
    private final UserRepository userRepository;

    private UserService() {
        userRepository = UserRepository.getInstance();
    }

    public static UserService getInstance() {
        if(INSTANCE == null) INSTANCE = new UserService();
        return INSTANCE;
    }

    @Override
    public User create(User user) {
        return userRepository.getByEmail(user.getEmail()).orElseGet(() -> userRepository.save(user));
    }

    @Override
    public User update(User user) {
        return userRepository.getByEmail(user.getEmail()).map(value -> userRepository.save(user)).orElse(null);
    }

    @Override
    public User remove(User user) {
        return userRepository.getByEmail(user.getEmail()).map(userRepository::delete).orElse(null);
    }

    @Override
    public Collection<User> findAll() { return userRepository.getAll(); }

    public User findByEmail(String email) {
        return Optional.ofNullable(email).map(value -> userRepository.getByEmail(email)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND))).orElse(null);
    }

    public User findById(UUID id) {
        return Optional.ofNullable(id).map(value -> userRepository.getById(id)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND))).orElse(null);
    }
}
