package org.example.model.dto;

import lombok.Builder;
import lombok.Data;
import org.example.model.enums.GameStatus;

import java.util.List;

@Data @Builder
public class GameResponse {
    private Long id;
    private GameStatus status;
    private int width;
    private int height;
    private int[][] board;
    private Long currentPlayerId;
    private List<PlayerResponse> players;

    @Data @Builder
    public static class PlayerResponse {
        private Long id;
        private String name;
        private int posX;
        private int posY;
        private int score;
    }
}
