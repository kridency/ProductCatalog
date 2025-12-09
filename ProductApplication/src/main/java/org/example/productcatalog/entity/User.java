package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.productcatalog.util.converter.PasswordConverter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Setter
@Getter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequence_generator")
    @SequenceGenerator(name = "sequence_generator", sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    private String email;
    @Convert(converter = PasswordConverter.class)
    private String password;
    @Column(name = "role", columnDefinition = "role_type", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RoleType role;
}
