package org.example.productcatalog.repository;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.example.productcatalog.entity.User;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @Override
    public User delete(User user) {
        entityManager.remove(user);
        return user;
    }

    @Override
    public Page<User> get(Specification<User> spec, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot));

        if (spec != null) {
            Predicate countPredicate = spec.toPredicate(countRoot, countQuery, cb);
            if (countPredicate != null) {
                countQuery.where(countPredicate);
            }
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CriteriaQuery<User> dataQuery = cb.createQuery(User.class);
        Root<User> dataRoot = dataQuery.from(User.class);

        if (spec != null) {
            Predicate dataPredicate = spec.toPredicate(dataRoot, dataQuery, cb);
            if (dataPredicate != null) {
                dataQuery.where(dataPredicate);
            }
        }

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(dataRoot.get(order.getProperty()))
                            : cb.desc(dataRoot.get(order.getProperty())))
                    .collect(Collectors.toList());
            dataQuery.orderBy(orders);
        }

        List<User> content = entityManager.createQuery(dataQuery)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(content, pageable, total);
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
