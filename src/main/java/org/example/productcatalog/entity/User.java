package org.example.productcatalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@Entity
public class User {
    @Id
    private long id;
    private String email;
    private String password;
    private RoleType role;
}
