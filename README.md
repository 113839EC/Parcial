# Parcial — Generic Backend for 2D Board Game

REST backend with Spring Boot for a multiplayer turn-based 2D board game. Serves as a reusable base for any board game project: configurable board, coordinate movement, scoring system, items, exit, and BFS included.

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 | Main language |
| Spring Boot | 3.3.4 | Base framework (web, JPA, validation) |
| H2 Database | Runtime | In-memory database (no installation needed) |
| Spring Data JPA | — | Repositories, transactions, ORM |
| Lombok | — | Eliminates boilerplate (getters, builders, constructors) |
| Jackson | — | Serializes `int[][]` board to JSON in DB |
| SpringDoc OpenAPI | 2.6.0 | Automatic Swagger UI |
| Spring Validation | — | Request validation with annotations (`@NotBlank`, `@Min`) |
| JUnit 5 + Mockito + AssertJ | — | Service unit tests |

---

## Project Structure

```
src/main/java/org/example/
├── controller/
│   └── GameController.java        # 5 REST endpoints
├── service/
│   ├── GameService.java           # Game business logic
│   └── BoardService.java          # Board operations
├── model/
│   ├── entity/
│   │   ├── Game.java              # JPA entity: board, state, players
│   │   └── Player.java            # JPA entity: name, position, score
│   ├── dto/
│   │   ├── CreateGameRequest.java # Body to create game
│   │   ├── MoveRequest.java       # Body to move player
│   │   └── GameResponse.java      # Unified response with board + players
│   └── enums/
│       ├── GameStatus.java        # WAITING | IN_PROGRESS | FINISHED
│       └── CellType.java          # EMPTY(0) WALL(1) PLAYER(2) ITEM(3) EXIT(4)
├── repository/
│   ├── GameRepository.java
│   └── PlayerRepository.java
└── exception/
    ├── GameException.java         # Domain exception
    └── GlobalExceptionHandler.java # Global HTTP error handler
```

---

## REST API

### Create Game
```http
POST /api/games
Content-Type: application/json

{
  "width": 5,
  "height": 5,
  "playerName": "Emanuel"
}
```
Creates `width x height` board. Player appears at `(0,0)`, EXIT at `(height-1, width-1)`. Initial state: `WAITING`.

### Join Game
```http
POST /api/games/{id}/join?playerName=Juan
```
Second player joins. State changes to `IN_PROGRESS`. Accepts any number of players while in `WAITING`.

### Move Player
```http
POST /api/games/{id}/move
Content-Type: application/json

{
  "playerId": 1,
  "x": 1,
  "y": 0
}
```
Moves player to cell `(x, y)`. Validations: only the current turn's player can move, cell cannot be `WALL` or out of bounds.

### Get Game State
```http
GET /api/games/{id}
```

### List Open Games
```http
GET /api/games/open
```
Returns all games in `WAITING` state.

---

## Game Logic

### Lifecycle
```
WAITING → (second player joins) → IN_PROGRESS → (someone reaches EXIT) → FINISHED
```

### Turns
Circular rotation among all players. After each turn, `currentPlayerId` advances to the next in the list (`(idx + 1) % players.size()`).

### Score
| Event | Points |
|---|---|
| Pick up ITEM (cell 3) | +10 |
| Reach EXIT (cell 4) | +100 + game over |

### Board
- Represented as `int[][]` (board[row][column])
- Stored in DB as JSON string (column `boardJson`, length 10000)
- Serialized/deserialized by `BoardService` using Jackson

---

## Algorithms in BoardService

### BFS — Shortest Path
```java
boardService.shortestPath(board, new int[]{startX, startY}, new int[]{endX, endY})
// Returns List<int[]> with path, empty if no route
```
Standard BFS with 4 directions. Ignores `WALL` cells. Ready to use if pathfinding is required.

