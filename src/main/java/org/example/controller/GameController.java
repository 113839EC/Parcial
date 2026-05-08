package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.CreateGameRequest;
import org.example.model.dto.GameResponse;
import org.example.model.dto.MoveRequest;
import org.example.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> create(@Valid @RequestBody CreateGameRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gameService.createGame(req.getWidth(), req.getHeight(), req.getPlayerName()));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GameResponse> join(@PathVariable Long id, @RequestParam String playerName) {
        return ResponseEntity.ok(gameService.joinGame(id, playerName));
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameResponse> move(@PathVariable Long id, @Valid @RequestBody MoveRequest req) {
        return ResponseEntity.ok(gameService.makeMove(id, req.getPlayerId(), req.getX(), req.getY()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @GetMapping("/open")
    public ResponseEntity<List<GameResponse>> open() {
        return ResponseEntity.ok(gameService.getOpenGames());
    }
}
