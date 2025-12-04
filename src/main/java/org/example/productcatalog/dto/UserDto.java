package org.example.productcatalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.example.productcatalog.entity.RoleType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_ERROR;
import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_NOT_SPECIFIED;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable, UserDetails {
    @Email(message = EMAIL_ERROR)
    @NotEmpty(message = EMAIL_NOT_SPECIFIED)
    private String email;
    @NotEmpty(message = "Password cannot be blank")
    private String password;
    private RoleType role;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
