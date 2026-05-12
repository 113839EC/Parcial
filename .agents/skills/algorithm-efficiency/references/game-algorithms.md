# Game Algorithms — Deep Reference

## Fisher-Yates Shuffle (Card/Deck Shuffle)

**Use case**: Shuffling decks, randomizing lists uniformly.

**Complexity**: O(n) time, O(1) space

**Why not sort-with-random?**: `Collections.sort(() -> rng.nextInt())` is O(n log n) AND
produces biased distributions — some orderings appear more than others.

```java
// ✅ Fisher-Yates — O(n), uniform distribution
Random rng = new Random();
for (int i = deck.size() - 1; i > 0; i--) {
    int j = rng.nextInt(i + 1);
    Collections.swap(deck, i, j);
}

// ✅ Java built-in (uses Fisher-Yates internally)
Collections.shuffle(deck);          // O(n)
Collections.shuffle(deck, rng);     // O(n), seeded RNG
```

**Key invariant**: Each element ends up in any position with equal probability 1/n.

---

## Deck/Hand Validation

**Use case**: Validating card game rules (max copies, required types, exact count).

| Rule | Structure | Complexity |
|------|-----------|------------|
| Max 4 copies of same card | `HashMap<CardId, Integer>` | O(n) |
| Exactly 60 cards | Counter in single pass | O(n) |
| At least 1 Basic Pokemon | Flag in single pass | O(n) |
| No duplicates in unique set | `HashSet` | O(n) |
| Find card by ID | `HashMap<String, Card>` | O(1) |

```java
// Validate all rules in ONE pass — O(n)
Map<String, Integer> freq = new HashMap<>();
boolean hasBasic = false;
int total = 0;

for (Card card : deck) {
    freq.merge(card.getId(), 1, Integer::sum);
    if (freq.get(card.getId()) > 4) throw new InvalidDeckException("Max 4 copies: " + card.getName());
    if (card.isBasicPokemon()) hasBasic = true;
    total++;
}

if (total != 60) throw new InvalidDeckException("Deck must have 60 cards");
if (!hasBasic) throw new InvalidDeckException("Need at least 1 Basic Pokemon");
```

**Anti-pattern**: Looping deck once per rule = O(k·n) where k = number of rules. Always combine into single pass.

---

## Turn Order / Initiative (Priority Queue)

**Use case**: Games where turn order depends on speed, initiative, or dynamic priority.

**Complexity**: O(log n) insert/poll, O(1) peek

```java
// PriorityQueue — highest speed goes first (max-heap via reversed comparator)
PriorityQueue<Player> turnOrder = new PriorityQueue<>(
    Comparator.comparingInt(Player::getSpeed).reversed()
);

turnOrder.addAll(players);      // O(n log n) initial build
Player next = turnOrder.poll(); // O(log n) — always fastest player

// Re-insert after turn with modified priority
next.applyTurnEffects();
turnOrder.offer(next);          // O(log n)
```

**When to use sorted list instead**: Fixed order that never changes mid-game → sort once O(n log n), then iterate O(1) per turn.

---

## Pathfinding in Grids / Mazes

### BFS — Shortest Path (unweighted)

**Use case**: Minimum steps in a grid, maze solving, flood fill.

**Complexity**: O(V + E) where V = cells, E = connections (4 or 8 per cell)

```java
// BFS shortest path — O(V + E)
int bfs(int[][] grid, int[] start, int[] end) {
    int rows = grid.length, cols = grid[0].length;
    boolean[][] visited = new boolean[rows][cols];
    Queue<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[]{start[0], start[1], 0}); // {row, col, steps}
    visited[start[0]][start[1]] = true;

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        if (curr[0] == end[0] && curr[1] == end[1]) return curr[2];
        for (int[] d : dirs) {
            int nr = curr[0] + d[0], nc = curr[1] + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && !visited[nr][nc] && grid[nr][nc] != 1) {
                visited[nr][nc] = true;
                queue.offer(new int[]{nr, nc, curr[2] + 1});
            }
        }
    }
    return -1; // no path
}
```

### Dijkstra — Shortest Path (weighted)

**Use case**: Movement costs differ per cell (terrain, slow tiles).

**Complexity**: O((V + E) log V)

```java
// Dijkstra with PriorityQueue — O((V+E) log V)
int[][] dijkstra(int[][] cost, int[] start) {
    int rows = cost.length, cols = cost[0].length;
    int[][] dist = new int[rows][cols];
    for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
    dist[start[0]][start[1]] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
    pq.offer(new int[]{start[0], start[1], 0});

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        if (curr[2] > dist[curr[0]][curr[1]]) continue; // stale entry
        for (int[] d : dirs) {
            int nr = curr[0] + d[0], nc = curr[1] + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                int newDist = dist[curr[0]][curr[1]] + cost[nr][nc];
                if (newDist < dist[nr][nc]) {
                    dist[nr][nc] = newDist;
                    pq.offer(new int[]{nr, nc, newDist});
                }
            }
        }
    }
    return dist;
}
```

