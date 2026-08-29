package org.example.productcatalog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.productcatalog.util.converter.PasswordConverter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

@Getter
@Setter
@Entity
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "id_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "email", unique = true)
    private String email;
    @Convert(converter = PasswordConverter.class)
    private String password;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "role")
    private RoleType role;
}
