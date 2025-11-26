package org.example.productcatalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_ERROR;
import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_NOT_SPECIFIED;

@Getter
@Setter
public class UserDto {
    @Email(message = EMAIL_ERROR)
    @NotBlank(message = EMAIL_NOT_SPECIFIED)
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
