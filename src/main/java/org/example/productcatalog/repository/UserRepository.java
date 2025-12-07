package org.example.productcatalog.repository;

import jakarta.persistence.*;
import org.example.productcatalog.entity.User;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository implements CrudRepository<User> {
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User add(User user) {
        entityManager.persist(user);
        entityManager.flush();
        entityManager.refresh(user);
        return user;
    }

    @Override
    public User update(User user) {
        return getByKey(user.getEmail())
                .map(x -> {
                    x.setPassword(user.getPassword());
                    x.setRole(user.getRole());
                    return x;
                }).orElse(null);
    }

    @Override
    public User delete(User user) {
        entityManager.remove(user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return entityManager.createQuery("FROM User", User.class).getResultList();
    }

    @Override
    public Optional<User> getByKey(String email) {
        try {
            return Optional.of(entityManager.createQuery("FROM User WHERE email=?1", User.class)
                    .setParameter(1, email).getSingleResult());
        } catch(NoResultException noresult) {
            return Optional.empty();
        }
    }

    public synchronized Optional<User> getById(long id) {
        return Optional.of(entityManager.find(User.class, id));
    }
}