### Fisher-Yates Shuffle
```java
boardService.shuffle(deck)
// In-place shuffle of int[], uniform distribution O(n)
```
Useful for dealing cards, generating random boards, etc.

---

## Cell Types

```java
EMPTY  = 0   // free cell
WALL   = 1   // blocked, cannot step on
PLAYER = 2   // player position
ITEM   = 3   // collectible item (+10 pts)
EXIT   = 4   // game exit (+100 pts, ends game)
```

---

## Database

H2 in-memory, no installation needed. Data is lost on app restart (`create-drop`).

- **H2 Console:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:gamedb`
  - User: `sa` / Password: *(empty)*

---

## Interactive Documentation

Swagger UI available automatically on app startup:

```
http://localhost:8080/swagger-ui/index.html
```

Allows testing all endpoints without Postman.

---

## Tests

5 unit tests in `GameServiceTest` with Mockito:

| Test | What it verifies |
|---|---|
| `makeMove_validMove_updatesPlayerPosition` | Player position is updated |
| `makeMove_wrongTurn_throwsGameException` | Cannot move if not your turn |
| `makeMove_invalidCell_throwsGameException` | Cannot move to blocked cell |
| `makeMove_reachExit_setsFinishedAndAddsScore` | Reaching EXIT ends game and awards 100 pts |
| `getGame_nonExistentId_throwsGameException` | Non-existent ID throws correct exception |

Run tests:
```bash
mvn test
```

---

## Run the Project

```bash
mvn spring-boot:run
```

Requirements: Java 21, Maven 3.x. No external DB required.

---

## Claude Code — Installed Skills and Agents

This project has Claude Code skills installed in `.agents/skills/`. They activate automatically when using Claude Code in this directory.

### Installed Skills

| Skill | Source | Command | Purpose |
|---|---|---|---|
| `caveman` | `juliusbrussee/caveman` | `/caveman` | Ultra-compressed responses ~75% fewer tokens. Levels: `lite`, `full`, `ultra` |
| `find-skills` | `vercel-labs/skills` | `/find-skills` | Finds and installs skills from the agent ecosystem via `npx skills` |
| `game-development` | `sickn33/antigravity-awesome-skills` | `/game-development` | Game development orchestrator. Routes to specific skills based on platform |
| `java-springboot` | `github/awesome-copilot` | `/java-springboot` | Best practices for developing Spring Boot applications in Java |
| `create-spring-boot-java-project` | `github/awesome-copilot` | `/create-spring-boot-java-project` | Generates complete Spring Boot project skeleton with Java |
| `unit-test-service-layer` | `giuseppe-trisciuoglio/developer-kit` | `/unit-test-service-layer` | Service layer tests with JUnit 5 + Mockito — applies directly to `GameService` |
| `nodejs-backend-patterns` | `wshobson/agents` | `/nodejs-backend-patterns` | Node.js backend patterns (Express/Fastify): middleware, auth, error handling, REST, WebSockets |

### How to Use Skills

```bash
# Activate caveman mode (short responses, no filler)
/caveman

# With specific level
/caveman lite     # full sentences but no filler
/caveman ultra    # maximum compression, abbreviations

# Find a skill for a task
/find-skills      # and describe what you need
```

### Manage Skills with CLI

```bash
# Find available skills
npx skills find <query>

# Install a skill
npx skills add <owner/repo@skill>

# View installed skills / updates
npx skills check
npx skills update
```

Skills are stored in `.agents/skills/` and the lock file in `skills-lock.json`. Both are versioned in git so anyone who clones the repo has the same skills available.

---

## How to Extend for Your Project

1. **Add cell type**: add value to `CellType` enum
2. **New movement rule**: modify `GameService.makeMove()`
3. **More board algorithms**: add methods to `BoardService`
4. **Persist data between restarts**: switch H2 to PostgreSQL in `application.properties`
5. **Add cards/deck**: use `boardService.shuffle()` on an array of card IDs
