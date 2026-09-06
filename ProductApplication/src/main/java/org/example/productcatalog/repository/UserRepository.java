package org.example.productcatalog.repository;

import jakarta.inject.Inject;
import jakarta.persistence.*;
import org.example.productcatalog.entity.User;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends AbstractRepository<User> {

    @Inject
    public UserRepository(EntityManager entityManager) {
        super(entityManager, User.class);
    }

    public Optional<User> getByKey(String email) { return getByKey("email", email); }
}
