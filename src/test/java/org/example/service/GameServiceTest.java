package org.example.service;

import org.example.exception.GameException;
import org.example.model.entity.Game;
import org.example.model.entity.Player;
import org.example.model.enums.GameStatus;
import org.example.repository.GameRepository;
import org.example.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock private GameRepository gameRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private BoardService boardService;

    @InjectMocks private GameService gameService;

    private Game game;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        player1 = Player.builder().id(1L).name("P1").posX(0).posY(0).score(0).build();
        player2 = Player.builder().id(2L).name("P2").posX(0).posY(0).score(0).build();

        game = Game.builder()
                .id(10L)
                .status(GameStatus.IN_PROGRESS)
                .width(5).height(5)
                .boardJson("[[2,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,4]]")
                .currentPlayerId(1L)
                .players(new ArrayList<>(List.of(player1, player2)))
                .build();

        player1.setGame(game);
        player2.setGame(game);
    }

    @Test
    void makeMove_validMove_updatesPlayerPosition() {
        int[][] board = new int[5][5];
        board[0][0] = 2;

        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
        when(boardService.deserialize(any())).thenReturn(board);
        when(boardService.isWalkable(any(), eq(1), eq(0))).thenReturn(true);
        when(boardService.serialize(any())).thenReturn("[]");
        when(gameRepository.save(any())).thenReturn(game);

        gameService.makeMove(10L, 1L, 1, 0);

        assertThat(player1.getPosX()).isEqualTo(1);
        assertThat(player1.getPosY()).isEqualTo(0);
    }

    @Test
    void makeMove_wrongTurn_throwsGameException() {
        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> gameService.makeMove(10L, 2L, 1, 0))
                .isInstanceOf(GameException.class)
                .hasMessage("Not your turn");
    }

    @Test
    void makeMove_invalidCell_throwsGameException() {
        int[][] board = new int[5][5];
        board[1][0] = 1;

        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
        when(boardService.deserialize(any())).thenReturn(board);
        when(boardService.isWalkable(any(), eq(1), eq(0))).thenReturn(false);

        assertThatThrownBy(() -> gameService.makeMove(10L, 1L, 1, 0))
                .isInstanceOf(GameException.class)
                .hasMessage("Invalid move: cell blocked or out of bounds");
    }

    @Test
    void makeMove_reachExit_setsFinishedAndAddsScore() {
        int[][] board = new int[5][5];
        board[0][0] = 2;
        board[4][4] = 4;

        when(gameRepository.findById(10L)).thenReturn(Optional.of(game));
        when(boardService.deserialize(any())).thenReturn(board);
        when(boardService.isWalkable(any(), eq(4), eq(4))).thenReturn(true);
        when(boardService.serialize(any())).thenReturn("[]");
        when(gameRepository.save(any())).thenReturn(game);

        gameService.makeMove(10L, 1L, 4, 4);

        assertThat(game.getStatus()).isEqualTo(GameStatus.FINISHED);
        assertThat(player1.getScore()).isEqualTo(100);
    }

    @Test
    void getGame_nonExistentId_throwsGameException() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.getGame(99L))
                .isInstanceOf(GameException.class)
                .hasMessage("Game not found: 99");
    }
}
