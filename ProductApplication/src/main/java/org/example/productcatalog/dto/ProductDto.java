package org.example.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements Serializable {
    @NotBlank(message = "Item cannot be blank")
    @JsonProperty("item")
    private String item;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("title")
    private String title;
    @JsonProperty("category")
    private String category;
    @Digits(message = "Item cannot be blank", integer = 10, fraction = 2)
    @JsonProperty("price")
    private Double price;
}
