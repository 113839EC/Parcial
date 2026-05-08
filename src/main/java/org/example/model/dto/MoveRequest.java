package org.example.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveRequest {
    @NotNull private Long playerId;
    @NotNull private Integer x;
    @NotNull private Integer y;
}
