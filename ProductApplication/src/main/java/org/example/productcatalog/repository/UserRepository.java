package org.example.productcatalog.repository;

import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.example.productcatalog.entity.User;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends AbstractRepository<User> {

    @Inject
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<User> get(Specification<User> spec, Pageable pageable) {
        return findAll(spec, pageable, User.class);
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

    @Override
    public synchronized Optional<User> getById(long id) {
        return Optional.of(entityManager.find(User.class, id));
    }
}
