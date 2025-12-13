package org.example.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonIgnoreProperties(value = {"authorities", "username"})
@Schema(description = "Data transfer object with user details.")
public class UserDto implements Serializable, UserDetails {
    @Email(message = EMAIL_ERROR)
    @NotEmpty(message = EMAIL_NOT_SPECIFIED)
    @JsonProperty("email")
    @Schema(description = "User email.")
    private String email;
    @NotEmpty(message = "Password cannot be blank")
    @JsonProperty("password")
    @Schema(description = "User password.")
    private String password;
    @JsonProperty("role")
    @Schema(description = "User role.")
    private RoleType role;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role == null ? RoleType.ROLE_USER.name() : role.name()));
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
