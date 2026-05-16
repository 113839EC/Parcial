package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.enums.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public int[][] emptyBoard(int width, int height) {
        return new int[height][width];
    }

    public int[][] deserialize(String json) {
        try {
            return mapper.readValue(json, int[][].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Board parse error", e);
        }
    }

    public String serialize(int[][] board) {
        try {
            return mapper.writeValueAsString(board);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Board serialize error", e);
        }
    }

    public boolean isInBounds(int[][] board, int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length;
    }

    public boolean isWalkable(int[][] board, int x, int y) {
        return isInBounds(board, x, y) && board[x][y] != CellType.WALL.value;
    }

    // BFS: shortest path from start to end — returns path as [x,y] list, empty if no path
    public List<int[]> shortestPath(int[][] board, int[] start, int[] end) {
        int rows = board.length, cols = board[0].length;
        int[][] parent = new int[rows][cols];
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(start);
        visited[start[0]][start[1]] = true;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            if (curr[0] == end[0] && curr[1] == end[1]) {
                List<int[]> path = reconstructPath(parent, start, end, cols);
                logger.debug("BFS found path of length {} from ({},{}) to ({},{})",
                        path.size(), start[0], start[1], end[0], end[1]);
                return path;
            }
            for (int d = 0; d < 4; d++) {
                int nx = curr[0] + dx[d], ny = curr[1] + dy[d];
                if (isWalkable(board, nx, ny) && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    parent[nx][ny] = curr[0] * cols + curr[1];
                    queue.add(new int[]{nx, ny});
                }
            }
        }
        logger.debug("BFS: no path from ({},{}) to ({},{})", start[0], start[1], end[0], end[1]);
        return Collections.emptyList();
    }

    private List<int[]> reconstructPath(int[][] parent, int[] start, int[] end, int cols) {
        LinkedList<int[]> path = new LinkedList<>();
        int x = end[0], y = end[1];
        while (x != start[0] || y != start[1]) {
            path.addFirst(new int[]{x, y});
            int p = parent[x][y];
            x = p / cols;
            y = p % cols;
        }
        path.addFirst(start);
        return path;
    }

    // Fisher-Yates shuffle — O(n), uniform distribution
    public int[] shuffle(int[] deck) {
        Random rand = new Random();
        for (int i = deck.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = deck[i]; deck[i] = deck[j]; deck[j] = tmp;
        }
        return deck;
    }
}
