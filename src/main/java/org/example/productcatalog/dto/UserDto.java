package org.example.productcatalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;

import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_ERROR;
import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_NOT_SPECIFIED;

@Getter
@Setter
public class UserDto implements Serializable {
    @Email(message = EMAIL_ERROR)
    @NotEmpty(message = EMAIL_NOT_SPECIFIED)
    private String email;
    @NotEmpty(message = "Password cannot be blank")
    private String password;
}
