package org.example.productcatalog.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private long id;
    private String email;
    private String password;
    private RoleType role;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = RoleType.ROLE_USER;
    }

}