### Comparison

| Algorithm | Weighted? | Complexity | Use when |
|-----------|-----------|------------|----------|
| BFS | No | O(V + E) | All moves cost the same |
| Dijkstra | Yes | O((V+E) log V) | Different terrain costs |
| A* | Yes | O((V+E) log V) | Need fastest result with heuristic |

---

## Matching / Triada (Group Detection)

**Use case**: Finding trios, sequences, or sets in a hand of cards.

### Find trios (same value × 3)

```java
// O(n) — single pass with frequency map
Map<String, Integer> freq = new HashMap<>();
for (Card c : hand) freq.merge(c.getValue(), 1, Integer::sum);

List<String> trios = freq.entrySet().stream()
    .filter(e -> e.getValue() >= 3)
    .map(Map.Entry::getKey)
    .collect(Collectors.toList());
// Time: O(n), Space: O(n)
```

### Find consecutive sequences (e.g., 3-4-5)

```java
// Sort then sliding window — O(n log n)
List<Integer> values = hand.stream().map(Card::getNumericValue).sorted().collect(Collectors.toList());

for (int i = 0; i <= values.size() - 3; i++) {
    if (values.get(i+1) == values.get(i) + 1 &&
        values.get(i+2) == values.get(i) + 2) {
        // sequence found: values[i], values[i+1], values[i+2]
    }
}
// Time: O(n log n) for sort, O(n) for scan
```

### Detect winning hand (all cards form valid groups)

```java
// Recursive backtracking — O(n!) worst case, but hand size bounded (~13-14 cards)
// For small fixed hand sizes this is acceptable
boolean isWinningHand(List<Card> hand) {
    if (hand.isEmpty()) return true;
    // try to form a trio or sequence starting from hand.get(0)
    // remove it and recurse — depth bounded by hand size
}
```

**Rule**: For fixed small hand sizes (≤ 14), even O(n!) is fast. For dynamic sizes, use DP.

---

## Batalla Naval (Battleship)

| Operation | Structure | Complexity |
|-----------|-----------|------------|
| Register shot | `HashSet<String>` coords | O(1) |
| Check already shot | `HashSet.contains()` | O(1) |
| Place ship | Mark cells in `HashMap` | O(k) k=ship size |
| Check ship sunk | All cells in `HashSet` hit | O(k) |
| AI — random untried cell | Shuffle remaining coords, poll | O(1) per shot |
| AI — smart hunt (BFS from hit) | BFS on adjacent cells | O(V) |

```java
// Track shots — O(1) per operation
Set<String> shotsFired = new HashSet<>();

boolean shoot(int row, int col) {
    String coord = row + "," + col;
    if (shotsFired.contains(coord)) throw new AlreadyShotException();
    shotsFired.add(coord);
    return board[row][col] == SHIP; // hit or miss
}

// AI — hunt mode BFS after a hit
Queue<int[]> huntQueue = new ArrayDeque<>();
huntQueue.offer(hitCoord);
while (!huntQueue.isEmpty()) {
    int[] curr = huntQueue.poll();
    if (shoot(curr[0], curr[1])) { // hit
        for (int[] adj : adjacentCells(curr)) {
            if (!shotsFired.contains(adj[0] + "," + adj[1]))
                huntQueue.offer(adj);
        }
    }
}
// Time: O(V) worst case, V = board size
```

---

## Maze Generation

| Algorithm | Complexity | Result |
|-----------|------------|--------|
| DFS (recursive backtracker) | O(V) | Long winding corridors |
| Prim's randomized | O(V log V) | More branching, open feel |
| Kruskal's randomized | O(V log V) | Uniform, many dead ends |

```java
// DFS maze generation — O(V)
void generateMaze(int[][] maze, int row, int col, boolean[][] visited) {
    visited[row][col] = true;
    int[][] dirs = {{0,2},{0,-2},{2,0},{-2,0}};
    shuffleArray(dirs); // randomize direction order
    for (int[] d : dirs) {
        int nr = row + d[0], nc = col + d[1];
        if (inBounds(nr, nc, maze) && !visited[nr][nc]) {
            maze[row + d[0]/2][col + d[1]/2] = PATH; // carve wall between
            generateMaze(maze, nr, nc, visited);
        }
    }
}
// Depth bounded by grid size — recursion safe for typical maze sizes
```

---

## Game Loop Performance Rules

```
State lookup          → HashMap/HashSet     → O(1)
Turn priority         → PriorityQueue       → O(log n)
Pathfinding           → BFS / Dijkstra      → O(V + E)
Shuffle               → Fisher-Yates        → O(n)
Deck/hand validation  → Single pass + Map   → O(n)
Group detection       → Frequency map       → O(n)
Battleship shots      → HashSet coords      → O(1)
```

**Critical rule**: Never nested-loop over game state per frame/turn.
Index everything with Map before the game loop starts.
