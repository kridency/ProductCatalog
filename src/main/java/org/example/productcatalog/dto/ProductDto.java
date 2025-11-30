package org.example.productcatalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class ProductDto {
    @NotBlank(message = "Item cannot be blank")
    private String item;
    private String brand;
    private String title;
    private String category;
    @NotBlank(message = "Item cannot be blank")
    private double price;
}
