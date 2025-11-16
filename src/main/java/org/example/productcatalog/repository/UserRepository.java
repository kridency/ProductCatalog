package org.example.productcatalog.repository;

import org.example.productcatalog.entity.User;

import java.util.*;

public class UserRepository implements CrudRepository<User> {
    private final Map<Long, User> users;
    private static UserRepository INSTANCE;

    private UserRepository() {
        users = new HashMap<>();
    }

    public static UserRepository getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new UserRepository();
        }
        return INSTANCE;
    }

    @Override
    public User save(User user) {
        return users.merge(user.getId(), user, (a, b) -> b);
    }

    @Override
    public User delete(User user) {
        return users.remove(user.getId());
    }

    public Collection<User> getAll() { return users.values(); }

    public Optional<User> getByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }
}
