package org.example.productcatalog.entity;

import jakarta.persistence.Table;

@Table(name="role_type")
public enum RoleType {
    ROLE_USER, ROLE_ADMIN
}
