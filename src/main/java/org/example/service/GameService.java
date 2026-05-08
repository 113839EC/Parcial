package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.GameException;
import org.example.model.dto.GameResponse;
import org.example.model.entity.Game;
import org.example.model.entity.Player;
import org.example.model.enums.CellType;
import org.example.model.enums.GameStatus;
import org.example.repository.GameRepository;
import org.example.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final BoardService boardService;

    @Transactional
    public GameResponse createGame(int width, int height, String playerName) {
        logger.info("Creating game {}x{} for player '{}'", width, height, playerName);

        int[][] board = boardService.emptyBoard(width, height);
        board[0][0] = CellType.PLAYER.value;
        board[height - 1][width - 1] = CellType.EXIT.value;

        Game game = Game.builder()
                .status(GameStatus.WAITING)
                .width(width)
                .height(height)
                .boardJson(boardService.serialize(board))
                .build();

        Player player = Player.builder()
                .name(playerName)
                .posX(0).posY(0)
                .score(0)
                .game(game)
                .build();

        game.getPlayers().add(player);
        game.setCurrentPlayerId(null);
        game = gameRepository.save(game);
        game.setCurrentPlayerId(game.getPlayers().get(0).getId());
        game = gameRepository.save(game);

        logger.info("Game {} created, waiting for players", game.getId());
        return toResponse(game);
    }

    @Transactional
    public GameResponse joinGame(Long gameId, String playerName) {
        logger.info("Player '{}' joining game {}", playerName, gameId);
        Game game = findGame(gameId);

        if (game.getStatus() != GameStatus.WAITING)
            throw new GameException("Game already started");

        Player player = Player.builder()
                .name(playerName)
                .posX(0).posY(0)
                .score(0)
                .game(game)
                .build();

        game.getPlayers().add(player);
        game.setStatus(GameStatus.IN_PROGRESS);
        game = gameRepository.save(game);

        logger.info("Game {} started with {} players", gameId, game.getPlayers().size());
        return toResponse(game);
    }

    @Transactional
    public GameResponse makeMove(Long gameId, Long playerId, int x, int y) {
        logger.info("Player {} moving to ({},{}) in game {}", playerId, x, y, gameId);
        Game game = findGame(gameId);

        if (game.getStatus() != GameStatus.IN_PROGRESS)
            throw new GameException("Game not in progress");
        if (!playerId.equals(game.getCurrentPlayerId()))
            throw new GameException("Not your turn");

        Player player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameException("Player not in game"));

        int[][] board = boardService.deserialize(game.getBoardJson());

        if (!boardService.isWalkable(board, x, y))
            throw new GameException("Invalid move: cell blocked or out of bounds");

        board[player.getPosX()][player.getPosY()] = CellType.EMPTY.value;
        player.setPosX(x);
        player.setPosY(y);

        if (board[x][y] == CellType.EXIT.value) {
            player.setScore(player.getScore() + 100);
            game.setStatus(GameStatus.FINISHED);
            logger.info("Player {} reached exit in game {}. Score: {}", playerId, gameId, player.getScore());
        } else if (board[x][y] == CellType.ITEM.value) {
            player.setScore(player.getScore() + 10);
            logger.debug("Player {} picked up item at ({},{})", playerId, x, y);
        }

        board[x][y] = CellType.PLAYER.value;
        game.setBoardJson(boardService.serialize(board));

        List<Player> players = game.getPlayers();
        int idx = players.indexOf(player);
        game.setCurrentPlayerId(players.get((idx + 1) % players.size()).getId());

        playerRepository.save(player);
        return toResponse(gameRepository.save(game));
    }

    @Transactional(readOnly = true)
    public GameResponse getGame(Long gameId) {
        return toResponse(findGame(gameId));
    }

    @Transactional(readOnly = true)
    public List<GameResponse> getOpenGames() {
        return gameRepository.findByStatus(GameStatus.WAITING)
                .stream().map(this::toResponse).toList();
    }

    private Game findGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Game {} not found", id);
                    return new GameException("Game not found: " + id);
                });
    }

    private GameResponse toResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .status(game.getStatus())
                .width(game.getWidth())
                .height(game.getHeight())
                .board(boardService.deserialize(game.getBoardJson()))
                .currentPlayerId(game.getCurrentPlayerId())
                .players(game.getPlayers().stream()
                        .map(p -> GameResponse.PlayerResponse.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .posX(p.getPosX())
                                .posY(p.getPosY())
                                .score(p.getScore())
                                .build())
                        .toList())
                .build();
    }
}
