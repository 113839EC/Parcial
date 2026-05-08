package org.example.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.model.enums.GameStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    private int width;
    private int height;

    // Board stored as 2D array serialized to JSON: board[row][col]
    @Column(length = 10000)
    private String boardJson;

    private Long currentPlayerId;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Player> players = new ArrayList<>();
}
