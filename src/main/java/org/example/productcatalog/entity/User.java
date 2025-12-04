package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.productcatalog.util.converter.PasswordConverter;

@Setter
@Getter
@Entity
@Table(schema = "custom", name = "user")
public class User {
    @Id
    private long id;
    private String email;
    @Convert(converter = PasswordConverter.class)
    private String password;
    private RoleType role;
}
