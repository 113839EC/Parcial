package org.example.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGameRequest {
    @Min(2) private int width;
    @Min(2) private int height;
    @NotBlank private String playerName;
}
