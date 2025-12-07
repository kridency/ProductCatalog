package org.example.productcatalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {
    @NotBlank(message = "Item cannot be blank")
    private String item;
    private String brand;
    private String title;
    private String category;
    @NotBlank(message = "Item cannot be blank")
    private double price;
}
