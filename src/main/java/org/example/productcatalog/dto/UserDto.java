package org.example.productcatalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static org.example.productcatalog.preset.ProductCatalogInit.EMAIL_ERROR;

@Data
public class UserDto {
    @Email(message = EMAIL_ERROR)
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
