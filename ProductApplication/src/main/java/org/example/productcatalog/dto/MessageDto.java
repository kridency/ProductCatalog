package org.example.productcatalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Data transfer object with request respone.")
public class MessageDto {
    @JsonProperty("message")
    @Schema(description = "Response content body.")
    private String message;
    @JsonProperty("description")
    @Schema(description = "Response content description.")
    private String description;
}
