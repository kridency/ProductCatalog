package org.example.productcatalog.repository;

import org.example.productcatalog.entity.Invocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvocationRepository extends JpaRepository<Invocation, Long> {
}
