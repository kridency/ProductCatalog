package org.example.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object with product details.")
public class ProductDto implements Serializable {
    @NotBlank(message = "Item cannot be blank")
    @JsonProperty("item")
    @Schema(description = "Product item number.")
    private String item;
    @JsonProperty("brand")
    @Schema(description = "Product brand.")
    private String brand;
    @JsonProperty("title")
    @Schema(description = "Product name.")
    private String title;
    @JsonProperty("category")
    @Schema(description = "Product category.")
    private String category;
    @Digits(message = "Item cannot be blank", integer = 10, fraction = 2)
    @JsonProperty("price")
    @Schema(description = "Product Price.")
    private Double price;
}
